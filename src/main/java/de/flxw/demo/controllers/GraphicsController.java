package de.flxw.demo.controllers;

import de.flxw.demo.data.GraphicsData;
import de.flxw.demo.repositories.PhotoRepository;
import de.flxw.demo.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GraphicsController {
    @Autowired
    private PhotoService photoService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/v1/timeline")
    public List<GraphicsData> getTimeline() {
        return photoService.getAll();
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
