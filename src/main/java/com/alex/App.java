package com.alex;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            ImageHandler firstImageHandler = new ImageHandler("1.bmp");
            ImageHandler secondImageHandler = new ImageHandler("2.bmp");
            int[][] firstImageMatrix = firstImageHandler.getImageMatrix();
            int[][] secondImageMatrix = secondImageHandler.getImageMatrix();

            int[] firstVector = firstImageHandler.getVectors();

            LearningMatrixHandler matrixHandler = new LearningMatrixHandler(firstVector, 70, 70);
            matrixHandler.setMatrixList(Arrays.asList(firstImageMatrix, secondImageMatrix));


            System.out.println("COUNT DIFFERENCES = " + matrixHandler.getDifference());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showReferenceVector(List<boolean[]> referenceVectors) {
        for (boolean[] referenceVector : referenceVectors) {
            for (int i = 0; i < referenceVector.length; i++) {
                System.out.print((referenceVector[i] ? 1 : 0) + " ");
            }
            System.out.println();
        }
    }

    private static void showLearningMatrix(boolean[][] secondLearningMatrix) {
        for (int i = 0; i < secondLearningMatrix.length; i++) {
            for (int j = 0; j < secondLearningMatrix[0].length; j++) {
                System.out.print((secondLearningMatrix[i][j] ? 1 : 0) + " ");
            }
            System.out.println();
        }
    }
}
