package ru.gsa.biointerfaceController_standalone.controllers.channel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public final class ChannelGUI<T extends Number> extends AnchorPane implements ChannelGUIUpdater<T>, Comparable<ChannelGUI<T>> {
    private final char index;

    private final Separator separator;
    private final NumberAxis graphicChartAxisX;
    private final LineChart<Integer, T> graphic;
    private final ObservableList<XYChart.Data<Integer, T>> dataLineGraphic = FXCollections.observableArrayList();

    private Boolean isReady = false;
    private int capacity = 1024;

    public ChannelGUI(char index) {
        this.index = index;

        setMinWidth(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);

        separator = buildSeparator();
        setLeftAnchor(separator, 5.0);
        setBottomAnchor(separator, 0.0);
        setRightAnchor(separator, 0.0);

        graphicChartAxisX = buildAxisX();
        NumberAxis graphicChartAxisY = buildAxisY();

        graphic = buildLineChart(index + 1, graphicChartAxisX, graphicChartAxisY, dataLineGraphic);
        setTopAnchor(graphic, 0.0);
        setBottomAnchor(graphic, 0.0);
        setLeftAnchor(graphic, 0.0);
        setRightAnchor(graphic, 0.0);

        building();
    }

    private void building() {
        getChildren().addAll(separator, graphic);
    }

    private Separator buildSeparator() {
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        separator.setMinWidth(1);
        separator.setMaxWidth(1);
        separator.setPrefWidth(1);
        return separator;
    }

    private NumberAxis buildAxisX() {
        NumberAxis axis = new NumberAxis();
        axis.setAnimated(false);
        axis.setAutoRanging(false);
        axis.setMinorTickLength(0);
        axis.setMinorTickVisible(false);
        axis.setPrefHeight(10);
        axis.setSide(Side.BOTTOM);
        axis.setTickLabelGap(1);
        axis.setTickLength(5);
        axis.setTickUnit(capacity >> 3);
        axis.setUpperBound(capacity - 1);
        axis.setTickLabelsVisible(false);
        axis.setPrefHeight(0);
        axis.setPrefWidth(0);
        return axis;
    }

    private NumberAxis buildAxisY() {
        NumberAxis axis = new NumberAxis();
        int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
        axis.setAutoRanging(false);
        axis.setForceZeroInRange(false);
        axis.setMinorTickLength(0);
        axis.setMinorTickVisible(false);
        axis.setPrefWidth(23);
        axis.setSide(Side.LEFT);
        axis.setTickLength(5);
        axis.setTickUnit((max - min) >> 1);
        axis.setUpperBound(max);
        axis.setLowerBound(min);
        axis.setTickLabelsVisible(false);
        axis.setPrefHeight(0);
        axis.setPrefWidth(0);
        return axis;
    }

    private LineChart<Integer, T> buildLineChart(int number, NumberAxis buildAxisX, NumberAxis buildAxisY, ObservableList<XYChart.Data<Integer, T>> chart) {
        LineChart<Integer, T> lineChart = new LineChart(buildAxisX, buildAxisY);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setAnimated(false);
        lineChart.setCache(false);
        lineChart.setEffect(null);
        lineChart.setCreateSymbols(false);
        lineChart.setPickOnBounds(false);
        lineChart.setTitle("channel " + number);
        lineChart.setStyle("-fx-font-size: " + 10 + "px;");
        lineChart.setStyle("-fx-padding: 0px;");
        lineChart.setStyle("-fx-border: 0px;");
        lineChart.setLegendVisible(false);
        lineChart.getData().add(new XYChart.Series<>(chart));

        lineChart.setMinWidth(USE_PREF_SIZE);
        lineChart.setMinWidth(USE_PREF_SIZE);
        lineChart.setMinHeight(USE_PREF_SIZE);
        lineChart.setMaxHeight(USE_PREF_SIZE);

        return lineChart;
    }

    @Override
    public void update(ArrayList<T> data) {
        Platform.runLater(() -> {
            for (int i = 0; i < data.size(); i++) {
                dataLineGraphic.get(i).setYValue(data.get(i));
            }
            setReady(true);
        });
    }

    @Override
    public void setCapacity(int capacity) {
        if (capacity < 7)
            capacity = 7;
        this.capacity = 1 << capacity;
        if (dataLineGraphic.size() > this.capacity) {
            int tmpDelta = dataLineGraphic.size() - this.capacity - 1;
            for (int i = 0; i < this.capacity; i++) {
                dataLineGraphic.get(i).setYValue(dataLineGraphic.get(i + tmpDelta).getYValue());
            }
            dataLineGraphic.remove(this.capacity, dataLineGraphic.size());
        } else if (dataLineGraphic.size() < this.capacity) {
            ArrayList<XYChart.Data<Integer, T>> tmp = new ArrayList<>();

            while (tmp.size() < this.capacity - dataLineGraphic.size()) {
                tmp.add(new XYChart.Data<>(tmp.size(), (T) Integer.valueOf(0)));
            }

            dataLineGraphic.forEach(o -> tmp.add(new XYChart.Data<>(tmp.size(), o.getYValue())));

            Platform.runLater(() -> {
                dataLineGraphic.clear();
                dataLineGraphic.addAll(tmp);
            });
        }

        setGraphicChartAxisXSize(this.capacity);
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void setReady(boolean Ready) {
        isReady = Ready;
    }

    public int getIndex() {
        return index;
    }

    private void setGraphicChartAxisXSize(int countPoint) {
        graphicChartAxisX.setTickUnit(countPoint >> 3);
        graphicChartAxisX.setUpperBound(countPoint - 1);
    }

    public void resizeWindow() {
        graphic.setPrefHeight(getPrefHeight());
        graphic.setPrefWidth(getPrefWidth());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelGUI<?> that = (ChannelGUI<?>) o;
        return index + 1 == that.index + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index + 1);
    }

    @Override
    public int compareTo(ChannelGUI<T> o) {
        return (index + 1) - (o.index + 1);
    }
}
