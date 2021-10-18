package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordDAO extends AbstractDAO<PatientRecordEntity, Integer> {
    protected static PatientRecordDAO dao;

    private PatientRecordDAO() throws PersistenceException {
        super();
    }

    public static DAO<PatientRecordEntity, Integer> getInstance() throws PersistenceException {
        if (dao == null)
            dao = new PatientRecordDAO();

        return dao;
    }

    @Override
    public PatientRecordEntity read(Integer key) throws PersistenceException {
        PatientRecordEntity entity;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            entity = session.get(PatientRecordEntity.class, key);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }

    @Override
    public List<PatientRecordEntity> getAll() throws PersistenceException {
        List<PatientRecordEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PatientRecordEntity> cq = cb.createQuery(PatientRecordEntity.class);
            cq.from(PatientRecordEntity.class);

            entities = session.createQuery(cq).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}
