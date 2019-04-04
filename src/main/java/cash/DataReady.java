package cash;

import java.util.List;

public interface DataReady<T extends Number> {
    void update(List<T> data);
    boolean listnenerIsReady();
}

