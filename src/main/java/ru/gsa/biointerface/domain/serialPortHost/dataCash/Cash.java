package ru.gsa.biointerface.domain.serialPortHost.dataCash;

import ru.gsa.biointerface.domain.DomainException;

public interface Cash {
    void add(int val) throws DomainException;
}
