package ru.gsa.biointerface.ui.window.examination;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.repository.SampleRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.repository.exception.ReadException;
import ru.gsa.biointerface.ui.window.AlertError;
import ru.gsa.biointerface.ui.window.channel.ContentForWindow;

import java.net.URL;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public final class ChannelController implements ContentForWindow {
    private final ArrayList<XYChart.Data<Long, Integer>> samples = new ArrayList<>();
    private final ObservableList<XYChart.Data<Long, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private Channel channel;
    private int start = 0;
    private int capacity = 0;

    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private Text nameText;
    @FXML
    private NumberAxis axisX;
    @FXML
    private NumberAxis axisY;
    @FXML
    private LineChart<Long, Integer> graphic;

    public ChannelController() {
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
    }

    public void setGraph(Channel channel) {
        if (channel == null)
            throw new NullPointerException("channel is null");

        if (channel.getChannelName() != null) {
            ChannelName channelName = channel.getChannelName();
            nameText.setText(channelName.getName());
        } else {
            nameText.setText("Channel " + (channel.getNumber() + 1));
        }

        try {
            List<Sample> sampleEntities = SampleRepository.getInstance().getAllByGraph(channel);

            for (Sample sample : sampleEntities) {
                samples.add(new XYChart.Data<>(sample.getId(), sample.getValue()));
            }
        } catch (ReadException | NoConnectionException e) {
            new AlertError("Error load samples for channels: " + e.getMessage());
        }

        Platform.runLater(() -> {
            dataLineGraphic.clear();
            dataLineGraphic.addAll(samples);
        });

        this.channel = channel;
    }

    public String getName() {
        return nameText.getText();
    }

    public int getLengthGraphic() {
        return samples.size();
    }

    public void setStart(int start) {
        if (this.start != start) {
            this.start = start;
            setAxisXSize();
        }
    }

    public void setCapacity(int capacity) {
        if (this.capacity != capacity) {
            this.capacity = capacity;
            setAxisXSize();
        }
    }

    private void setAxisXSize() {
        axisX.setTickUnit(capacity >> 3);
        axisX.setLowerBound(start);
        axisX.setUpperBound(start + capacity - 1);
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
        ChannelController channelController = (ChannelController) o;
        return Objects.equals(channel, channelController.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}
