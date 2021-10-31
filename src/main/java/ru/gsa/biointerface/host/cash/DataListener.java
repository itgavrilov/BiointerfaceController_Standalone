package ru.gsa.biointerface.host.cash;

import java.util.Deque;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */
public interface DataListener {
    void setNewSamples(Deque<Integer> data);
}

