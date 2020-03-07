package de.flxw.demo.controllers;

import de.flxw.demo.data.GraphicsDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/graphics")
public class GraphicsController {
    private GraphicsDatabase graphicsDatabase;

    @Autowired
    public GraphicsController(GraphicsDatabase graphicsDatabase) {
        this.graphicsDatabase = graphicsDatabase;
    }

    /*@GetMapping
    public List<String> getAllGraphics() {
        return graphicsDatabase.getAllGraphics();
    }*/
}
