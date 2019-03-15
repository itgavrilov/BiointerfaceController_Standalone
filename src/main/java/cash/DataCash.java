package cash;

import java.util.LinkedList;
import java.util.List;

public class DataCash<T> {
    private List<T> dataInDataCash = new LinkedList<>();
    private Listener<T> listener;
    private int updateCount = 7;

    public DataCash(Listener<T> listener) {
        this.listener = listener;
    }

    public void setUpdateCount (int updateCount) {
        if(updateCount>=3)
            this.updateCount = updateCount;
        else
            this.updateCount = 3;
        dataInDataCash.clear();
    }

    public void add(T item){
        dataInDataCash.add(item);
        if(dataInDataCash.size() > (1<<updateCount)) {
            List<T> clon = new LinkedList();
            clon.addAll(dataInDataCash.subList(0,1<<updateCount));
            listener.update(clon);
            dataInDataCash.clear();
        }
    }
}
