package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdRepository extends AbstractRepository<Icd, Long> {
    protected static IcdRepository dao;

    private IcdRepository() throws NoConnectionException {
        super();
    }

    public static IcdRepository getInstance() throws NoConnectionException {
        if (dao == null)
            dao = new IcdRepository();

        return dao;
    }
}
