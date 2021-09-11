package ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.dataCash;

import java.util.LinkedList;

public interface DataCashListener<T> {
    void update(LinkedList<T> data);
}

