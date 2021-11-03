package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdRepositoryImpl extends AbstractRepository<Icd, Integer> implements IcdRepository {
    private static IcdRepository repository;

    private IcdRepositoryImpl() throws Exception {
        super();
    }

    public static IcdRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new IcdRepositoryImpl();
        }

        return repository;
    }

    @Override
    public Icd save(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        if (entity.getId() > 0 && existsById(entity.getId())) {
            update(entity);
        } else {
            insert(entity);
        }

        return entity;
    }
}
