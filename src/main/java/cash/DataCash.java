package cash;

import java.util.LinkedList;

public class DataCash<T extends Number> {
    private final DataReady<T> listener;

    private final LinkedList<T> dataInDataCash = new LinkedList();
    private int powerOfTwoForUpdateCount = 5;
    private int updateCount = 1 << powerOfTwoForUpdateCount;

    public DataCash(DataReady listener) {
        this.listener = listener;
    }

    public void add(T val){
        dataInDataCash.add(val);

        if(dataInDataCash.size() >= updateCount && listener.listnenerIsReady()) {
            listener.update(new LinkedList<>(dataInDataCash));
            dataInDataCash.clear();
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
