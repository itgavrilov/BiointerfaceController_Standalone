package ru.gsa.biointerface.domain.host.cash;

import ru.gsa.biointerface.domain.DomainException;

public interface Cash {
    void addListener(DataListener listener);

    void add(int val) throws DomainException;
}
