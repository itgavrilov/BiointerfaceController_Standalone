package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.Graph;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleDAO extends AbstractDAO<Sample, Long> {
    protected static SampleDAO dao;

    private SampleDAO() throws PersistenceException {
        super();
    }

    public static SampleDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new SampleDAO();

        return dao;
    }

    public List<Sample> getAllByGraph(Graph graph) throws PersistenceException {
        List<Sample> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "FROM sample where numberOfChannel = :numberOfChannel and examination_id = :examination_id";
            //noinspection unchecked
            Query<Sample> query = session.createQuery(hql);
            query.setParameter("numberOfChannel", graph.getNumberOfChannel());
            query.setParameter("examination_id", graph.getExaminationEntity().getId());

            entities = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}