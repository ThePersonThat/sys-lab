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
        List<Image> images = Arrays.asList(image, image2);
        LearningMatrixHandler matrixHandler = new LearningMatrixHandler(image.getVector(), 70, 70);
        matrixHandler.setMatrixList(images);

        WindowBuilder builder = new WindowBuilder(250);
        builder.addTab(image);
        builder.addTab(image2);
        builder.buildTable(images, matrixHandler.getDifference());

        return builder.getRootPane();
    }

    public Image getImage(String filename)  {
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
}
