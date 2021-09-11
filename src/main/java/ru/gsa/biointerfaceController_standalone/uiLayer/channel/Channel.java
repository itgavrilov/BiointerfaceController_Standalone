package ru.gsa.biointerfaceController_standalone.uiLayer.channel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public final class Channel implements ChannelUpdater<Integer>, ContentComparable {
    private final ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private int id;
    private Boolean isReady = false;
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private Text title;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Integer, Integer> graphic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
        int min = -2048,
                max = 2047,
                tickUnit = max / 4,
                tickCount = (max - min) / tickUnit;
        setCapacity(10);


        axisY.setTickUnit(tickUnit);
        axisX.setMinorTickCount(tickCount);
        axisY.setUpperBound(max);
        axisY.setLowerBound(min);
        graphic.getData().add(new XYChart.Series<>(dataLineGraphic));
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        title.setText("Channel " + (id + 1));
    }

    @Override
    public void update(ArrayList<Integer> data) {
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
        int capacity1 = 1 << capacity;
        if (dataLineGraphic.size() > capacity1) {
            int tmpDelta = dataLineGraphic.size() - capacity1 - 1;
            for (int i = 0; i < capacity1; i++) {
                dataLineGraphic.get(i).setYValue(dataLineGraphic.get(i + tmpDelta).getYValue());
            }
            dataLineGraphic.remove(capacity1, dataLineGraphic.size());
        } else if (dataLineGraphic.size() < capacity1) {
            ArrayList<XYChart.Data<Integer, Integer>> tmp = new ArrayList<>();

            while (tmp.size() < capacity1 - dataLineGraphic.size()) {
                tmp.add(new XYChart.Data<>(tmp.size(), 0));
            }

            dataLineGraphic.forEach(o -> tmp.add(new XYChart.Data<>(tmp.size(), o.getYValue())));

            Platform.runLater(() -> {
                dataLineGraphic.clear();
                dataLineGraphic.addAll(tmp);
            });
        }

        setAxisXSize(capacity1);
    }

    private void setAxisXSize(int countPoint) {
        axisX.setTickUnit(countPoint >> 3);
        axisX.setUpperBound(countPoint - 1);
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void setReady(boolean Ready) {
        isReady = Ready;
    }

    public void resizeWindow(double height, double width) {
        anchorPaneRoot.setPrefHeight(height);
        anchorPaneRoot.setPrefWidth(width);

        graphic.setPrefHeight(height);
        graphic.setPrefWidth(width);

        title.setWrappingWidth(width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel that = (Channel) o;
        return id + 1 == that.id + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id + 1);
    }

    @Override
    public int compareTo(ContentComparable o) {
        return (id + 1) - (o.getId() + 1);
    }
}
