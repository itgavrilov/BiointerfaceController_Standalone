package channel;

import javafx.scene.chart.XYChart;

import java.util.LinkedList;

public interface ChartReady<X extends Number,Y extends Number> {
    void update(LinkedList<XYChart.Data<X, Y>> data);
    boolean getReady();
    void setReady(boolean Ready);
}
