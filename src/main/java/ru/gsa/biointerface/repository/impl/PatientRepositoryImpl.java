package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.repository.PatientRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRepositoryImpl extends AbstractRepository<Patient, Integer> implements PatientRepository {
    private static PatientRepository repository;

    private PatientRepositoryImpl() throws Exception {
        super();
    }

    public static PatientRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new PatientRepositoryImpl();
        }

        return repository;
    }

    @Override
    public Patient save(Patient entity) throws Exception {
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
