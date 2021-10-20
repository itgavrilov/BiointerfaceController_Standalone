package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdDAO extends AbstractDAO<IcdEntity, Integer> {
    protected static IcdDAO dao;

    private IcdDAO() throws PersistenceException {
        super();
    }

    public static IcdDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new IcdDAO();

        return dao;
    }

    @Override
    public IcdEntity read(Integer key) throws PersistenceException {
        IcdEntity entity;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            entity = session.get(IcdEntity.class, key);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }

    @Override
    public List<IcdEntity> getAll() throws PersistenceException {
        List<IcdEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<IcdEntity> cq = cb.createQuery(IcdEntity.class);
            cq.from(IcdEntity.class);

            entities = session.createQuery(cq).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}
