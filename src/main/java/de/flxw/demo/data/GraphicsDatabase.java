package de.flxw.demo.data;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import de.flxw.demo.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class GraphicsDatabase {
    protected List<GraphicsData> db;
    private final Logger LOG = LoggerFactory.getLogger(GraphicsDatabase.class);
    private Thread dbWorker;

    public GraphicsDatabase() {
    }

    @PostConstruct
    public void init() {
        File dbFile = new File(Configuration.DB_NAME);

        if (dbFile.exists()) {
            LOG.info("Reusing database files is not supported yet!");

            //dbWorker = new ReuseDbWorker();

            // read db file
            // create diff filetree
            // drop deleted
            // add new
            // update changed
        } else {
            LOG.info("No database found, populating and creating a new one");
            dbWorker = new InitDbWorker();
            dbWorker.run();
        }

        /* spawn a thread here after the application has fully gone up.
           the thread should read in the DB file, and update its contents
           if the file did not exist, the thread should create it in the first place
         */
    }

    private abstract class DbWorker extends Thread {
        public void commitToDb() {
            try {
                FileOutputStream fos = new FileOutputStream(Configuration.DB_NAME);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(db);
                oos.close();
            } catch (IOException e) {
                LOG.error("Could not save the database to location " + Configuration.DB_NAME);
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
            String time = extractTimeTaken(f);
            boolean valid = (cs != "");

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

        private String extractTimeTaken(File file) {
            try {
                Metadata mt = JpegMetadataReader.readMetadata(file);
                Directory exif = mt.getFirstDirectoryOfType(ExifIFD0Directory.class);
                return exif.getString(ExifIFD0Directory.TAG_DATETIME);
            } catch (Exception e) {
            }

            try {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                return attr.creationTime().toString();
            } catch (Exception ex) {
                return "";
            }
        }

        private String getFileChecksum(MessageDigest digest, File file) throws IOException {
            //Get file input stream for reading the file content
            FileInputStream fis = new FileInputStream(file);

            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

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
            String pwd = (new File("")).getAbsolutePath();

            try (Stream<Path> walk = Files.walk(Paths.get(pwd))) {
                db = walk
                        .filter(Files::isRegularFile)
                        .map(x -> x.toString())
                        .filter(this::isImage)
                        .map(this::createGraphicsObject)
                        .filter(x -> x.isValid())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }

            LOG.info("InitDbWorker scanned files: " + db.size());
            this.commitToDb();
            LOG.info("Committed update image database to " + Configuration.DB_NAME);
        }
    }
}
