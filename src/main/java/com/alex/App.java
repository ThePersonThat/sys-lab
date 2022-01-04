package com.alex;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
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
        String[] names = {
                "1.bmp", "2.bmp", "3.bmp"
        };

        List<Image> images = new ArrayList<>();

        for (String name : names) {
            images.add(getImage(name));
        }

        List<Double> optimized = optimize(names);

        LearningMatrixHandler matrixHandler = new LearningMatrixHandler(images.get(0).getVector(),
                optimized.get(1).intValue(), optimized.get(1).intValue());

        matrixHandler.setMatrixList(images);
        matrixHandler.examAlgorithm(getImage("4.bmp"));

        WindowBuilder builder = new WindowBuilder(250);

        for (int i = 0; i < images.size(); i++) {
            builder.addTab(images.get(i));
        }

        builder.buildTable(images);

        return builder.getRootPane();
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

    private List<Double> optimize(String[] names) {
        int deltaI = 0;
        double averageKfe;
        double maxAverageKfe = 0;
        List<Image> images;

        for (int i = 0; i < 100; i++) {

            images = new ArrayList<>();

            for (int j = 0; j < names.length; j++) {
                images.add(getImage(names[j]));
            }

            LearningMatrixHandler matrixHandler = new LearningMatrixHandler(images.get(0).getVector(), i, i);
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
