package ru.gsa.biointerface.repository.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.domain.entity.SampleID;
import ru.gsa.biointerface.repository.SampleRepository;
import ru.gsa.biointerface.repository.exception.InsertException;
import ru.gsa.biointerface.repository.exception.ReadException;
import ru.gsa.biointerface.repository.exception.TransactionNotOpenException;
import ru.gsa.biointerface.repository.exception.TransactionStopException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleRepositoryImpl extends AbstractRepository<Sample, SampleID> implements SampleRepository {
    private static SampleRepository repository;
    private static Session session;
    private boolean transactionIsOpen;

    private SampleRepositoryImpl() throws Exception {
        super();
    }

    public static SampleRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new SampleRepositoryImpl();
        }

        return repository;
    }

    public List<Sample> findAllByChannel(Channel entity) throws Exception {
        List<Sample> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "FROM sample where channel_number = :channel_number and examination_id = :examination_id";
            //noinspection unchecked
            Query<Sample> query = session.createQuery(hql);
            query.setParameter("channel_number", entity.getId().getNumber());
            query.setParameter("examination_id", entity.getId().getExamination_id());

            entities = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new ReadException(e);
        }

        return entities;
    }

    @Override
    public Sample insert(Sample entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        try {
            session.save(entity);
            return entity;
        } catch (Exception e) {
            LOGGER.error("Insert entity error", e);
            throw new InsertException(e);
        }
    }

    @Override
    public void transactionOpen() throws Exception {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            LOGGER.info("Transaction open is successful");
            transactionIsOpen = true;
        } catch (Exception e) {
            LOGGER.error("Transaction opening error", e);
            throw new TransactionNotOpenException(e);
        }
    }

    @Override
    public void transactionClose() throws Exception {
        if (!transactionIsOpen())
            throw new TransactionNotOpenException("Transaction is not active");

        transactionIsOpen = false;

        try {
            session.flush();
            session.getTransaction().commit();
            session.close();
            LOGGER.info("Transaction close is successful");
        } catch (Exception e) {
            LOGGER.error("Transaction closing error", e);
            throw new TransactionStopException(e);
        }
    }

    @Override
    public boolean transactionIsOpen() {
        boolean result = session != null &&
                session.isOpen() &&
                session.getTransaction().isActive();

        return result && transactionIsOpen;
    }
}