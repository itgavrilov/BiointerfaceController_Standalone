package ru.gsa.biointerface.ui.window.ExaminationNew;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.gsa.biointerface.domain.Channel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public final class ChannelController implements ChannelUpdater, ContentForWindow {
    private final ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private Channel channel;
    private CheckBoxOfChannel checkBox;
    private Boolean isReady = false;
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private TextField nameField;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Integer, Integer> graphic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    public void setCheckBox(CheckBoxOfChannel checkBox) {
        if (checkBox == null)
            throw new NullPointerException("checkBox is null");

        this.checkBox = checkBox;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        nameField.setText(channel.getName());
        checkBox.setText(channel.getName());
    }

    @Override
    public void update(List<Integer> data) {
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

    @Override
    public void resizeWindow(double height, double width) {
        anchorPaneRoot.setPrefHeight(height);
        anchorPaneRoot.setPrefWidth(width);

        graphic.setPrefHeight(height);
        graphic.setPrefWidth(width);
    }

    public void nameFieldChange() {
        if (!nameField.getText().equals(channel.getName())) {
            checkBox.setText(nameField.getText());
            channel.setName(nameField.getText());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelController channelController = (ChannelController) o;
        return Objects.equals(channel, channelController.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
