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
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.host.HostHandler;
import ru.gsa.biointerface.host.cash.DataListener;
import ru.gsa.biointerface.services.ChannelNameService;
import ru.gsa.biointerface.ui.window.AlertError;
import ru.gsa.biointerface.ui.window.channel.ChannelCheckBox;
import ru.gsa.biointerface.ui.window.channel.ContentForWindow;

import java.net.URL;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public final class ChannelForMeteringController implements DataListener, ContentForWindow {
    private final ChannelNameService channelNameService;
    private final ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private final List<Integer> samples = new ArrayList<>();
    private ChannelName channelName;
    private HostHandler hostHandler;
    private ChannelCheckBox checkBox;
    private int numberOfChannel;
    private final StringConverter<ChannelName> converter = new StringConverter<>() {
        @Override
        public String toString(ChannelName channelName) {
            return getChannelName(numberOfChannel, channelName);
        }

        @Override
        public ChannelName fromString(String string) {
            return null;
        }
    };
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private ComboBox<ChannelName> nameComboBox;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Integer, Integer> graphic;

    public ChannelForMeteringController() throws Exception {
        channelNameService = ChannelNameService.getInstance();
    }

    private static String getChannelName(int numberOfChannel, ChannelName channelName) {
        String str = "Channel " + (numberOfChannel + 1);
        if (channelName != null)
            str = channelName.getName();

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

    public void setConnection(HostHandler hostHandler) {
        if (hostHandler == null)
            throw new NullPointerException("Connection is null");

        this.hostHandler = hostHandler;
    }

    public void onNameComboBoxShowing() {
        ObservableList<ChannelName> channelNames = FXCollections.observableArrayList();
        try {
            channelNames.addAll(channelNameService.getAll());
        } catch (Exception e) {
            new AlertError("Error load list channel names: " + e.getMessage());
        }
        nameComboBox.getItems().clear();
        nameComboBox.getItems().addAll(channelNames);
    }

    public void nameComboBoxSelect() {
        channelName = nameComboBox.getValue();
        hostHandler.setNameInChannel(numberOfChannel, channelName);
        checkBox.setText(getChannelName(numberOfChannel, channelName));
    }

    public void setCheckBox(ChannelCheckBox checkBox) {
        if (checkBox == null)
            throw new NullPointerException("checkBox is null");

        String channelName = getChannelName(numberOfChannel, this.channelName);

        this.checkBox = checkBox;
        checkBox.setText(channelName);
    }

    @Override
    public void setNewSamples(Deque<Integer> data) {
        samples.addAll(data);
        Platform.runLater(this::filling);
    }

    public void setCapacity(int capacity) {
        if (capacity < 128)
            throw new IllegalArgumentException("Capacity must be greater than 127");

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
        ChannelForMeteringController that = (ChannelForMeteringController) o;
        return numberOfChannel == that.numberOfChannel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfChannel);
    }
}
