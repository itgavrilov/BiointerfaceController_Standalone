package programms;

import cash.DataCash;
import cash.Listener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

import java.util.List;


public class ChannelChart extends AnchorPane implements Listener<Integer>{
    private NumberAxis graphicChartAxisX;
    private NumberAxis graphicChartAxisY;
    private LineChart<Integer, Integer> graphic;
    private Slider graphicsSliderZoom;
    private CheckBox checkBox;

    private ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();
    private int maxPoint = 10;
    public final DataCash<Integer> dataCash = new DataCash(this);

    public ChannelChart(int i, CheckBox checkBoxOut){

        checkBox = checkBoxOut;
        checkBox.setOnAction(event -> {
            setDisable(!checkBox.isSelected());
        });

        graphicChartAxisX = buildAxisX();

        graphicChartAxisY = buildAxisY();

        graphic = buildLineChart(i, graphicChartAxisX, graphicChartAxisY);
        graphic.getData().add(new XYChart.Series(dataLineGraphic));

        graphicsSliderZoom = buildSlider();
        graphicsSliderZoom.setOnMouseReleased(event -> {
            setSliderZoomValue((int) graphicsSliderZoom.getValue());
        });

        setBottomAnchor(graphic, 0.0);
        setLeftAnchor(graphic, 0.0);
        setTopAnchor(graphic, 0.0);
        setRightAnchor(graphic, 0.0);
        setLeftAnchor(graphicsSliderZoom, 30.0);
        setTopAnchor(graphicsSliderZoom, 10.0);
    }

    public void building(){
        getChildren().addAll(graphic, graphicsSliderZoom);
    }

    private NumberAxis buildAxisX() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setAnimated(false);
        xAxis.setAutoRanging(false);
        xAxis.setMinorTickLength(0);
        xAxis.setMinorTickVisible(false);
        xAxis.setPrefHeight(10);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setTickLabelGap(1);
        xAxis.setTickLength(5);
        xAxis.setTickUnit(1 << (maxPoint - 3));
        xAxis.setUpperBound(1 << maxPoint);

        return xAxis;
    }

    private NumberAxis buildAxisY() {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setForceZeroInRange(false);
        yAxis.setMinorTickLength(1);
        yAxis.setMinorTickVisible(false);
        yAxis.setPrefWidth(23);
        yAxis.setSide(Side.LEFT);
        yAxis.setTickLength(1);
        yAxis.setTickUnit(128);
        yAxis.setUpperBound(4095);

        return yAxis;
    }

    private LineChart buildLineChart(int number, NumberAxis buildAxisX, NumberAxis buildAxisY) {
        LineChart lineChart = new LineChart(buildAxisX, buildAxisY);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setAnimated(false);
        lineChart.setCache(true);
        lineChart.setDisable(true);
        lineChart.setLayoutX(10);
        lineChart.setLayoutY(10);
        lineChart.setPickOnBounds(false);
        lineChart.setTitle("Channel "+number);

        return  lineChart;
    }

    private Slider buildSlider() {
        Slider slider = new Slider();
        slider.setValue(10);
        slider.setBlockIncrement(1);
        slider.setLayoutX(10);
        slider.setLayoutY(10);
        slider.setMajorTickUnit(1);
        slider.setMax(13);
        slider.setMin(7);
        slider.setMinorTickCount(1);
        slider.setPrefHeight(14);
        slider.setPrefWidth(110);

        return slider;
    }

    @Override
    public void update(List<Integer> dataInDataCash) {
        //setDataInObservableList(MyMath.rms(dataInDataCash, (1<<maxPoint)/ (1<<(this.maxPoint - 3))));
        setDataInObservableList(dataInDataCash);
    }

    public void setDataInObservableList(List<Integer> data){
        Platform.runLater(() -> {
            int dataLineChartSize = dataLineGraphic.size();
            int dataSize = data.size();

            if (dataLineChartSize >= (1 << maxPoint)) {
                int counterData = 0;
                for (int i = 0; i < dataLineChartSize; i++) {
                    if (dataLineGraphic.get(i).getXValue() < dataLineChartSize - dataSize) {
                        dataLineGraphic.get(i).setYValue(dataLineGraphic.get(i + dataSize).getYValue());
                    } else {
                        dataLineGraphic.get(i).setYValue(data.get(counterData++));
                    }
                }
            } else {
                for (Integer val : data) {
                    dataLineGraphic.add(new XYChart.Data(dataLineChartSize++, val));
                }
            }
            data.clear();
        });
    }

    public void setSliderZoomValue(int val){
        if(maxPoint > val) dataLineGraphic.clear();
        maxPoint = val;
        graphicsSliderZoom.setValue(maxPoint);
        dataCash.setUpdateCount(maxPoint - 3);
        graphicChartAxisX.setUpperBound(1 << maxPoint);
        graphicChartAxisX.setTickUnit(1 << (maxPoint - 3));
    }

    public boolean paneIsActiv() {
        return !isDisable();
    }
}
