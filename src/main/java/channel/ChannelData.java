package channel;

import cash.DataCash;
import javafx.scene.chart.XYChart;
import cash.DataReady;
import channel.seriesReducer.MyPoint;
import channel.seriesReducer.SeriesReducer;

import java.util.*;

public class ChannelData<X extends Number,Y extends Number> implements DataReady<Y> {

    private final ChartReady<X, Y> listener;

    List<MyPoint<X, Y>> points = new ArrayList<>();


    public final DataCash<Y> dataCash = new DataCash(this);

    private Boolean listnenerIsReady = true;

    private int powerOfTwoForMaxPoint = 10;

    private int maxPoint = 1 << powerOfTwoForMaxPoint;

    private int reduceEpsilon = 1 << (powerOfTwoForMaxPoint - 8);



    public ChannelData (ChartReady<X, Y> listener){ this.listener = listener; }

    @Override
    public void update(List<Y> data) {
        listnenerIsReady = false;

        Integer pointsLastIndex = points.size();

        if (pointsLastIndex < maxPoint) {
            for(Y o:data){ points.add(new MyPoint<>((X)pointsLastIndex++, o)); }
        } else {
            for (int i = 0, j=0; i < maxPoint; i++) {
                if (i < maxPoint - data.size()) {
                    points.get(i).setY(points.get(i + data.size()).getY());
                } else {
                    points.get(i).setY(data.get(j++));
                }
            }
        }

        data.clear();

        if(listener.listnenerIsReady()){
            List<XYChart.Data<X, Y>> tmpPoints = new ArrayList<>();

            List<MyPoint<X, Y>> reduced =  SeriesReducer.reduce(points, reduceEpsilon);

            for(MyPoint o: reduced){
                tmpPoints.add(new XYChart.Data<>((X) o.getX(), (Y) o.getY()));
            }

            listener.update(new ArrayList<>(tmpPoints));
        }

        listnenerIsReady = true;
    }

    @Override
    public boolean listnenerIsReady() {
        return listnenerIsReady;
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
        reduceEpsilon = 1 << (powerOfTwoForMaxPoint - 7);

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
