package ru.gsa.biointerface.domain.host.dataCash;

import ru.gsa.biointerface.domain.DataListener;
import ru.gsa.biointerface.domain.DomainException;

public interface Cash {
    void addListener(DataListener listener);
    void add(int val) throws DomainException;
}
