package channel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public final class Channel extends AnchorPane implements GUIIsReady<Double> {
    private Separator separator;
    private final NumberAxis graphicChartAxisX;
    private final NumberAxis graphicChartAxisY;
    private final LineChart<Integer, Double> graphic;
    private final CheckBox checkBox;
    public final Slider graphicsSliderZoom;

    private ObservableList<XYChart.Data<Integer, Double>> dataLineGraphic = FXCollections.observableArrayList();
    private final Data data = new Data( this, 10);

    private Boolean isReady = false;
    private int capacity = 1024;

    public Channel(int i, Slider sliderZoom, CheckBox checkBoxOut){
        separator = buildSeparator();
        setLeftAnchor(separator, 0.0);
        setBottomAnchor(separator, 0.0);
        setRightAnchor(separator, 0.0);

        checkBox = checkBoxOut;
        checkBox.setOnAction(event -> setDisable(!checkBox.isSelected()));

        graphicsSliderZoom = buildSlider(sliderZoom);
        graphicsSliderZoom.setOnMouseReleased(event -> setSliderZoomValue((int) graphicsSliderZoom.getValue()));
        setLeftAnchor(graphicsSliderZoom, 30.0);
        setTopAnchor(graphicsSliderZoom, 10.0);

        graphicChartAxisX = buildAxisX();
        graphicChartAxisY = buildAxisY();
        graphic = buildLineChart(i, graphicChartAxisX, graphicChartAxisY);
        graphic.getData().add(new XYChart.Series(dataLineGraphic));

        setBottomAnchor(graphic, 0.0);
        setLeftAnchor(graphic, 0.0);
        setTopAnchor(graphic, 0.0);
        setRightAnchor(graphic, 0.0);

        setSliderZoomValue((int)sliderZoom.getValue());

        building();
    }

    private void building(){
        getChildren().addAll(separator, graphic, graphicsSliderZoom);
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
        axis.setUpperBound(capacity-1);
        axis.setTickLabelsVisible(false);
        axis.setPrefHeight(0);
        axis.setPrefWidth(0);
        return axis;
    }

    private NumberAxis buildAxisY() {
        NumberAxis axis = new NumberAxis();
        axis.setAutoRanging(false);
        axis.setForceZeroInRange(false);
        axis.setMinorTickLength(1);
        axis.setMinorTickVisible(false);
        axis.setPrefWidth(23);
        axis.setSide(Side.LEFT);
        axis.setTickLength(1);
        axis.setTickUnit(128);
        axis.setUpperBound(4095);
        axis.setTickLabelsVisible(false);
        axis.setPrefHeight(0);
        axis.setPrefWidth(0);
        return axis;
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
        lineChart.setStyle("-fx-font-size: " + 12 + "px;");
        lineChart.setLegendVisible(false);
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

    public void setSliderZoomValue(int capacityInPowerOfTwo){
        if(capacityInPowerOfTwo < 7)
            capacityInPowerOfTwo = 7;
        capacity = 1 << capacityInPowerOfTwo;
        if(dataLineGraphic.size() > capacity) {
            int tmpDelta = dataLineGraphic.size() - capacity -1;
            for(int i = 0; i < capacity; i++) {
                dataLineGraphic.get(i).setYValue(dataLineGraphic.get(i+tmpDelta).getYValue());
            }
            dataLineGraphic.remove(capacity, dataLineGraphic.size());
        } else {

            ArrayList<XYChart.Data<Integer, Double>> tmp = new ArrayList<>();
            while (tmp.size() < capacity - dataLineGraphic.size()) {
                tmp.add(new XYChart.Data<>(dataLineGraphic.size() + tmp.size() - 1, 0.0));
            }

            Platform.runLater(()->dataLineGraphic.addAll(tmp));
        }
        data.setCapacity(capacityInPowerOfTwo);
        graphicsSliderZoom.setValue(capacityInPowerOfTwo);
        setGraphicChartAxisXSize(capacity);
    }

    private void setGraphicChartAxisXSize(int countPoint){
        graphicChartAxisX.setTickUnit(countPoint>>3);
        graphicChartAxisX.setUpperBound(countPoint-1);
    }

    public boolean paneIsActiv() {
        return !isDisable();
    }

    public void add(double val){
        data.dataCash.add(val);
    }
}
