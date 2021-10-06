package ru.gsa.biointerface.domain;


import java.util.Deque;

public interface DataListener {
    boolean isReady();
    void setNewSamples(Deque<Integer> data) throws DomainException;
}

