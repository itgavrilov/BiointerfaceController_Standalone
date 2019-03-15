package cash;

import java.util.List;

public interface Listener<T> {
    void update(List<T> data);
}

