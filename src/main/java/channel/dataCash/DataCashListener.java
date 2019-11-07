package channel.dataCash;

import java.util.LinkedList;

public interface DataCashListener<T> {
    void update(LinkedList<T> data);
}

