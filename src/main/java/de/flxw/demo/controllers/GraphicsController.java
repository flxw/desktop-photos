package de.flxw.demo.controllers;

import de.flxw.demo.data.GraphicsData;
import de.flxw.demo.data.GraphicsDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class GraphicsController {
    private GraphicsDatabase graphicsDatabase;

    @Autowired
    public GraphicsController(GraphicsDatabase graphicsDatabase) {
        this.graphicsDatabase = graphicsDatabase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/v1/timeline")
    public Map<Date, List<GraphicsData>> getTimeline() {
        return graphicsDatabase.getTimelineMap();
    }

    @GetMapping("/graphics")
    public ResponseEntity<byte[]> getImage(@RequestParam Long id) throws IOException {
        String fileName = graphicsDatabase.getById(id).getFileName();
        File imgFile = new File(fileName);
        InputStream targetStream = new FileInputStream(imgFile);
        byte[] bytes = StreamUtils.copyToByteArray(targetStream);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }
}
