package com.alex;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Image {
    private int[][] imageMatrix;
    private int[] vector;
    private String filename;
    private boolean[] referenceVector;
    private boolean[][] learningMatrix;
    private javafx.scene.image.Image image;
    private List<int[]> distances;
    private List<int[]> countElementsInRadius;

    public Image(String filename) {
        this.filename = filename;
    }

    public void load() throws IOException {
        ImageHandler imageHandler = new ImageHandler(filename);
        imageMatrix = imageHandler.getImageMatrix();
        vector = imageHandler.getVectors();
        loadImage();
    }

    public void setLearningMatrix(boolean[][] learningMatrix) {
        this.learningMatrix = learningMatrix;
    }

    public void setReferenceVector(boolean[] referenceVector) {
        this.referenceVector = referenceVector;
    }

    public boolean[][] getLearningMatrix() {
        if (learningMatrix == null) {
            throw new NullPointerException();
        }
        return learningMatrix;
    }

    public boolean[] getReferenceVector() {
        if (referenceVector == null) {
            throw new NullPointerException();
        }

        return referenceVector;
    }


    public void addDistance(int[] distance) {
        if (distances == null) {
            distances = new ArrayList<>();
        }

        distances.add(distance);
    }

    public List<int[]> getDistance() {
        if (distances.size() == 0) {
            throw new NullPointerException();
        }

        return distances;
    }

    public void addRadiusElem(int[] elems) {
        if (countElementsInRadius == null) {
            countElementsInRadius = new ArrayList<>();
        }

        countElementsInRadius.add(elems);
    }

    public List<int[]> getCountElementsInRadius() {
        if (countElementsInRadius.size() == 0) {
            throw new NullPointerException();
        }

        return countElementsInRadius;
    }

    public int[] getVector() {
        return vector;
    }

    public int[][] getImageMatrix() {
        return imageMatrix;
    }

    public String getFilename() {
        return filename;
    }

    public javafx.scene.image.Image getImage() {
        return image;
    }

    public void loadImage() {
        try {
            String url = new File(filename).toURI().toURL().toString();
            image = new javafx.scene.image.Image(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
