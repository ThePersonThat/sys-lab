package com.alex;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class ImageHandler {
    private int height;
    private int width;
    private int[][] imageMatrix;
    private int[] vector;

    public ImageHandler(String filename) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));
        height = image.getHeight();
        width = image.getWidth();
        imageMatrix = getImageBytes(image);
        vector = getVerticalVectors();
    }
    public int[] getVectors() {
        return vector;
    }

    public int[][] getImageMatrix() {
        return imageMatrix;
    }


    private int[][] getImageBytes(BufferedImage image) {
        byte[] bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int[][] imageBytes = new int[image.getHeight()][image.getWidth()];
        int k = 0;

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                imageBytes[i][j] = bytes[k] & 0xFF;
                k++;
            }
        }

        return imageBytes;
    }

    private int[] getVerticalVectors() {
        int[] vectors = new int[width];
        int k = 0;

        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                vectors[k] += imageMatrix[i][j];
            }

            vectors[k] /= height;

            k++;
        }

        return vectors;
    }
}
