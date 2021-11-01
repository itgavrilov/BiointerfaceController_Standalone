package ru.gsa.biointerface.repository.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.domain.entity.SampleID;
import ru.gsa.biointerface.repository.SampleRepository;
import ru.gsa.biointerface.repository.exception.ReadException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleRepositoryImpl extends AbstractRepository<Sample, SampleID> implements SampleRepository {
    private static SampleRepository dao;

    private SampleRepositoryImpl() throws Exception {
        super();
    }

    public static SampleRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new SampleRepositoryImpl();
        }

        return dao;
    }

    public List<Sample> getAllByChannel(Channel channel) throws Exception {
        List<Sample> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "FROM sample where channel_id = :channel_id and examination_id = :examination_id";
            //noinspection unchecked
            Query<Sample> query = session.createQuery(hql);
            query.setParameter("channel_id", channel.getId());
            query.setParameter("examination_id", channel.getExamination().getId());

            entities = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new ReadException(e);
        }

        return entities;
    }
}