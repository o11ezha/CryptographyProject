package com.example.goofyconverter.Backend;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Chartlogic {

    public int[] createChart(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        double[][] a = convertImageToArray(image);

        int n = a[0].length;
        int[] histogram = new int[256];

        for (double[] doubles : a) {
            for (int j = 0; j < n; j++) {
                int intensity = (int) doubles[j];
                histogram[intensity]++;
            }
        }
        return histogram;
    }

    public static double getIntensity(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return 0.299 * r + 0.587 * g + 0.114 * b;
    }

    public static double[][] convertImageToArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] pixels = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                Color color = new Color(image.getRGB(col, row));
                pixels[col][row] = getIntensity(color);
            }
        }
        return pixels;
    }
}
