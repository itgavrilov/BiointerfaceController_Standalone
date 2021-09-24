package ru.gsa.biointerface.domain.host.dataCash;

import ru.gsa.biointerface.domain.DomainException;

public interface Cash {
    void add(int val) throws DomainException;
}
