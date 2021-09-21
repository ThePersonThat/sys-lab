package com.alex;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class WindowBuilder {
    private TabPane tabPane;
    private final int matrixHeight;

    public WindowBuilder(int matrixHeight) {
        this.tabPane = new TabPane();
        this.matrixHeight = matrixHeight;
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    public void addTab(Image image) {
        Tab tab = new Tab(image.getFilename(), buildBox(image));
        tabPane.getTabs().add(tab);
    }

    public Pane getRootPane() {
        Pane pane = new Pane();
        pane.getChildren().add(tabPane);

        return pane;
    }

    private VBox buildBox(Image image) {
        VBox box = new VBox();
        box.setPrefWidth(App.width);
        box.setPrefHeight(App.height);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);
        box.setPadding(new Insets(20, 20, 20, 20));


        box.getChildren().add(buildImageViews(image));
        box.getChildren().add(buildImageMatrixTextArea(image));
        box.getChildren().add(buildLearningMatrixTextArea(image));

        return box;
    }

    private HBox buildImageViews(Image image) {
        ImageView view1 = new ImageView(image.getImage());
        ImageView view2 = new ImageView(new javafx.scene.image.Image("/arrow.png"));
        ImageView view3 = new ImageView(SwingFXUtils.toFXImage(ImageHandler.imageFromLearningMatrix(image), null));

        view2.setFitWidth(100);
        view2.setFitHeight(100);

        HBox box = new HBox(view1, view2, view3);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);

        return box;
    }

    private TextArea buildImageMatrixTextArea(Image image) {
        TextArea area = new TextArea();
        area.setPrefHeight(matrixHeight);
        area.setText(matrixToString(image.getImageMatrix()));

        return area;
    }

    private TextArea buildLearningMatrixTextArea(Image image) {
        TextArea area = new TextArea();
        area.setPrefHeight(matrixHeight);
        area.setText(matrixToString(image.getLearningMatrix()));

        return area;
    }

    private String matrixToString(int[][] matrix) {
        StringBuilder builder = new StringBuilder();

        for (int[] ints : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                builder.append(ints[j]).append(" ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private String matrixToString(boolean[][] matrix) {
        StringBuilder builder = new StringBuilder();

        for (boolean[] booleans : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                builder.append(booleans[j] ? 1 : 0).append(" ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

}
