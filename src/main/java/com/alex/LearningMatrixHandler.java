package com.alex;

import java.util.ArrayList;
import java.util.List;

public class LearningMatrixHandler {
    private int[] upEdge;
    private int[] downEdge;
    private int upLimit;
    private int downLimit;
    private List<Image> imageList;

    public LearningMatrixHandler(int[] mainVector, int upLimit, int downLimit) {
        this.upLimit = upLimit;
        this.downLimit = downLimit;
        this.upEdge = getUpEdge(mainVector);
        this.downEdge = getDownEdge(mainVector);
        imageList = new ArrayList<>();
    }

    public void setMatrixList(List<Image> matrixList) {
        for (Image image : matrixList) {
            image.setLearningMatrix(getLearningMatrix(image.getImageMatrix()));
            imageList.add(image);
        }

        setVectorList();
        calculateDistance();
        calculateElementsInRadius();
        calculateD1();
        calculateByFormulas();
    }

    public boolean[][] getLearningMatrix(int[][] imageMatrix) {
        boolean[][] matrix = new boolean[imageMatrix.length][imageMatrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = (imageMatrix[i][j] < upEdge[i] && imageMatrix[i][j] > downEdge[i]);
            }
        }

        return matrix;
    }

    public List<Integer> getIntersectionRadius() {
        List<Integer> radius = new ArrayList<>();

        for (Image image : imageList) {
            List<int[]> countElementsInRadius = image.getCountElementsInRadius();
            int[] vector = countElementsInRadius.get(0);
            int i;

            for (i = 0; i < vector.length; i++) {
                if (compareValueWithList(i, vector.length, countElementsInRadius)) {
                    break;
                }
            }

            radius.add(i);
        }

        return radius;
    }

    private void calculateElementsInRadius() {
        for (Image image : imageList) {
            for (int[] distance : image.getDistance()) {
                int[] elementsInRadius = new int[distance.length];

                for (int i = 0; i < distance.length; i++) {
                    int count = 0;
                    for (int j = 0; j < distance.length; j++) {
                        if (distance[j] <= i) {
                            count++;
                        }
                    }
                    elementsInRadius[i] = count;
                }
                image.addRadiusElem(elementsInRadius);
            }
        }
    }

    private void calculateD1() {
        for (int i = 0; i < imageList.size(); i++) {
            Image image = imageList.get(i);
            List<int[]> elemsInRadius = image.getCountElementsInRadius();

            for (int j = 0; j < elemsInRadius.size(); j++) {
                int[] elems = elemsInRadius.get(j);
                double[] d = new double[elems.length];
                double[] ab = new double[elems.length]; // alpha and beta

                for (int k = 0; k < elems.length; k++) {
                    if (i == j) {
                        d[k] = roundAvoid((double) elems[k] / (double) elems.length);
                        ab[k] = roundAvoid(1 - ((double) elems[k] / (double) elems.length));
                    } else {
                        d[k] = roundAvoid(1 - ((double) elems[k] / (double) elems.length));
                        ab[k] = roundAvoid((double) elems[k] / (double) elems.length);
                    }
                }

                image.addD1(d);
                image.addAb(ab);
            }
        }
    }

    private void calculateByFormulas() {
        for (int i = 0; i < imageList.size(); i++) {
            Image image = imageList.get(i);
            List<double[]> alphaAndBettaList = image.getAlphaAndBetaList();
            List<double[]> dList = image.getD1List();
            double[] alpha = null;
            double[] betta = null;
            double[] d1 = null;
            double[] d2 = null;

            for (int j = 0; j < dList.size(); j++) {
                if (j == i) {
                    alpha = alphaAndBettaList.get(j);
                    d1 = dList.get(j);
                } else {
                    betta = alphaAndBettaList.get(j);
                    d2 = dList.get(j);
                }
            }

            image.setChars(alpha, betta, d1, d2,
                    calculateByKulbakFormula(alpha, betta, d1, d2),
                    calculateByShenonFormula(alpha, betta, d1, d2));
        }
    }

    private double[] calculateByKulbakFormula(double[] alpha, double[] betta, double[] d1, double[] d2) {
        double[] e = new double[alpha.length];

        for (int i = 0; i < alpha.length; i++) {
            if (d1[i] > 0.5 && d2[i] > 0.5) {
                e[i] = (Math.log((2 - (alpha[i] + betta[i])) / (alpha[i] + betta[i])) / Math.log(2)) * (1 - (alpha[i] + betta[i]));
            } else {
                e[i] = 0;
            }
        }

        return e;
    }

    private double[] calculateByShenonFormula(double[] alpha, double[] betta, double[] d1, double[] d2) {
        double[] e = new double[alpha.length];

        for (int i = 0; i < alpha.length; i++) {
            if (d1[i] > 0.5 && d2[i] > 0.5) {
                double alphaResult = alpha[i] / (alpha[i] + d2[i]);
                double bettaResult = betta[i] / (betta[i] + d1[i]);
                double d1Result = d1[i] / (d1[i] + betta[i]);
                double d2Result = d2[i] / (d2[i] + alpha[i]);

                e[i] = 1 + 0.5 * ((alphaResult * log2(alphaResult)) + (bettaResult * log2(bettaResult)) + (d1Result * log2(d1Result)) + (d2Result * (log2(d2Result))));
            } else {
                e[i] = 0;
            }
        }

        return e;
    }

    private void show(String name, double[] arr) {
        System.out.println("============ " + name + " ================");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }

        System.out.println();
    }


    private double log2(double n) {
        return n == 0 ? 0 : Math.log(n) / Math.log(2);
    }

    private double roundAvoid(double value) {
        double scale = Math.pow(10, 3);
        return Math.round(value * scale) / scale;
    }


    private void calculateDistance() {
        List<boolean[]> vectors = new ArrayList<>();

        for (Image image : imageList) {
            vectors.add(image.getReferenceVector());
        }

        for (Image image : imageList) {
            for (boolean[] vector : vectors) {
                int[] distance = calculateDistance(image.getLearningMatrix(), vector);
                image.addDistance(distance);
            }
        }
    }

    private int[] calculateDistance(boolean[][] matrix, boolean[] vector) {
        int[] distance = new int[matrix[0].length];
        int countDifferences;

        for (int i = 0; i < matrix.length; i++) {
            countDifferences = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] != vector[j]) {
                    countDifferences++;
                }
            }
            distance[i] = countDifferences;
        }

        return distance;
    }

    private void setVectorList() {
        for (Image image : imageList) {
            image.setReferenceVector(getReferenceVector(image.getLearningMatrix()));
        }
    }

    public int getDifference() {
        int countDifference = 0;
        boolean[] vector = imageList.get(0).getReferenceVector();

        for (int j = 0; j < vector.length; j++) {
            boolean value = vector[j];
            if (!compareVectorValueWithList(j, value)) {
                countDifference++;
            }
        }

        return countDifference;
    }

    private boolean[] getReferenceVector(boolean[][] learningMatrix) {
        int countZeros = 0;
        int countOnes = 0;
        int k = 0;
        boolean[] vector = new boolean[learningMatrix.length];

        for (int j = 0; j < learningMatrix[0].length; j++) {

            for (int i = 0; i < learningMatrix.length; i++) {
                if (learningMatrix[j][i]) {
                    countOnes++;
                } else {
                    countZeros++;
                }
            }

            vector[k] = countOnes > countZeros;

            k++;
        }

        return vector;
    }

    private int[] getUpEdge(int[] vector) {
        int[] upLimits = new int[vector.length];

        for (int i = 0; i < vector.length; i++) {
            upLimits[i] = vector[i] + upLimit;
        }

        return upLimits;
    }


    private int[] getDownEdge(int[] vector) {
        int[] downLimits = new int[vector.length];

        for (int i = 0; i < vector.length; i++) {
            downLimits[i] = vector[i] - downLimit;
        }

        return downLimits;
    }

    private boolean compareVectorValueWithList(int index, boolean value) {
        for (int i = 1; i < imageList.size(); i++) {
            boolean[] vector = imageList.get(i).getReferenceVector();

            if (vector[index] != value) {
                return false;
            }
        }

        return true;
    }

    private boolean compareValueWithList(int index, int value, List<int[]> list) {
        for (int[] vector : list) {
            if (vector[index] != value) {
                return false;
            }
        }

        return true;
    }
}
