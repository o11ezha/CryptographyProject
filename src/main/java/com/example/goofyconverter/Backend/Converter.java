package com.example.goofyconverter.Backend;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class Converter {

    String outputFileName = "default";

    public String convertToJpeg(byte[] message, String path, String stegoKey) {
        BufferedImage img;

        try {
            img = ImageIO.read(new File(path));

            WritableRaster raster = img.getRaster();
            DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();

            byte[] writableBytes = buffer.getData();
            int offset = 0;

            int messageLength = message.length;
            for (int i = 0; i < 32; i++) {
                int bit = (messageLength >> (31 - i)) & 1;
                writableBytes[offset] = (byte) ((writableBytes[offset] & 0xFE) | bit);
                offset++;
            }

            for (int i = 0; i < message.length; i++) {
                byte b = message[i];
                for (int j = 0; j < 8; j++) {
                    int bit = (b >> (7 - j)) & 1;
                    writableBytes[offset] = (byte) ((writableBytes[offset] & 0xFE) | bit);
                    offset++;
                }

                Encryption encryption = new Encryption();
                byte[] nextKey = encryption.getHash(stegoKey + i);
                for (byte value : nextKey) {
                    offset = (offset + (value & 0xFF)) % writableBytes.length;
                }
            }

            String documentsDirectory = System.getProperty("user.home") + File.separator + "Documents";
            String fileName = new File(path).getName();

            String outputFileName = documentsDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf('.')) + ".png";
            File output = new File(outputFileName);
            ImageIO.write(img, "png", output);

            return outputFileName;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public ResultOfExtraction extractMessage(String imagePath, String stegoKey) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));

            WritableRaster raster = img.getRaster();
            DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();

            List<Point> pixelCoordinates = new ArrayList<>();
            byte[] readableBytes = buffer.getData();
            int offset = 0;

            int messageLength = 0;
            for (int i = 0; i < 32; i++) {
                messageLength = (messageLength << 1) | (readableBytes[offset] & 1);
                offset++;
            }

            byte[] message = new byte[messageLength];
            for (int i = 0; i < messageLength; i++) {
                byte b = 0;
                for (int j = 0; j < 8; j++) {
                    b = (byte) ((b << 1) | (readableBytes[offset] & 1));
                    offset++;
                }
                message[i] = b;

                Encryption encryption = new Encryption();
                byte[] nextKey = encryption.getHash(stegoKey + i);
                for (byte value : nextKey) {
                    offset = (offset + (value & 0xFF)) % readableBytes.length;
                }

                int x = (offset / 3) % img.getWidth();
                int y = (offset / 3) / img.getWidth();
                pixelCoordinates.add(new Point(x, y));
            }

            return new ResultOfExtraction(message, pixelCoordinates);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}