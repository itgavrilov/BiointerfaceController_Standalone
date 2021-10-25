package ru.gsa.biointerface.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.repository.exception.ReadException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleRepository extends AbstractRepository<Sample, Long> {
    protected static SampleRepository dao;

    private SampleRepository() throws NoConnectionException {
        super();
    }

    public static SampleRepository getInstance() throws NoConnectionException {
        if (dao == null)
            dao = new SampleRepository();

        return dao;
    }

    public List<Sample> getAllByGraph(Channel channel) throws ReadException {
        List<Sample> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "FROM sample where number = :number and examination_id = :examination_id";
            //noinspection unchecked
            Query<Sample> query = session.createQuery(hql);
            query.setParameter("number", channel.getNumber());
            query.setParameter("examination_id", channel.getExamination().getId());

            entities = query.list();
            session.getTransaction().commit();
        } catch (Exception e){
            throw new ReadException(e);
        }

        return entities;
    }
}