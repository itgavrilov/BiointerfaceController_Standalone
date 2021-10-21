package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class GraphDAO extends AbstractDAO<GraphEntity, Integer> {
    protected static GraphDAO dao;

    private GraphDAO() throws PersistenceException {
        super();
    }

    public static GraphDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new GraphDAO();

        return dao;
    }

    public List<GraphEntity> getAllByExamination(ExaminationEntity examinationEntity) throws PersistenceException {
        List<GraphEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            String hql = "FROM graph where examination_id  = :id";
            //noinspection unchecked
            Query<GraphEntity> query = session.createQuery(hql);
            query.setParameter("id", examinationEntity.getId());

            entities = query.list();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}