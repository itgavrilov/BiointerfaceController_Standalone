package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleDAO extends AbstractDAO<SampleEntity, Integer> {
    protected static SampleDAO dao;

    private SampleDAO() throws PersistenceException {
        super();
    }

    public static SampleDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new SampleDAO();

        return dao;
    }

    public List<SampleEntity> getAllByGraph(GraphEntity graphEntity) throws PersistenceException {
        List<SampleEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "FROM sample where numberOfChannel = :numberOfChannel and examination_id = :examination_id";
            //noinspection unchecked
            Query<SampleEntity> query = session.createQuery(hql);
            query.setParameter("numberOfChannel", graphEntity.getNumberOfChannel());
            query.setParameter("examination_id", graphEntity.getExaminationEntity().getId());

            entities = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}