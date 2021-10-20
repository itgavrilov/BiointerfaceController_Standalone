package ru.gsa.biointerface.domain.host.cash;


import ru.gsa.biointerface.domain.DomainException;

import java.util.Deque;

public interface DataListener {
    void setNewSamples(Deque<Integer> data) throws DomainException;
}

