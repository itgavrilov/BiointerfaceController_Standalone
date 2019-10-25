package channel;

import java.util.*;

public class ChannelData implements DataReady {
    private final ChartReady listener;
    private LinkedList<Double> arrayOfMeasurements = new LinkedList();
    private int capacity;
    private int lastIndex = 0;
    public final DataCash dataCash = new DataCash(this);


    ChannelData(ChartReady listener, int capacityInPowerOfTwo){
        this.listener = listener;
        setCapacity(capacityInPowerOfTwo);
    }

    @Override
    public void update(LinkedList<Double> data) {
        data.forEach(this::add);
        if(listener.getReady()) {
            listener.setReady(false);
            listener.update(new ArrayList<>(arrayOfMeasurements));
        }
    }

    private void add(Double y){
        arrayOfMeasurements.set(lastIndex, y);
        if(lastIndex < capacity-1)
            lastIndex++;
        else
            lastIndex = 0;
    }

    public void setCapacity(int capacityInPowerOfTwo){
        int tmpCapacityInPowerOfTwo;
        if(capacityInPowerOfTwo > 7)
            tmpCapacityInPowerOfTwo = capacityInPowerOfTwo;
        else
            tmpCapacityInPowerOfTwo = 7;

        capacity = 1 << tmpCapacityInPowerOfTwo;
        if(arrayOfMeasurements.size() > capacity) {
            while (arrayOfMeasurements.size() > capacity){
                arrayOfMeasurements.pollFirst();
            }
        } else while (arrayOfMeasurements.size() < capacity){
            arrayOfMeasurements.add(0.0);
        }
        dataCash.setCapacityInPowerOfTwo(tmpCapacityInPowerOfTwo);
    }

//    long t0 = System.nanoTime();
//    long t1 = System.nanoTime();
//    System.out.println(String.format("Reduces points from in %.1f ms", (t1 - t0) / 1e6));
}
