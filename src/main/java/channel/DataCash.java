package channel;

import java.util.LinkedList;

public class DataCash{
    private final DataReady listener;

    private int powerOfTwoForUpdateCount = 5;
    private int updateCount = 1 << powerOfTwoForUpdateCount;
    private LinkedList<Double> dataInDataCash = new LinkedList<>();

    public DataCash(DataReady listener) {
        this.listener = listener;
    }

    public void add(Double val){
        dataInDataCash.add(val);

        if (dataInDataCash.size() >= updateCount) {
            listener.update(dataInDataCash);
            dataInDataCash.clear();
        }
    }

    public void setCapacityInPowerOfTwo(int powerOfTwo){
        if(powerOfTwo > 7)
            powerOfTwoForUpdateCount = powerOfTwo-4;
        else
            powerOfTwoForUpdateCount = 3;

        updateCount = 1 << powerOfTwoForUpdateCount;
    }
}
