package de.flxw.demo.data;

import de.flxw.demo.Configuration;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class GraphicsDatabase {
    protected Map<String, GraphicsData> db;
    @Getter protected SortedMap<Date, List<GraphicsData>> timelineMap;
    private final Logger LOG = LoggerFactory.getLogger(GraphicsDatabase.class);
    private Thread dbWorker;

    public GraphicsDatabase() {
    }

    @PostConstruct
    public void init() {
        File dbFile = new File(Configuration.DB_NAME);

        if (dbFile.exists()) {
            LOG.info("Found a database file - updating and reusing it");
            dbWorker = new ReuseDbWorker();
        } else {
            LOG.info("No database found - populating and creating a new one");
            dbWorker = new InitDbWorker();
        }

        dbWorker.start();
    }

    private abstract class DbWorker extends Thread {
        public void commitToDb() {
            try {
                FileOutputStream fos = new FileOutputStream(Configuration.DB_NAME);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(db);
                oos.close();
                LOG.info("Committed update image database to " + Configuration.DB_NAME);
            } catch (IOException e) {
                LOG.error("Could not save the database to location " + Configuration.DB_NAME);
            }
        }

        public void constructTimeline() {
            timelineMap = new TreeMap<>(Collections.reverseOrder());

            for (GraphicsData gd : db.values()) {
                Date key = gd.getDate();

                if(timelineMap.containsKey(key)){
                    List<GraphicsData> l = timelineMap.get(key);
                    l.add(gd);
                } else {
                    List<GraphicsData> l = new ArrayList<>();
                    l.add(gd);
                    timelineMap.put(gd.getDate(), l);
                }
            }
        }

        protected boolean isImage(String fileName) {
            return Arrays
                    .stream(Configuration.SUPPORTED_EXTENSIONS)
                    .anyMatch(entry -> fileName.endsWith(entry));
        }
    }

    private class InitDbWorker extends DbWorker {
        public void run() {
            long startTime = System.currentTimeMillis();
            String pwd = (new File("")).getAbsolutePath();

            try (Stream<Path> walk = Files.walk(Paths.get(pwd))) {
                db = walk
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .filter(this::isImage)
                        .map(GraphicsData::of)
                        .filter(x -> x.isValid())
                        .collect(Collectors.toMap(
                                GraphicsData::getFileName,
                                Function.identity()
                        ));
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.commitToDb();
            this.constructTimeline();

            long endTime = System.currentTimeMillis();

            LOG.info("InitDbWorker scanned files: " + db.size());
            LOG.info("Database initialization took " + (endTime - startTime) + "ms");
        }
    }

    private class ReuseDbWorker extends DbWorker {
        public void run() {
            long startTime = System.currentTimeMillis();
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(Configuration.DB_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                db = (Map<String, GraphicsData>) ois.readObject();

                ois.close();
            } catch (Exception e) {
                LOG.error("FATAL! Reusing the existing db failed! Delete it to fix the issue.");
                e.printStackTrace();
                return;
            }

            // build up filetree for diffing
            String pwd = (new File("")).getAbsolutePath();
            Set<String> currentFilenames;

            try (Stream<Path> walk = Files.walk(Paths.get(pwd))) {
                currentFilenames = walk
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .filter(this::isImage)
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                e.printStackTrace();
                LOG.error("FATAL! Can't scan the current directory!");
                return;
            }

            Set<String> recoveredGraphicsNames = db.keySet();

            int nChanged = 0;
            nChanged  = removeOldEntriesFromDb(currentFilenames, recoveredGraphicsNames);
            nChanged += addNewEntriesToDb(currentFilenames, recoveredGraphicsNames);

            if (nChanged > 0) commitToDb();
            constructTimeline();

            long endTime = System.currentTimeMillis();
            LOG.info("Database recovery and update took " + (endTime - startTime) + "ms");
        }

        private int removeOldEntriesFromDb(final Set<String> currentFilenames, final Set<String> recoveredFilenames) {
            List<String> toBeRemoved = recoveredFilenames.stream()
                    .filter(x -> !currentFilenames.contains(x))
                    .collect(Collectors.toList());

            db.keySet().removeAll(toBeRemoved);

            return toBeRemoved.size();
        }

        private int addNewEntriesToDb(final Set<String> currentFilenames, final Set<String> recoveredFilenames) {
            Map<String, GraphicsData> toBeAdded = currentFilenames.stream()
                    .filter(x -> !recoveredFilenames.contains(x))
                    .map(GraphicsData::of)
                    .collect(Collectors.toMap(
                            GraphicsData::getFileName,
                            Function.identity()
                    ));

            db.putAll(toBeAdded);

            return toBeAdded.size();
        }
    }
}
