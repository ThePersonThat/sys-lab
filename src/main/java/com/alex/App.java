package com.alex;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class App extends Application {
    public static final int height = 1050;
    public static final int width = 800;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = build();
        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pane build() {
        Image image = getImage("1.bmp");
        Image image2 = getImage("2.bmp");
        Image image3 = getImage("3.bmp");
        List<Image> images = Arrays.asList(image, image2, image3);

        List<Double> optimized = optimize();

        LearningMatrixHandler matrixHandler = new LearningMatrixHandler(image.getVector(),
                optimized.get(1).intValue(), optimized.get(1).intValue());

        matrixHandler.setMatrixList(images);

        WindowBuilder builder = new WindowBuilder(250);
        builder.addTab(image);
        builder.addTab(image2);

        List<Integer> differences = matrixHandler.getDifferences();
        int k = 0;
        /*for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++, k++) {
                System.out.println((i + 1) + " - " + (j + 1));
                System.out.println("Vectors: ");
                System.out.println(vectorToString(images.get(i).getReferenceVector()));
                System.out.println(vectorToString(images.get(j).getReferenceVector()));
                System.out.println("Difference: " + differences.get(k));
                System.out.println("=====================================");
            }
        }*/
//        builder.buildTable(images, matrixHandler.getDifference());

        return builder.getRootPane();
    }

    private String vectorToString(boolean[] vector) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < vector.length; i++) {
            builder.append(vector[i] ? 1 : 0).append(" ");
        }

        return builder.toString();
    }

    public Image getImage(String filename) {
        Image image = new Image(filename);
        try {
            image.load();
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error reading file: " + filename);
            System.exit(1);
        }

        return image;
    }

    private List<Double> optimize() {
        int deltaI = 0;
        double averageKfe;
        double maxAverageKfe = 0;

        for (int i = 0; i < 100; i++) {
                Image image = getImage("1.bmp");
                Image image2 = getImage("2.bmp");
                Image image3 = getImage("3.bmp");
                List<Image> images = Arrays.asList(image, image2, image3);
                LearningMatrixHandler matrixHandler = new LearningMatrixHandler(image.getVector(), i, i);
                matrixHandler.setMatrixList(images);
                averageKfe = 0;

                for (int k = 0; k < images.size(); k++) {
                    Image currentImage = images.get(k);
                    double[] d1 = currentImage.getChars().getD1();
                    double[] betta = currentImage.getChars().getBetta();
                    double[] kfe = currentImage.getChars().getKulbak();
                    double maxKfe = 0;
                    boolean wasFind = false;

                    for (int d = 0; d < kfe.length; d++) {
                        if (d1[d] > 0.5 && betta[d] < 0.5) {
                            if (kfe[d] > maxKfe) {
                                maxKfe = kfe[d];
                            }

                            wasFind = true;
                        }
                    }

                    if (!wasFind) {
                        for (int d = 0; d < kfe.length; d++) {
                            if (kfe[d] > maxKfe) {
                                maxKfe = kfe[d];
                            }
                        }
                    }

                    averageKfe += maxKfe;
                }

                averageKfe /= images.size();

                if (averageKfe > maxAverageKfe) {
                    maxAverageKfe = averageKfe;
                    deltaI = i;
                }
        }

        System.out.println("Max kfe = " + maxAverageKfe + " DeltaI = " + deltaI);
        return Arrays.asList(maxAverageKfe, (double) deltaI);
    }
}
