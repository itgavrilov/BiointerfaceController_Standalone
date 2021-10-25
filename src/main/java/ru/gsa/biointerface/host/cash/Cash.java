package ru.gsa.biointerface.host.cash;

public interface Cash {
    void setListener(DataListener listener);

    void add(int val);
}
