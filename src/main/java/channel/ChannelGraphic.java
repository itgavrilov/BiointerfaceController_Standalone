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

import java.util.LinkedList;

public class ChannelGraphic<X extends Number,Y extends Number> extends AnchorPane implements ChartReady<X, Y> {
    private final NumberAxis graphicChartAxisX;
    private final LineChart<X, Y> graphic;
    private final Slider graphicsSliderZoom;
    private final CheckBox checkBox;

    private ObservableList<XYChart.Data<X, Y>> dataLineGraphic = FXCollections.observableArrayList();

    public final ChannelData<X, Y> channelData = new ChannelData(this);
    private Boolean isReady = false;
    private int countPoint = 1023;

    public ChannelGraphic(int i, Slider sliderZoom, CheckBox checkBoxOut){
        checkBox = checkBoxOut;
        checkBox.setOnAction(event -> setDisable(!checkBox.isSelected()));
        graphicChartAxisX = buildAxisX();
        graphic = buildLineChart(i, graphicChartAxisX, buildAxisY());
        graphic.getData().add(new XYChart.Series(dataLineGraphic));

        graphicsSliderZoom = buildSlider(sliderZoom);
        graphicsSliderZoom.setOnMouseReleased(event -> setSliderZoomValue((int) graphicsSliderZoom.getValue()));

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
        xAxis.setTickUnit(countPoint >> 3);
        xAxis.setUpperBound(countPoint);

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
        return slider;
    }

    @Override
    public void update(LinkedList<XYChart.Data<X, Y>> data)  {
        Platform.runLater(() -> {
            dataLineGraphic.clear();
            dataLineGraphic.addAll(data);
            data.clear();
            setReady(true);
        });
    }

    @Override
    public boolean getReady() { return isReady; }

    @Override
    public void setReady(boolean Ready) { isReady = Ready; }

    public void setSliderZoomValue(double countPointInPowerOfTwo){
        if(countPoint > ((1 << (int)countPointInPowerOfTwo)-1)) dataLineGraphic.clear();
        if(countPointInPowerOfTwo > 7) countPoint = (1 << (int)countPointInPowerOfTwo)-1;
        else countPoint = 127;
        channelData.setMaxPoint((int)countPointInPowerOfTwo);
        graphicsSliderZoom.setValue(countPointInPowerOfTwo);
        setGraphicChartAxisXSize(countPoint);
    }

    private void setGraphicChartAxisXSize(int countPoint){
        graphicChartAxisX.setTickUnit(countPoint>>3);
        graphicChartAxisX.setUpperBound(countPoint);
    }

    public boolean paneIsActiv() {
        return !isDisable();
    }
}
