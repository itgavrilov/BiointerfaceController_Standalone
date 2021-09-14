package ru.gsa.biointerface.domain.serialPortHost.dataCash;

import java.util.LinkedList;

public interface DataCashListener<T> {
    void update(LinkedList<T> data);
}

