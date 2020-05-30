package de.flxw.demo.controllers;

import de.flxw.demo.data.TimelineEntry;
import de.flxw.demo.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class GraphicsController {
    @Autowired
    private PhotoService photoService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/v1/timeline")
    public Map<Date, List<TimelineEntry.Metadata>> getTimeline() {
        return photoService.getTimelineIds();
    }

    @GetMapping("/graphics")
    public ResponseEntity<byte[]> getImage(@RequestParam Long id) {
        byte[] thumbnailBytes = photoService.getThumbnail(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(thumbnailBytes);
    }
}
