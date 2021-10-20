package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationDAO extends AbstractDAO<ExaminationEntity, Integer> {
    protected static ExaminationDAO dao;
    private static Session session;

    private ExaminationDAO() throws PersistenceException {
        super();
    }

    public static ExaminationDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new ExaminationDAO();

        return dao;
    }


    @Override
    public ExaminationEntity insert(ExaminationEntity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (!transactionIsOpen())
            throw new PersistenceException("Transaction is not active");

        try {
            session.save(entity);
        } catch (Exception e) {
            throw new PersistenceException("Save " + entity + " error", e);
        }

        return entity;
    }

    @Override
    public ExaminationEntity read(Integer key) throws PersistenceException {
        ExaminationEntity entity;

        try (final Session session = sessionFactory.openSession()) {
            entity = session.get(ExaminationEntity.class, key);
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }


    public List<ExaminationEntity> getByDevice(DeviceEntity device) throws PersistenceException {
        List<ExaminationEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM examination where device_id  = :id";
            Query<ExaminationEntity> query = session.createQuery(hql);
            query.setParameter("id", device.getId());

            entities = query.list();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }

    public List<ExaminationEntity> getByPatientRecord(PatientRecordEntity patientRecordEntity) throws PersistenceException {
        List<ExaminationEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM examination where patientRecord_id = :id";
            Query<ExaminationEntity> query = session.createQuery(hql);
            query.setParameter("id", patientRecordEntity.getId());

            entities = query.list();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }

    @Override
    public List<ExaminationEntity> getAll() throws PersistenceException {
        List<ExaminationEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ExaminationEntity> cq = cb.createQuery(ExaminationEntity.class);
            cq.from(ExaminationEntity.class);

            entities = session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }


    public void beginTransaction() throws PersistenceException {
        try {
            session = sessionFactory.openSession();
            try {
                session.beginTransaction();
            } catch (Exception e) {
                throw new PersistenceException("BeginTransaction error", e);
            }
        } catch (Exception e) {
            throw new PersistenceException("OpenSession error", e);
        }
    }

    public void endTransaction() throws PersistenceException {
        if (!transactionIsOpen())
            throw new PersistenceException("Transaction is not active");

        try {
            session.flush();
            session.getTransaction().commit();
            try {
                session.close();
            } catch (Exception e) {
                throw new PersistenceException("CloseSession error", e);
            }
        } catch (Exception e) {
            throw new PersistenceException("SetAutoCommit error", e);
        }
    }

    public boolean sessionIsOpen() {
        boolean result = false;

        if (session != null)
            result = session.isOpen();

        return result;
    }

    public boolean transactionIsOpen() {
        boolean result = false;

        if (sessionIsOpen())
            result = session.getTransaction().isActive();

        return result;
    }
}
