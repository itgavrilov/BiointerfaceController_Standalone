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

import java.util.List;


public class ChannelGraphic extends AnchorPane implements ChartReady<Integer, Integer> {
    private final NumberAxis graphicChartAxisX;
    private final LineChart<Integer, Integer> graphic;
    private final Slider graphicsSliderZoom;
    private final CheckBox checkBox;

    private ObservableList<XYChart.Data<Integer, Integer>> dataLineGraphic = FXCollections.observableArrayList();

    public final ChannelData<Integer, Integer> channelData = new ChannelData(this);
    private Boolean listnenerIsReady = true;
    private int maxPoint = 1024;

    public ChannelGraphic(int i, CheckBox checkBoxOut){

        checkBox = checkBoxOut;
        checkBox.setOnAction(event -> setDisable(!checkBox.isSelected()));

        graphicChartAxisX = buildAxisX();

        graphic = buildLineChart(i, graphicChartAxisX, buildAxisY());
        graphic.getData().add(new XYChart.Series(dataLineGraphic));

        graphicsSliderZoom = buildSlider();
        graphicsSliderZoom.setOnMouseReleased(event -> setSliderZoomValue((int) graphicsSliderZoom.getValue()));

        setBottomAnchor(graphic, 0.0);
        setLeftAnchor(graphic, 0.0);
        setTopAnchor(graphic, 0.0);
        setRightAnchor(graphic, 0.0);
        setLeftAnchor(graphicsSliderZoom, 30.0);
        setTopAnchor(graphicsSliderZoom, 10.0);

        setSliderZoomValue(10);
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
        xAxis.setTickUnit(maxPoint >> 3);
        xAxis.setUpperBound(maxPoint);

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
        lineChart.setTitle("channel " +number);

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
    public void update(List<XYChart.Data<Integer, Integer>> data) {
        listnenerIsReady = false;

        Platform.runLater(() -> {

            dataLineGraphic.clear();
            dataLineGraphic.addAll(data);
            data.clear();

            listnenerIsReady = true;
        });
    }

    @Override
    public boolean listnenerIsReady() { return listnenerIsReady; }

    public void setSliderZoomValue(int powerOfTwo){
        if(maxPoint > (1 << powerOfTwo)) dataLineGraphic.clear();

        if(powerOfTwo > 7)
            maxPoint = 1 << powerOfTwo;
        else
            maxPoint = 128;

        channelData.setMaxPoint(powerOfTwo);

        graphicsSliderZoom.setValue(powerOfTwo);
        graphicChartAxisX.setTickUnit(maxPoint>>3);
        graphicChartAxisX.setUpperBound(maxPoint);
    }

    public boolean paneIsActiv() {
        return !isDisable();
    }
}
