package de.flxw.demo.data;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class GraphicsData implements Serializable {
    @JsonIgnore
    @Getter
    private String fileName;

    @Getter
    private int height;

    @Getter
    private int width;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    @Getter
    private Date timeStamp;

    @JsonIgnore
    private long size;

    @JsonIgnore
    public Date getDate() {
        Instant i = this.timeStamp.toInstant().truncatedTo(ChronoUnit.DAYS);
        return Date.from(i);
    }

    private GraphicsData() {};

    public static GraphicsData of(String fileName) {
        File f = new File(fileName);
        GraphicsData gd = new GraphicsData();

        gd.fileName = fileName;
        gd.timeStamp = getTimestamp(f);

        Dimension ds = getDimensions(f);
        gd.width = (int) ds.getWidth();
        gd.height = (int) ds.getHeight();
        gd.size = f.length();

        return gd;
    }

    private static Date getTimestamp(File file) {
        try {
            Metadata mt = JpegMetadataReader.readMetadata(file);
            Directory exif = mt.getFirstDirectoryOfType(ExifIFD0Directory.class);

            // extract time when the picture was taken
            String s = exif.getString(ExifIFD0Directory.TAG_DATETIME);
            SimpleDateFormat exifDateParser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            return exifDateParser.parse(s);
        } catch (Exception e) {}

        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return new Date(attr.creationTime().toMillis());
        } catch (Exception e) {}

        return new Date(0,0,0);
    }

    private static Dimension getDimensions(File file) {
        try(ImageInputStream in = ImageIO.createImageInputStream(file)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    reader.dispose();
                }
            }
        } catch (Exception e) { }

        return new Dimension(1337, 1337);
    }

    @Override
    public String toString() {
        return "GraphicsData{" +
                "fileName='" + fileName + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", timeStamp=" + timeStamp +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof GraphicsData) {
            GraphicsData o = (GraphicsData) obj;
            return o.fileName.equals(fileName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size);
    }

    public long getId() {
        return hashCode();
    }

    public static long getId(String fileName) {
        File f = new File(fileName);

        return Objects.hash(fileName, f.length());
    }
}
