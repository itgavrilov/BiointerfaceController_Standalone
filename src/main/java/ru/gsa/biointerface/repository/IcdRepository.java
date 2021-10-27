package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.database.AbstractRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdRepository extends AbstractRepository<Icd, Long> {
    private static IcdRepository dao;

    private IcdRepository() throws Exception {
        super();
    }

    public static IcdRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new IcdRepository();
        }

        return dao;
    }
}
