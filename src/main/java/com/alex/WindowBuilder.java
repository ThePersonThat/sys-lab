package com.alex;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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

    private Node buildBox(Image image) {
        VBox box = buildVbox(15);
        box.setPrefWidth(App.width);
        box.setPrefHeight(App.height);
        box.setPadding(new Insets(20, 20, 20, 20));


        box.getChildren().add(buildImageViews(image));
        box.getChildren().add(buildImageMatrixTextArea(image));
        box.getChildren().add(buildLearningMatrixTextArea(image));
        box.getChildren().add(buildReferenceVector(image));

        return box;
    }

    private Node buildImageViews(Image image) {
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

    private Node buildImageMatrixTextArea(Image image) {
        TextArea area = new TextArea();
        area.setPrefHeight(matrixHeight);
        area.setText(matrixToString(image.getImageMatrix()));

        return buildLabel("Начальна матриця", area);
    }

    private Node buildLearningMatrixTextArea(Image image) {
        TextArea area = new TextArea();
        area.setPrefHeight(matrixHeight);
        area.setText(matrixToString(image.getLearningMatrix()));

        return buildLabel("Бiнарна матриця", area);
    }

    private Node buildReferenceVector(Image image) {
        TextArea area = new TextArea();
        area.setText(vectorToString(image.getReferenceVector()));
        area.setPrefHeight(20);

        ImageView view = new ImageView(SwingFXUtils.toFXImage(ImageHandler.imageFromVector(image), null));

        return buildLabel("Еталоний вектор", area, view);
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

    private String vectorToString(boolean[] vector) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < vector.length; i++) {
            builder.append(vector[i] ? 1 : 0).append(" ");
        }

        return builder.toString();
    }

    private VBox buildLabel(String text, Node ... node) {
        Label label = new Label(text);
        VBox vBox = buildVbox(5);

        label.setFont(new Font(18));
        vBox.getChildren().add(label);
        vBox.getChildren().addAll(node);

        return vBox;
    }

    private VBox buildVbox(int spacing) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(spacing);

        return vBox;
    }
}
