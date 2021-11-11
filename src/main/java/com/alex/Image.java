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
    private List<double[]> d1List;
    private List<double[]> alphaAndBetaList;
    private Chars chars;

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

    public void addD1(double[] d1) {
        if (d1List == null) {
            d1List = new ArrayList<>();
        }

        d1List.add(d1);
    }

    public List<double[]> getD1List() {
        if (d1List.size() == 0) {
            throw new NullPointerException();
        }

        return d1List;
    }

    public void addAb(double[] ab) {
        if (alphaAndBetaList == null) {
            alphaAndBetaList = new ArrayList<>();
        }

        alphaAndBetaList.add(ab);
    }

    public List<double[]> getAlphaAndBetaList() {
        if (alphaAndBetaList.size() == 0) {
            throw new NullPointerException();
        }

        return alphaAndBetaList;
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

    public void setChars(double[] alpha, double[] betta, double[] d1, double[] d2, double[] kulbak, double[] chenon) {
        this.chars = new Chars(alpha, betta, d1, d2, kulbak, chenon);
    }

    public Chars getChars() {
        if (chars == null) {
            throw new NullPointerException();
        }

        return chars;
    }

    public class Chars {
        private double[] alpha;
        private double[] betta;
        private double[] d1;
        private double[] d2;
        private double[] kulbak;
        private double[] chenon;

        public Chars(double[] alpha, double[] betta, double[] d1, double[] d2, double[] kulbak, double[] chenon) {
            this.alpha = alpha;
            this.betta = betta;
            this.d1 = d1;
            this.d2 = d2;
            this.kulbak = kulbak;
            this.chenon = chenon;
        }

        public double[] getD1() {
            return d1;
        }

        public double[] getD2() {
            return d2;
        }

        public double[] getAlpha() {
            return alpha;
        }

        public double[] getBetta() {
            return betta;
        }

        public double[] getChenon() {
            return chenon;
        }

        public double[] getKulbak() {
            return kulbak;
        }
    }
}
