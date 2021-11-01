package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdRepositoryImpl extends AbstractRepository<Icd, Long> implements IcdRepository {
    private static IcdRepository dao;

    private IcdRepositoryImpl() throws Exception {
        super();
    }

    public static IcdRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new IcdRepositoryImpl();
        }

        return dao;
    }
}
