package channel;

import channel.cash.DataCash;
import javafx.scene.chart.XYChart;
import channel.cash.DataReady;
import channel.seriesReducer.MyPoint;
import channel.seriesReducer.SeriesReducer;

import java.util.*;

public class ChannelData<X extends Number,Y extends Number> implements DataReady<Y> {
    private final ChartReady<X, Y> listener;
    private int powerOfTwoForMaxPoint = 10;
    private int maxPoint = 1 << powerOfTwoForMaxPoint;
    private int reduceEpsilon = (powerOfTwoForMaxPoint - 7)<<2;
    private LinkedList<MyPoint<X, Y>> points = new LinkedList<>();

    public final DataCash<Y> dataCash = new DataCash<>(this);

    ChannelData(ChartReady<X, Y> listener){ this.listener = listener; }

    //final LinkedList<XYChart.Data<X, Y>> tmpPoints = new LinkedList<>();

    @Override
    public void update(LinkedList<Y> data) {
        Integer pointsIndex = points.size();
        if (pointsIndex < maxPoint) {
            for (Y o : data) {
                points.add(new MyPoint<>((X)pointsIndex++, o));
            }
        } else {
            int i = 0;
            int j = 0;
            int lastPoint = points.size() - data.size();

            for(MyPoint<X, Y> o: points){
                if (i < lastPoint) {
                    o.setY(points.get((i++) + data.size()).getY());
                } else {
                    o.setY(data.get(j++));
                }
            }
        }
        data.clear();

        if(listener.getReady()) {
            listener.setReady(false);
            LinkedList<XYChart.Data<X, Y>> tmpPoints = new LinkedList<>();
            LinkedList<MyPoint<X, Y>> reduced = SeriesReducer.reduce(points, reduceEpsilon);

            for (MyPoint o : reduced) {
                tmpPoints.add(new XYChart.Data<>((X) o.getX(), (Y) o.getY()));
            }
            reduced.clear();
            listener.update(tmpPoints);
        }
    }

//    long t0 = System.nanoTime();
//    long t1 = System.nanoTime();
//    System.out.println(String.format("Reduces points from in %.1f ms", (t1 - t0) / 1e6));


    public void setMaxPoint (int powerOfTwo) {
        if(powerOfTwo > 7)
            powerOfTwoForMaxPoint = powerOfTwo;
        else
            powerOfTwoForMaxPoint = 7;

        maxPoint = 1 << powerOfTwoForMaxPoint;
        //reduceEpsilon = 1 << (powerOfTwoForMaxPoint - 7);
        reduceEpsilon = (powerOfTwoForMaxPoint - 7)<<2;

        if(points.size()>maxPoint) {
            while (points.size() > maxPoint) {
                points.remove(0);
            }
            for(Integer i=0;i<points.size();i++){
                points.get(i).setX((X) i);
            }
        }
        dataCash.setPowerOfTwoForUpdateCount(powerOfTwoForMaxPoint);
    }
}
