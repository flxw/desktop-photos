package de.flxw.demo.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class GraphicsDatabase {
    protected List<String> db;
    private final Logger LOG = LoggerFactory.getLogger(GraphicsDatabase.class);
    private final String DB_NAME = "imagedb.java.serialized";
    private final String[] supportedExtensions = {"png", "jpg"};

    private Thread dbWorker;

    public GraphicsDatabase() {
    }

    @PostConstruct
    public void init() {
        File dbFile = new File(DB_NAME);

        if (dbFile.exists()) {
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
        /**/
    }

    public List<String> getAllGraphics() {
        return db;
    }

    private class InitDbWorker extends Thread {
        public void run() {
            String pwd = (new File("")).getAbsolutePath();

            try (Stream<Path> walk = Files.walk(Paths.get(pwd))) {
                db = walk
                        .filter(Files::isRegularFile)
                        .map(x -> x.toString())
                        .filter(x -> this.isImage(x))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }

            LOG.info("InitDbWorker scanned files: " + db.size());
        }

        private boolean isImage(String fileName) {
            return Arrays.stream(supportedExtensions).anyMatch(entry -> fileName.endsWith(entry));
        }
    }

}
