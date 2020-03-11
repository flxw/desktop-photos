package de.flxw.demo.data;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import de.flxw.demo.Configuration;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class GraphicsDatabase {
    protected Map<String, GraphicsData> db;
    @Getter protected SortedMap<Date, List<String>> timelineMap;
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
            timelineMap = new TreeMap<Date, List<String>>(Collections.reverseOrder());

            for (GraphicsData gd : db.values()) {
                Date key = gd.getDate();

                if(timelineMap.containsKey(key)){
                    List<String> l = timelineMap.get(key);
                    l.add(gd.getFileName());
                } else {
                    List<String> l = new ArrayList<String>();
                    l.add(gd.getFileName());
                    timelineMap.put(gd.getDate(), l);
                }
            }
        }

        protected boolean isImage(String fileName) {
            return Arrays
                    .stream(Configuration.SUPPORTED_EXTENSIONS)
                    .anyMatch(entry -> fileName.endsWith(entry));
        }

        protected GraphicsData createGraphicsObject(String fileName) {
            File f = new File(fileName);

            String cs = generateChecksum(f);
            Date time = extractTimeTaken(f);
            boolean valid = !cs.equals("");

            return new GraphicsData(fileName, cs, time, valid);
        }

        private String generateChecksum(File file) {
            String cs;

            try {
                MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                cs = getFileChecksum(md5Digest, file);
            } catch (Exception e) {
                cs = "";
            }

            return cs;
        }

        private Date extractTimeTaken(File file) {
            try {
                Metadata mt = JpegMetadataReader.readMetadata(file);
                Directory exif = mt.getFirstDirectoryOfType(ExifIFD0Directory.class);
                String s = exif.getString(ExifIFD0Directory.TAG_DATETIME);
                SimpleDateFormat exifDateParser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date d = exifDateParser.parse(s);
                return d;
            } catch (Exception e) {}

            try {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                return new Date(attr.creationTime().toMillis());
            } catch (Exception e) {}

            return new Date(0,0,0);
        }

        private String getFileChecksum(MessageDigest digest, File file) throws IOException {
            //Get file input stream for reading the file content
            FileInputStream fis = new FileInputStream(file);

            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount;

            //Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            };

            //close the stream; We don't need it now.
            fis.close();

            //Get the hash's bytes
            byte[] bytes = digest.digest();

            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //return complete hash
            return sb.toString();
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
                        .map(this::createGraphicsObject)
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

            long endTime = System.currentTimeMillis();
            LOG.info("Database recovery and update took " + (endTime - startTime) + "ms");

            constructTimeline();
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
                    .map(this::createGraphicsObject)
                    .collect(Collectors.toMap(
                            GraphicsData::getFileName,
                            Function.identity()
                    ));

            db.putAll(toBeAdded);

            return toBeAdded.size();
        }
    }
}
