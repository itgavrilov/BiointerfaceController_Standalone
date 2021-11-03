package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordRepositoryImpl extends AbstractRepository<PatientRecord, Integer> implements PatientRecordRepository {
    private static PatientRecordRepository repository;

    private PatientRecordRepositoryImpl() throws Exception {
        super();
    }

    public static PatientRecordRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new PatientRecordRepositoryImpl();
        }

        return repository;
    }

    @Override
    public PatientRecord save(PatientRecord entity) throws Exception {
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
