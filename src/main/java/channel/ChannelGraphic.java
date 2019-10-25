package channel;

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

import java.util.ArrayList;

public class ChannelGraphic extends AnchorPane implements ChartReady {
    private final NumberAxis graphicChartAxisX;
    private final NumberAxis graphicChartAxisY;
    private final LineChart<Integer, Double> graphic;
    private final CheckBox checkBox;
    public final Slider graphicsSliderZoom;

    private ObservableList<XYChart.Data<Integer, Double>> dataLineGraphic = FXCollections.observableArrayList();

    public final ChannelData channelData = new ChannelData( this, 10);
    private Boolean isReady = false;
    private int capacity = 1024;

    public ChannelGraphic(int i, Slider sliderZoom, CheckBox checkBoxOut){
        checkBox = checkBoxOut;
        checkBox.setOnAction(event -> setDisable(!checkBox.isSelected()));
        graphicsSliderZoom = buildSlider(sliderZoom);
        graphicsSliderZoom.setOnMouseReleased(event -> setSliderZoomValue((int) graphicsSliderZoom.getValue()));

        graphicChartAxisX = buildAxisX();
        graphicChartAxisY = buildAxisY();
        graphic = buildLineChart(i, graphicChartAxisX, graphicChartAxisY);
        graphic.getData().add(new XYChart.Series(dataLineGraphic));

        setBottomAnchor(graphic, 0.0);
        setLeftAnchor(graphic, 0.0);
        setTopAnchor(graphic, 0.0);
        setRightAnchor(graphic, 0.0);
        setLeftAnchor(graphicsSliderZoom, 30.0);
        setTopAnchor(graphicsSliderZoom, 10.0);

        setSliderZoomValue((int)sliderZoom.getValue());
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
        xAxis.setTickUnit(capacity >> 3);
        xAxis.setUpperBound(capacity -1);

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
        lineChart.setCache(false);
        lineChart.setEffect(null);
        lineChart.setCreateSymbols(false);
        lineChart.setPickOnBounds(false);
        lineChart.setTitle("channel " +number);
        lineChart.setLayoutX(10);
        lineChart.setLayoutY(10);

        return  lineChart;
    }

    private Slider buildSlider(Slider sliderZoom) {
        Slider slider = new Slider();
        slider.setValue(sliderZoom.getValue());
        slider.setBlockIncrement(sliderZoom.getBlockIncrement());
        slider.setLayoutX(sliderZoom.getLayoutX());
        slider.setLayoutY(sliderZoom.getLayoutY());
        slider.setMajorTickUnit(sliderZoom.getMajorTickUnit());
        slider.setMinorTickCount(sliderZoom.getMinorTickCount());
        slider.setMin(sliderZoom.getMin());
        slider.setMax(sliderZoom.getMax());
        slider.setPrefHeight(sliderZoom.getPrefHeight());
        slider.setPrefWidth(sliderZoom.getPrefWidth());
        slider.setDisable(sliderZoom.isDisable());
        return slider;
    }

    @Override
    public void update(ArrayList<Double> data)  {
        Platform.runLater(() -> {
            for(int i=0; i<data.size(); i++){
                dataLineGraphic.get(i).setYValue(data.get(i));
            }
            setReady(true);
        });
    }

    @Override
    public boolean getReady() { return isReady; }

    @Override
    public void setReady(boolean Ready) { isReady = Ready; }

    public void setSliderZoomValue(double capacityInPowerOfTwo){
        int tmp;
        if(capacityInPowerOfTwo > 7)
            tmp = (int)capacityInPowerOfTwo;
        else
            tmp = 7;
        capacity = 1 << tmp;
        if(dataLineGraphic.size() > capacity) {
            int tmpDelta = dataLineGraphic.size() - capacity -1;
            for(int i = 0; i < capacity; i++) {
                dataLineGraphic.get(i).setYValue(dataLineGraphic.get(i+tmpDelta).getYValue());
            }
            dataLineGraphic.remove(capacity, dataLineGraphic.size());
        } else while (dataLineGraphic.size() < capacity){
            dataLineGraphic.add(new XYChart.Data<>(dataLineGraphic.size()-1, 0.0));
        }
        channelData.setCapacity(tmp);
        graphicsSliderZoom.setValue(tmp);
        setGraphicChartAxisXSize(capacity);
    }

    private void setGraphicChartAxisXSize(int countPoint){
        graphicChartAxisX.setTickUnit(countPoint>>3);
        graphicChartAxisX.setUpperBound(countPoint-1);
    }

    public boolean paneIsActiv() {
        return !isDisable();
    }
}
