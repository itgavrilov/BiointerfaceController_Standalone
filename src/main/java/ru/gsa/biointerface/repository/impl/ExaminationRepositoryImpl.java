package ru.gsa.biointerface.repository.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.exception.ReadException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationRepositoryImpl extends AbstractRepository<Examination, Integer> implements ExaminationRepository {
    private static ExaminationRepository repository;

    private ExaminationRepositoryImpl() throws Exception {
        super();
    }

    public static ExaminationRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new ExaminationRepositoryImpl();
        }

        return repository;
    }

    public List<Examination> findAllByPatient(Patient entity) throws Exception {
        try (final Session session = sessionFactory.openSession()) {
            //noinspection JpaQlInspection
            String hql = "FROM examination where patient_id = :id";
            //noinspection unchecked
            Query<Examination> query = session.createQuery(hql);
            query.setParameter("id", entity.getId());
            List<Examination> entities = query.list();
            LOGGER.info("Reading entities by patientRecord is successful");

            return entities;
        } catch (Exception e) {
            LOGGER.error("Error reading entities by patientRecord", e);
            throw new ReadException(e);
        }
    }

    @Override
    public Examination save(Examination entity) throws Exception {
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
