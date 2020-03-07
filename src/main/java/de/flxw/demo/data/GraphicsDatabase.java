package de.flxw.demo.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class GraphicsDatabase {
    private ArrayList<String> db;
    private static final Logger LOG =
            LoggerFactory.getLogger(GraphicsDatabase.class);

    public GraphicsDatabase() {
    }

    @PostConstruct
    public void refreshAfterStartup() {
        LOG.info("Yeah, hello postconstruct event");
        /* spawn a thread here after the application has fully gone up.
           the thread should read in the DB file, and update its contents
           if the file did not exist, the thread should create it in the first place
         */
        /*try (Stream<Path> walk = Files.walk(Paths.get("/Users/f.wolff/Pictures"))) {
            // We want to find only regular files
            this.db = walk
                    .filter(Files::isRegularFile)
                    .map(x -> x.toString())
                    .filter(x -> x.endsWith("png"))
                    .collect(Collectors.toList());

            this.db.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public List<String> getAllGraphics() {
        return db;
    }
}
