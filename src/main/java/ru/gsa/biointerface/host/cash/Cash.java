package ru.gsa.biointerface.host.cash;

public interface Cash {
    void addListener(DataListener listener);

    void add(int val);
}
