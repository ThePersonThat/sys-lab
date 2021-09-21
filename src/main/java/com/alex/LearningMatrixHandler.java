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
            if (!compareValueWithList(j, value)) {
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

            for (boolean[] matrix : learningMatrix) {
                if (matrix[j]) {
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

    private boolean compareValueWithList(int index, boolean value) {
        for (int i = 1; i < imageList.size(); i++) {
            boolean[] vector = imageList.get(i).getReferenceVector();

            if (vector[index] != value) {
                return false;
            }
        }

        return true;
    }
}
