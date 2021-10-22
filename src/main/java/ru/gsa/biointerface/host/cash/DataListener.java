package ru.gsa.biointerface.host.cash;


import java.util.Deque;

public interface DataListener {
    void setNewSamples(Deque<Integer> data);
}

