package channel.dataCash;

import java.util.LinkedList;
/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */

public final class DataCash<T extends Number>{
    private final DataCashListener dataCashListener;
    private int updateCountInPowerOfTwo;
    private int updateCount = 1 << updateCountInPowerOfTwo;
    private LinkedList<T> dataInDataCash = new LinkedList<>();

    public DataCash(DataCashListener dataCashListener, int updateCountInPowerOfTwo) {
        this.dataCashListener = dataCashListener;
        this.updateCountInPowerOfTwo = updateCountInPowerOfTwo;
    }

    public void add(T val){
        dataInDataCash.add(val);
        if (dataInDataCash.size() >= updateCount) {
            dataCashListener.update(dataInDataCash);
            dataInDataCash.clear();
        }
    }

    public void setCapacityInPowerOfTwo(int updateCountInPowerOfTwo){
        if(updateCountInPowerOfTwo > 7) {
            this.updateCountInPowerOfTwo = updateCountInPowerOfTwo - 4;
        } else {
            this.updateCountInPowerOfTwo = 3;
        }
        updateCount = 1 << this.updateCountInPowerOfTwo;
    }
}
