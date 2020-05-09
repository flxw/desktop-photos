package de.flxw.demo.data;

import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelineEntry {
    @Getter
    private Date date;

    @Getter
    private List<Metadata> metadata;

    public TimelineEntry(Date date, String[] rawMetadataEntries) {
        this.date = date;
        this.metadata = new ArrayList<Metadata>(rawMetadataEntries.length);

        for (String rawEntry : rawMetadataEntries) {
            String[] splitParameters = rawEntry.split(";");
            assert(splitParameters.length == 3);

            Long id = Long.parseLong(splitParameters[0]);
            Integer height = Integer.parseInt(splitParameters[1]);
            Integer width = Integer.parseInt(splitParameters[2]);

            Metadata md = new Metadata(id, height, width);
            this.metadata.add(md);
        }
    }

    @Value
    public class Metadata {
        Long id;
        Integer height;
        Integer width;
    }
}

