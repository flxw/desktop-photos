package de.flxw.demo.data;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.flxw.demo.Configuration;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @JsonIgnore  @Getter
    private String thumbnailLocation;

    private GraphicsData() {};

    public static GraphicsData of(String fileName) {
        File f = new File(fileName);
        BufferedImage bimg = null;
        GraphicsData gd = new GraphicsData();

        try {
            bimg = ImageIO.read(f);
        } catch (IOException e) {
            return null;
        }

        gd.fileName = fileName;
        gd.timeStamp = getTimestamp(f);
        gd.size = f.length();
        gd.width = (int) bimg.getWidth();
        gd.height = (int) bimg.getHeight();

        // account for image orientation in JPEG here
        int rotation = 0;
        boolean dimensionsFlipped = false;

        if (fileName.toLowerCase().endsWith("jpg")) {
            try {
                dimensionsFlipped = shouldFlipDimensions(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (dimensionsFlipped) {
            int x = gd.width;
            gd.width = gd.height;
            gd.height = x;
            rotation = 90;
        }

        try {
            String thumbFileName = Long.toString(gd.getId());
            gd.thumbnailLocation = Paths.get(Configuration.getAppDir(), thumbFileName + ".png").toString();
            File thumbnailFile = new File(gd.thumbnailLocation);
            Thumbnails
                    .of(bimg)
                    .rotate(rotation)
                    .size(200, 200)
                    .outputFormat("png")
                    .toFile(thumbnailFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

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

    private static boolean shouldFlipDimensions(File f) throws ImageProcessingException, IOException, MetadataException {
        Metadata metadata = ImageMetadataReader.readMetadata(f);
        Iterator<Directory> directoryIterator = metadata.getDirectories().iterator();

        for (Directory directory = directoryIterator.next();
             directoryIterator.hasNext();
             directory = directoryIterator.next()) {
            for (Tag tag : directory.getTags()) {
                if (tag.getTagName().equals("Orientation")) {
                    return directory.getInt(ExifSubIFDDirectory.TAG_ORIENTATION) >= 4;
                }
            }
        }

        return false;
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

    public void cleanup() {
        File toBeDeleted = new File(this.thumbnailLocation);
        toBeDeleted.delete();
    }
}
