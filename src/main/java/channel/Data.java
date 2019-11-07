package channel;

import channel.dataCash.DataCash;
import channel.dataCash.DataCashListener;

import java.util.*;

public class Data implements DataCashListener<Double> {
    private final GUIIsReady listener;
    private LinkedList<Double> arrayOfMeasurements = new LinkedList();
    private int capacity;
    public final DataCash dataCash;


    Data(GUIIsReady listener, int capacityInPowerOfTwo){
        this.listener = listener;
        dataCash = new DataCash(this, capacityInPowerOfTwo);
        setCapacity(capacityInPowerOfTwo);
    }

    private void add(Double y){
        arrayOfMeasurements.add(y);
        if(arrayOfMeasurements.size() > capacity)
            arrayOfMeasurements.pollFirst();
    }

    @Override
    public void update(LinkedList<Double> data) {
        data.forEach(this::add);
        if(listener.getReady()) {
            listener.setReady(false);
            listener.update(new ArrayList<>(arrayOfMeasurements));
        }
    }

    public void setCapacity(int capacityInPowerOfTwo){
        if(capacityInPowerOfTwo < 7)
            capacityInPowerOfTwo = 7;

        capacity = 1 << capacityInPowerOfTwo;
        if(arrayOfMeasurements.size() > capacity) {
            while (arrayOfMeasurements.size() > capacity){
                arrayOfMeasurements.pollFirst();
            }
        } else while (arrayOfMeasurements.size() < capacity){
            arrayOfMeasurements.add(0.0);
        }

        dataCash.setCapacityInPowerOfTwo(capacityInPowerOfTwo);
    }

//    long t0 = System.nanoTime();
//    long t1 = System.nanoTime();
//    System.out.println(String.format("Reduces points from in %.1f ms", (t1 - t0) / 1e6));
}
