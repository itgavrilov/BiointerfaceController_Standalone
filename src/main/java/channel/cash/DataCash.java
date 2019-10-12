package channel.cash;

import java.util.LinkedList;

public class DataCash<T extends Number> {
    private final DataReady<T> listener;

    private int powerOfTwoForUpdateCount = 5;
    private int updateCount = 1 << powerOfTwoForUpdateCount;
    private LinkedList<T> dataInDataCash = new LinkedList<>();

    public DataCash(DataReady<T> listener) {
        this.listener = listener;
    }

    public void add(T val){
        dataInDataCash.add(val);

        if (dataInDataCash.size() >= updateCount) {
            listener.update(dataInDataCash);
            dataInDataCash = new LinkedList<>();
        }
    }

    public void setPowerOfTwoForUpdateCount(int powerOfTwo){
        if(powerOfTwo > 7)
            powerOfTwoForUpdateCount = powerOfTwo-5;
        else
            powerOfTwoForUpdateCount = 3;

        updateCount = 1 << powerOfTwoForUpdateCount;
    }
}
