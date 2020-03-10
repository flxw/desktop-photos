package de.flxw.demo.data;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

public class GraphicsData implements Serializable {
    @Getter private String fileName;
    @Getter private String checkSum;
    @Getter private String timeStamp;
    @Getter private boolean valid;

    public GraphicsData(String fileName, String checkSum, String timeStamp, boolean valid) {
        this.fileName = fileName;
        this.checkSum = checkSum;
        this.timeStamp = timeStamp;
        this.valid = valid;
    }

    @Override
    public String toString() {
        return fileName + "{checkSum='" + checkSum + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", valid=" + valid + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof GraphicsData) {
            GraphicsData o = (GraphicsData) obj;
            return o.getCheckSum().equals(checkSum) && o.getFileName().equals(fileName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }
}
