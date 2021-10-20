package ru.gsa.biointerface.ui.window.metering;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.DataListener;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.ui.window.graph.CheckBoxOfGraph;
import ru.gsa.biointerface.ui.window.graph.ContentForWindow;

import java.net.URL;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public final class GraphForMeteringController implements DataListener, ContentForWindow {
    private final ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private final List<Integer> samples = new ArrayList<>();
    private int numberOfChannel;
    private final StringConverter<Channel> converter = new StringConverter<>() {
        @Override
        public String toString(Channel channel) {
            return getChannelName(numberOfChannel, channel);
        }

        @Override
        public Channel fromString(String string) {
            return null;
        }
    };
    private Channel channelSelected;
    private Connection connection;
    private CheckBoxOfGraph checkBox;
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private ComboBox<Channel> nameComboBox;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Integer, Integer> graphic;
    private Boolean ready = true;

    private static String getChannelName(int numberOfChannel, Channel channel) {
        String str = "Channel " + (numberOfChannel + 1);
        if (channel != null)
            str = channel.getName();
        return str;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int min = -2048,
                max = 2047,
                tickUnit = max / 4,
                tickCount = (max - min) / tickUnit;

        axisY.setTickUnit(tickUnit);
        axisY.setMinorTickCount(tickCount);
        axisY.setUpperBound(max);
        axisY.setLowerBound(min);
        graphic.getData().add(new XYChart.Series<>(dataLineGraphic));
        nameComboBox.setConverter(converter);
    }

    public void setNumberOfChannel(int numberOfChannel) {
        if (numberOfChannel < 0)
            throw new IllegalArgumentException("NumberOfChannel < 0");

        this.numberOfChannel = numberOfChannel;
    }

    public void setConnection(Connection connection) {
        if (connection == null)
            throw new NullPointerException("Connection is null");

        this.connection = connection;
    }

    public void onNameComboBoxShowing() {
        ObservableList<Channel> list = FXCollections.observableArrayList();
        try {
            list.addAll(Channel.getAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        nameComboBox.getItems().clear();
        nameComboBox.getItems().addAll(list);
    }

    public void nameComboBoxSelect() {
        channelSelected = nameComboBox.getValue();
        String channelName = getChannelName(numberOfChannel, channelSelected);

        try {
            connection.setChannelInGraph(numberOfChannel, channelSelected);
        } catch (DomainException e) {
            e.printStackTrace();
        }
        nameComboBox.getEditor().setText(channelName);
        checkBox.setText(channelName);
    }

    public void setCheckBox(CheckBoxOfGraph checkBox) throws DomainException {
        if (checkBox == null)
            throw new NullPointerException("checkBox is null");

        String channelName = getChannelName(numberOfChannel, channelSelected);

        this.checkBox = checkBox;
        checkBox.setText(channelName);
    }

    @Override
    public void setNewSamples(Deque<Integer> data) {
        ready = false;

        samples.addAll(data);

        Platform.runLater(() -> {
            filling();
            ready = true;
        });
    }

    public void setCapacity(int capacity) throws DomainException {
        if (capacity < 128)
            throw new DomainException("Capacity must be greater than 127");

        if (dataLineGraphic.size() > capacity) {
            Platform.runLater(() -> {
                dataLineGraphic.remove(capacity, dataLineGraphic.size());
                filling();
            });
        } else if (dataLineGraphic.size() < capacity) {
            Platform.runLater(() -> {
                while (dataLineGraphic.size() < capacity) {
                    dataLineGraphic.add(new XYChart.Data<>(dataLineGraphic.size(), 0));
                }
                filling();
            });
        }

        setAxisXSize(capacity);
    }

    private void filling() {
        if (samples.size() >= dataLineGraphic.size()) {
            for (int i = 0; i < dataLineGraphic.size(); i++) {
                int value = samples.get(samples.size() - dataLineGraphic.size() + i);
                dataLineGraphic.get(i).setYValue(value);
            }
        } else {
            for (int i = 0, j = 0; i < dataLineGraphic.size(); i++) {
                if (i < dataLineGraphic.size() - samples.size()) {
                    dataLineGraphic.get(i).setYValue(0);
                } else {
                    int value = samples.get(j++);
                    dataLineGraphic.get(i).setYValue(value);
                }
            }
        }
    }

    private void setAxisXSize(int capacity) {
        axisX.setTickUnit(capacity >> 3);
        axisX.setLowerBound(0);
        axisX.setUpperBound(capacity - 1);
    }

    public void setEnable(boolean enable) {
        nameComboBox.setDisable(!enable);
    }

    @Override
    public void resizeWindow(double height, double width) {
        anchorPaneRoot.setPrefHeight(height);
        anchorPaneRoot.setPrefWidth(width);

        graphic.setPrefHeight(height);
        graphic.setPrefWidth(width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphForMeteringController that = (GraphForMeteringController) o;
        return numberOfChannel == that.numberOfChannel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfChannel);
    }
}
