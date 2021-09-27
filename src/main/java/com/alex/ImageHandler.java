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

    public static BufferedImage imageFromLearningMatrix(Image image) {
        int width = (int)image.getImage().getWidth();
        int height = (int)image.getImage().getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        boolean[][] matrix = image.getLearningMatrix();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = 0;

                if (!matrix[i][j]) {
                    color = 0xffffff;
                }

                bufferedImage.setRGB(i, j, color);
            }
        }

        return bufferedImage;
    }

    public static BufferedImage imageFromVector(Image image) {
        boolean[] vector = image.getReferenceVector();
        int width = vector.length;
        int height = 30;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = 0;

                if (!vector[i]) {
                    color = 0xffffff;
                }

                bufferedImage.setRGB(i, j, color);
            }
        }

        return bufferedImage;
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
