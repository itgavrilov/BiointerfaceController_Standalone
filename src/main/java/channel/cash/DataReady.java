package channel.cash;

import java.util.LinkedList;

public interface DataReady<T extends Number> {
    void update(LinkedList<T> data);
}

