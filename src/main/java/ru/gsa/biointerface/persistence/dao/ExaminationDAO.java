package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationDAO extends AbstractDAO<Examination, Long> {
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
    public Examination insert(Examination entity) throws PersistenceException {
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

    public List<Examination> getByDevice(Device device) throws PersistenceException {
        List<Examination> entities;

        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM examination where device_id  = :id";
            //noinspection unchecked
            Query<Examination> query = session.createQuery(hql);
            query.setParameter("id", device.getId());

            entities = query.list();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }

    public List<Examination> getByPatientRecord(PatientRecord patientRecord) throws PersistenceException {
        List<Examination> entities;

        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM examination where patientRecord_id = :id";
            //noinspection unchecked
            Query<Examination> query = session.createQuery(hql);
            query.setParameter("id", patientRecord.getId());

            entities = query.list();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }

    public void transactionStart() throws PersistenceException {
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

    public void transactionStop() throws PersistenceException {
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
        return session != null && session.isOpen();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean transactionIsOpen() {
        return sessionIsOpen() && session.getTransaction().isActive();
    }
}
