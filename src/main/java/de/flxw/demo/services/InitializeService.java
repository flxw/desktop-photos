package de.flxw.demo.services;

import de.flxw.demo.Configuration;
import de.flxw.demo.data.GraphicsData;
import de.flxw.demo.repositories.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InitializeService implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(InitializeService.class);

    @Autowired
    PhotoRepository photoRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        long startTime = System.currentTimeMillis();

        // build up filetree for diffing
        String pwd = (new File("")).getAbsolutePath();
        Map<Long, String> currentIdNameMapping;

        try {
            Stream<Path> walk = Files.find(Paths.get(pwd), Integer.MAX_VALUE, this::checkFileValidity);
            currentIdNameMapping = walk
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toMap(GraphicsData::getId, Function.identity()));
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("FATAL! Can't scan the current directory!");
            return;
        }

        Set<Long> currentGraphicsIds = currentIdNameMapping.keySet();

        int nChanged = 0;
        nChanged  = removeOldEntriesFromDb(currentGraphicsIds);
        nChanged += addNewEntriesToDb(currentGraphicsIds, currentIdNameMapping);

        long endTime = System.currentTimeMillis();
        LOG.info("Database recovery and update took " + (endTime - startTime) + "ms");
    }

    private int removeOldEntriesFromDb(final Set<Long> current) {
        Set<Long> recoveredIds = photoRepository.getAllIds();
        List<Long> toBeRemoved = recoveredIds
                                        .stream()
                                        .filter(x -> !current.contains(x))
                                        .collect(Collectors.toList());

        if (toBeRemoved.size() != 0) {
            photoRepository.deleteInBatchById(toBeRemoved);
        }

        return toBeRemoved.size();
    }

    private int addNewEntriesToDb(final Set<Long> current, final Map<Long, String> mapping) {
        List<String> pathsOfObjectstoBeAdded = current.stream()
                .filter(x -> !photoRepository.existsById(x))
                .map(mapping::get)
                .collect(Collectors.toList());

        pathsOfObjectstoBeAdded
                .stream()
                .map(GraphicsData::of)
                .map(photoRepository::save)
                .collect(Collectors.toList());

        return pathsOfObjectstoBeAdded.size();
    }

    private boolean checkFileValidity(Path fileName, BasicFileAttributes attrs) {
        boolean isAcceptedFile = Arrays
                .stream(Configuration.getSupportedFileExtensions())
                .anyMatch((ext) -> fileName.toString().toLowerCase().endsWith(ext));

        boolean isContainedInAppDirectory = fileName.toAbsolutePath().toString().contains(Configuration.getAppDir());

        return isAcceptedFile && !isContainedInAppDirectory;
    }

    /*public void commitToDb() {
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



    public Map<Long, GraphicsData> createGraphicsMapFromPathstrings(List<String> pathList) {
        return pathList
                .parallelStream()
                .map(GraphicsData::of)
                .collect(Collectors.toMap(
                        GraphicsData::getId,
                        Function.identity()
                ));
    }

    //REUSEDB worker


     */
}
