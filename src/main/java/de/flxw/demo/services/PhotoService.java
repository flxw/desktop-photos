package de.flxw.demo.services;

import de.flxw.demo.data.TimelineEntry;
import de.flxw.demo.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        byte[] b = photoRepository.getThumbnailImageById(id);
        return b;
    }

    public int getState() {
        return photoRepository.getDbEntryCount();
    }
}
