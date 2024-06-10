package com.example.goofyconverter.Backend;

import java.awt.*;
import java.util.List;

public class ResultOfExtraction {

    private byte[] extractedMessage;
    private java.util.List<Point> pixelCoordinates;

    public ResultOfExtraction(byte[] message, java.util.List<Point> pixelCoordinates) {
        this.extractedMessage = message;
        this.pixelCoordinates = pixelCoordinates;
    }

    public byte[] getMessage() {
        return extractedMessage;
    }

    public List<Point> getPixelCoordinates() {
        return pixelCoordinates;
    }

}
