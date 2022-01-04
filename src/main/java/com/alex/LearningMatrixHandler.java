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
        findNeighbor();
        calculateSK();
        calculateDistances();
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

    public void examAlgorithm(Image image) {
        boolean[][] learningMatrix = getLearningMatrix(image.getImageMatrix());
        double maxMu = 0;
        int index = -1;

        for (int i = 0; i < imageList.size(); i++) {
            Image img = imageList.get(i);
            boolean[] referenceVector = img.getReferenceVector();
            int[] ints = calculateDistance(learningMatrix, referenceVector);
            double[] mu = new double[ints.length];
            double averageMu = 0;
            int optRadius = getOptRadius(img);

            for (int j = 0; j < ints.length; j++) {
                mu[j] = 1 - (double) ints[j] / optRadius;
                averageMu += mu[j];
            }

            averageMu /= ints.length;

            System.out.println("index: " + i + " = " + averageMu);

            if (averageMu > maxMu) {
                maxMu = averageMu;
                index = i;
            }
        }

        if (maxMu > 0) {
            System.out.println("The image is related to " + (index + 1) + " class");
        } else {
            System.out.println("The image is unknown");
        }
    }

    private int getOptRadius(Image image) {
        double[] kfe = image.getChars().getKulbak();
        double max = 0;
        int index = -1;

        for (int i = 0; i < kfe.length; i++) {
            if (kfe[i] > max) {
                max = kfe[i];
                index = i;
            }
        }

        return index;
    }

    private void findNeighbor() {
        for (int i = 0; i < imageList.size(); i++) {
            Image image = imageList.get(i);
            int minDifference = 0;
            int indexNeighbor = -1;

            for (int j = 0; j < imageList.size(); j++) {
                int difference = compareDifferences(image, imageList.get(j));

                if (minDifference < difference) {
                    minDifference = difference;
                    indexNeighbor = j;
                }
            }

            if (indexNeighbor == -1) {
                for (int j = 0; j < imageList.size(); j++) {
                    if (j != i) {
                        indexNeighbor = j;
                        break;
                    }
                }
            }


            image.setNeighbor(indexNeighbor);
        }
    }

    private void calculateElementsInRadius() {
        for (Image image : imageList) {
            for (int[] distance : image.getSK()) {
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
                    if (j == 0) {
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
                if (j == 0) {
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
            e[i] = 0.5 * (log2((d1[i] + d2[i] + 0.1) / (alpha[i] + betta[i] + 0.1)) * ((d1[i] + d2[i]) - (alpha[i] + betta[i])));
        }

        return e;
    }

    private double[] calculateByShenonFormula(double[] alpha, double[] betta, double[] d1, double[] d2) {
        double[] e = new double[alpha.length];

        for (int i = 0; i < alpha.length; i++) {
            double alphaResult = alpha[i] == 0 ? 0 : (alpha[i] / (alpha[i] + d2[i])) * log2(alpha[i] / (alpha[i] + d2[i]));
            double bettaResult = betta[i] == 0 ? 0 : (betta[i] / (betta[i] + d1[i])) * log2(betta[i] / (betta[i] + d1[i]));
            double d1Result = d1[i] == 0 ? 0 : (d1[i] / (d1[i] + betta[i])) * log2(d1[i] / (d1[i] + betta[i]));
            double d2Result = d2[i] == 0 ? 0 : (d2[i] / (d2[i] + alpha[i])) * log2(d2[i] / (d2[i] + alpha[i]));

            e[i] = 1 + 0.5 * (alphaResult + bettaResult + d1Result + d2Result);
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
        return Math.log(n) / Math.log(2);
    }

    private double roundAvoid(double value) {
        double scale = Math.pow(10, 3);
        return Math.round(value * scale) / scale;
    }


    private void calculateSK() {
        for (Image image : imageList) {
            int indexNeighbor = image.getIndexNeighbor();
            Image neighbor = imageList.get(indexNeighbor);
            List<boolean[]> vectors = new ArrayList<>();

            vectors.add(image.getReferenceVector());
            vectors.add(neighbor.getReferenceVector());

            for (boolean[] vector : vectors) {
                int[] distance = calculateDistance(image.getLearningMatrix(), vector);
                image.addSK(distance);
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

    private boolean compareVectorValueWithList(int index, boolean value) {
        for (int i = 1; i < imageList.size(); i++) {
            boolean[] vector = imageList.get(i).getReferenceVector();

            if (vector[index] != value) {
                return false;
            }
        }

        return true;
    }

    private void calculateDistances() {
        for (int i = 0; i < imageList.size(); i++) {
            List<Integer> differences = new ArrayList<>();
            Image image = imageList.get(i);

            for (int j = 0; j < imageList.size(); j++) {
                if (i == j) {
                    differences.add(-1);
                } else {
                    int difference = compareDifferences(image, imageList.get(j));
                    differences.add(difference);
                }
            }

            image.setDistance(differences);
        }
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

    private int compareDifferences(Image one, Image second) {
        boolean[] vectorOne = one.getReferenceVector();
        boolean[] vectorTwo = second.getReferenceVector();
        int countDifferences = 0;

        for (int i = 0; i < vectorOne.length; i++) {
            if (vectorOne[i] != vectorTwo[i]) {
                countDifferences++;
            }
        }

        return countDifferences;
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
