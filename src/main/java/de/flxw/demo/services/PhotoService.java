package de.flxw.demo.services;

import de.flxw.demo.data.GraphicsData;
import de.flxw.demo.data.TimelineEntry;
import de.flxw.demo.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    public Map<Date, List<TimelineEntry.Metadata>> getTimelineIds() {
        List<TimelineEntry> ret =  photoRepository.getTimelineIds();

        return ret.stream()
                .collect(Collectors.toMap(TimelineEntry::getDate,
                                          TimelineEntry::getMetadata,
                                          (v1,v2)->v1,
                                          LinkedHashMap::new));
    }

    public byte[] getThumbnail(Long id) {
        return photoRepository.getThumbnailImageById(id);
    }

    public String getFileNameForId(Long id) {
        Optional<GraphicsData> eventualEntry = photoRepository.findById(id);

        return eventualEntry.map(GraphicsData::getFileName).orElse(null);
    }

    public List<GraphicsData> getAll() {
        return photoRepository.findAll(Sort.by("timeStamp"));
    }
}
