package de.flxw.demo.data;

import lombok.Getter;

import java.io.Serializable;

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
                ", valid=" + valid +
                '}';
    }
}
