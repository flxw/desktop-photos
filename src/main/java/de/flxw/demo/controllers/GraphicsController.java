package de.flxw.demo.controllers;

import de.flxw.demo.data.GraphicsDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/timeline")
public class GraphicsController {
    private GraphicsDatabase graphicsDatabase;

    @Autowired
    public GraphicsController(GraphicsDatabase graphicsDatabase) {
        this.graphicsDatabase = graphicsDatabase;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public Map<Date, List<String>> getTimeline() {
        Map<Date, List<String>> t = graphicsDatabase.getTimelineMap();
        return t;
    }
}
