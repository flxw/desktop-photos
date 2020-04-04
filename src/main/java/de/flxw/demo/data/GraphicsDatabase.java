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
    protected Map<Long, GraphicsData> db;
    @Getter protected SortedMap<Date, List<GraphicsData>> timelineMap;
    private final Logger LOG = LoggerFactory.getLogger(GraphicsDatabase.class);
    private Thread dbWorker;

    public GraphicsDatabase() {
    }

    @PostConstruct
    public void init() {
        File dbFile = new File(Configuration.getDbLocation());

        if (dbFile.exists()) {
            LOG.info("Found a database file - updating and reusing it");
            dbWorker = new ReuseDbWorker();
        } else {
            LOG.info("No database found - populating and creating a new one");
            Configuration.createAppDirIfNonExistent();
            dbWorker = new InitDbWorker();
        }

        dbWorker.start();
    }

    public GraphicsData getById(final long id) {
        return this.db.get(id);
    }

    private abstract class DbWorker extends Thread {
        public void commitToDb() {
            try {
                FileOutputStream fos = new FileOutputStream(Configuration.getDbLocation());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(db);
                oos.close();
                LOG.info("Committed update database to " + Configuration.getDbLocation());
            } catch (IOException e) {
                LOG.error("Could not save database to " + Configuration.getDbLocation());
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
                    .stream(Configuration.getSupportedFileExtensions())
                    .anyMatch(entry -> fileName.toLowerCase().endsWith(entry));
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
                        .collect(Collectors.toMap(
                                GraphicsData::getId,
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
                fis = new FileInputStream(Configuration.getDbLocation());
                ObjectInputStream ois = new ObjectInputStream(fis);
                db = (Map<Long, GraphicsData>) ois.readObject();

                ois.close();
            } catch (Exception e) {
                LOG.error("FATAL! Reusing the existing db failed! Delete it to fix the issue.");
                e.printStackTrace();
                return;
            }

            // build up filetree for diffing
            String pwd = (new File("")).getAbsolutePath();
            Map<Long, String> currentIdNameMapping;

            try (Stream<Path> walk = Files.walk(Paths.get(pwd))) {
                currentIdNameMapping = walk
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .filter(this::isImage)
                        .collect(Collectors.toMap(GraphicsData::getId, Function.identity()));
            } catch (IOException e) {
                e.printStackTrace();
                LOG.error("FATAL! Can't scan the current directory!");
                return;
            }

            Set<Long> recoveredGraphicsIds = db.keySet();
            Set<Long> currentGraphicsIds = currentIdNameMapping.keySet();

            int nChanged = 0;
            nChanged  = removeOldEntriesFromDb(currentGraphicsIds, recoveredGraphicsIds);
            nChanged += addNewEntriesToDb(currentGraphicsIds, recoveredGraphicsIds, currentIdNameMapping);

            if (nChanged > 0) commitToDb();
            constructTimeline();

            long endTime = System.currentTimeMillis();
            LOG.info("Database recovery and update took " + (endTime - startTime) + "ms");
        }

        private int removeOldEntriesFromDb(final Set<Long> current, final Set<Long> recovered) {
            List<Long> toBeRemoved = recovered.stream()
                    .filter(x -> !current.contains(x))
                    .collect(Collectors.toList());

            for (Long graphicsId : toBeRemoved) {
                db.get(graphicsId).cleanup();
            }

            db.keySet().removeAll(toBeRemoved);

            return toBeRemoved.size();
        }

        private int addNewEntriesToDb(final Set<Long> current, final Set<Long> recovered, final Map<Long, String> mapping) {
            Map<Long, GraphicsData> toBeAdded = current.stream()
                    .filter(x -> !recovered.contains(x))
                    .map(mapping::get)
                    .map(GraphicsData::of)
                    .collect(Collectors.toMap(
                            GraphicsData::getId,
                            Function.identity()
                    ));

            db.putAll(toBeAdded);

            return toBeAdded.size();
        }
    }
}
