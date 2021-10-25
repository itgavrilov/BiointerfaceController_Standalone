package ru.gsa.biointerface.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.*;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationRepository extends AbstractRepository<Examination, Long> {
    protected static ExaminationRepository dao;
    private static Session session;

    private ExaminationRepository() throws NoConnectionException {
        super();
    }

    public static ExaminationRepository getInstance() throws NoConnectionException {
        if (dao == null)
            dao = new ExaminationRepository();

        return dao;
    }


    @Override
    public void insert(Examination entity) throws TransactionNotOpenException, InsertException {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        try {
            session.save(entity);
            LOGGER.info("Entity insert successful");
        } catch (Exception e){
            LOGGER.error("Insert entity error", e);
            throw new InsertException(e);
        }
    }

    public List<Examination> getByPatientRecord(PatientRecord patientRecord) throws ReadException {
        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM examination where patientRecord_id = :id";
            //noinspection unchecked
            Query<Examination> query = session.createQuery(hql);
            query.setParameter("id", patientRecord.getId());
            List<Examination> entities = query.list();
            LOGGER.info("Reading entities by patientRecord is successful");

            return entities;
        } catch (Exception e){
            LOGGER.error("Error reading entities by patientRecord", e);
            throw new ReadException(e);
        }
    }

    public void transactionOpen() throws TransactionNotOpenException {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            LOGGER.info("Transaction open is successful");
        }catch (Exception e){
            LOGGER.error("Transaction opening error", e);
            throw new TransactionNotOpenException(e);
        }
    }

    public void transactionClose() throws TransactionNotOpenException, TransactionStopException {
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        try {
            session.flush();
            session.getTransaction().commit();
            session.close();
            LOGGER.info("Transaction close is successful");
        } catch (Exception e){
            LOGGER.error("Transaction closing error", e);
            throw new TransactionStopException(e);
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
