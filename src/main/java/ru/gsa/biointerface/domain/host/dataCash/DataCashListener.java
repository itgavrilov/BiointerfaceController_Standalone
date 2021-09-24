package ru.gsa.biointerface.domain.host.dataCash;

import ru.gsa.biointerface.domain.DomainException;

import java.util.LinkedList;

public interface DataCashListener {
    void update(LinkedList<Integer> data) throws DomainException;
}

