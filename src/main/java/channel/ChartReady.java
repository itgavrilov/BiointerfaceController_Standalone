package channel;

import javafx.scene.chart.XYChart;

import java.util.List;

public interface ChartReady<X extends Number,Y extends Number> {
    void update(List<XYChart.Data<X, Y>> data);
    boolean listnenerIsReady();
}
