package com.alex;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void buildTable(List<Image> images) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 20, 20, 20));
        javafx.scene.image.Image sizeImage = images.get(0).getImage();
        int width = (int) sizeImage.getWidth();
        int height = (int) sizeImage.getHeight();

        HBox firstRow = new HBox();
        firstRow.setAlignment(Pos.CENTER);
        ImageView whiteImage = new ImageView(createWhiteImage(width, height));
        firstRow.getChildren().add(whiteImage);

        for (int j = 0; j < images.size(); j++) {
            ImageView view = new ImageView(images.get(j).getImage());
            firstRow.getChildren().add(view);
        }

        vBox.getChildren().add(firstRow);

        for (int i = 0; i < images.size(); i++) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            Image image = images.get(i);
            ImageView view = new ImageView(image.getImage());
            hBox.getChildren().add(view);

            for (int j = 0; j < images.size(); j++) {
                Label label = new Label();
                label.setPrefHeight(height);
                label.setPrefWidth(width);
                label.setAlignment(Pos.CENTER);
                label.setFont(new Font(18));

                int distance = images.get(i).getDistance().get(j);
                label.setText(distance == -1 ? "-" : Integer.toString(distance));

                hBox.getChildren().add(label);
            }


            vBox.getChildren().add(hBox);
        }


        Tab tab = new Tab("Графiчне вiдображення", vBox);
        tabPane.getTabs().add(tab);
    }

    private javafx.scene.image.Image createWhiteImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setPaint(new Color(244, 244, 244));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        return SwingFXUtils.toFXImage(image, null);
    }

    private Node buildBox(Image image) {
        VBox box = buildVbox(15);
        box.setPrefWidth(App.width);
        box.setPrefHeight(App.height);
        box.setPadding(new Insets(20));

        box.getChildren().add(buildImageViews(image));
        box.getChildren().add(buildMatrixs(image));
        box.getChildren().add(buildReferenceVector(image));
        box.getChildren().addAll(buildSk(image));
        box.getChildren().add(buildChars(image));
        box.getChildren().add(buildCharts(image));

        return box;
    }

    private Node buildChars(Image image) {
        StringBuilder text = new StringBuilder();

        Image.Chars chars = image.getChars();
        double[] alpha = chars.getAlpha();
        double[] betta = chars.getBetta();
        double[] d1 = chars.getD1();
        double[] d2 = chars.getD2();
        double[] shenon = chars.getChenon();
        double[] kulbak = chars.getKulbak();

        text.append(String.format("%s%14s%12s%16s%21s%16s%n", "alpha", "betta", "d1", "d2", "KFE Шенон", "KFE Кульбак"));

        for (int i = 0; i < alpha.length; i++) {
            text.append(String.format("%.2f%15.2f%15.2f%15.2f%15.3f%15.3f%n", alpha[i], betta[i], d1[i], d2[i], shenon[i], kulbak[i]));
        }

        VBox vBox = (VBox) buildLabelTextArea(150, text.toString(), "");
        vBox.setPrefWidth(30);
        vBox.setFillWidth(false);

        return buildLabel("Характеристики зображення", vBox);
    }

    private Node buildMatrixs(Image image) {
        Node imageMatrix = buildImageMatrixTextArea(image);
        Node learningMatrix = buildLearningMatrixTextArea(image);

        HBox hBox = new HBox(30, imageMatrix, learningMatrix);
        hBox.setPrefHeight(matrixHeight);

        return hBox;
    }

    private Node buildCharts(Image image) {
        Image.Chars chars = image.getChars();
        double[] chenon = chars.getChenon();
        double[] kulbak = chars.getKulbak();
        Node chenonChart = buildLabel("График по формулi Шенона", buildOneChart(chenon));
        Node kulbakChart = buildLabel("График по формулi Кульбака", buildOneChart(kulbak));

        return new HBox(chenonChart, kulbakChart);
    }

    private Node buildOneChart(double[] KFE) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("d");
        yAxis.setLabel("KFE");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setScaleX(0.85);
        lineChart.setScaleY(0.85);
        lineChart.setPrefHeight(300);
        lineChart.setPadding(Insets.EMPTY);
        lineChart.setLegendVisible(false);

        XYChart.Series<Number, Number> chart = new XYChart.Series<>();
        chart.setName("");

        for (int i = 0; i < KFE.length; i++) {
            chart.getData().add(new XYChart.Data<>(i, KFE[i]));
        }

        lineChart.getData().add(chart);

        return lineChart;
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

    private Node buildSk(Image image) {
        List<int[]> elements = image.getSK();
        HBox hBox = new HBox(30);

        for (int i = 0; i < elements.size(); i++) {
            String text = i == tabPane.getTabs().size() ? "себе" : "сусiда";
            Node node = buildLabelTextArea(20, vectorToString(elements.get(i)), "Розподiл реалiзацiй для " + text);
            hBox.getChildren().add(node);
        }

        return hBox;
    }

    private Node buildImageMatrixTextArea(Image image) {
        return buildLabelTextArea(matrixHeight, matrixToString(image.getImageMatrix()), "Навчальна матриця");
    }

    private Node buildLearningMatrixTextArea(Image image) {
        return buildLabelTextArea(matrixHeight, matrixToString(image.getLearningMatrix()), "Бiнарна матриця");
    }

    private Node buildReferenceVector(Image image) {
        return buildLabelTextArea(20, vectorToString(image.getReferenceVector()), "Еталоний вектор",
                new ImageView(SwingFXUtils.toFXImage(ImageHandler.imageFromVector(image), null)));
    }

    private Node buildLabelTextArea(int prefHeight, String text, String name, Node... node) {
        TextArea textArea = new TextArea();
        textArea.setText(text);
        textArea.setPrefHeight(prefHeight);

        List<Node> nodes = new ArrayList<>(Arrays.asList(node));
        nodes.add(0, textArea);
        Node[] arrayNodes = new Node[nodes.size()];

        return buildLabel(name, nodes.toArray(arrayNodes));
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

    private String vectorToString(int[] vector) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < vector.length; i++) {
            builder.append(vector[i]).append(" ");
        }

        return builder.toString();
    }

    private VBox buildLabel(String text, Node... node) {
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
