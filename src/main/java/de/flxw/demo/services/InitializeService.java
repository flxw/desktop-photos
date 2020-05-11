package de.flxw.demo.services;

import de.flxw.demo.configuration.Configuration;
import de.flxw.demo.data.GraphicsData;
import de.flxw.demo.repositories.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InitializeService extends Thread implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(InitializeService.class);

    @Autowired
    PhotoRepository photoRepository;

    @Override
    public void afterPropertiesSet() {
        this.start();
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
                .parallelStream()
                .map(GraphicsData::of)
                .map(photoRepository::save)
                .collect(Collectors.toList());

        return pathsOfObjectstoBeAdded.size();
    }

    private void removeEntryFromDb(String fileName) {
        Long entryId = GraphicsData.getId(fileName);
        photoRepository.deleteById(entryId);
    }

    private void addEntryToDb(String fileName) {
        GraphicsData newEntry = GraphicsData.of(fileName);
        photoRepository.save(newEntry);
    }

    private boolean checkFileValidity(Path fileName, BasicFileAttributes attrs) {
        return isFileValid(fileName);
    }

    private boolean isFileValid(Path fileName) {
        boolean isAcceptedFile = Arrays
                .stream(Configuration.getSupportedFileExtensions())
                .anyMatch((ext) -> fileName.toString().toLowerCase().endsWith(ext));

        boolean isContainedInAppDirectory = fileName.toAbsolutePath().toString().contains(Configuration.getAppDir());

        return isAcceptedFile && !isContainedInAppDirectory;
    }

    private void runInitialDbCheck() {
        long startTime = System.currentTimeMillis();
        LOG.info("Starting database population...");

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

        int nRemoved = removeOldEntriesFromDb(currentGraphicsIds);
        int nAdded   = addNewEntriesToDb(currentGraphicsIds, currentIdNameMapping);

        long endTime = System.currentTimeMillis();
        LOG.info("Database recovery and update took " + (endTime - startTime) + "ms");
        LOG.info("Database entry diff: +" + nAdded + " -" + nRemoved);
    }

    private void watchFilesystemForChanges() {
        try {

            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(System.getProperty("user.dir"));

            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    String fileName = Paths.get(event.context().toString()).toAbsolutePath().toString();

                    if (!isFileValid(Paths.get(fileName))) continue;

                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
                        LOG.info("Photo added: " + fileName);
                        addEntryToDb(fileName);
                    } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                        LOG.info("Photo deleted: " + fileName);
                        removeEntryFromDb(fileName);
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
                        LOG.info("Photo updated: " + fileName);
                        removeEntryFromDb(fileName);
                        addEntryToDb(fileName);
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            // when watcher couldn't register
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        super.run();

        runInitialDbCheck();
        watchFilesystemForChanges();
    }
}
