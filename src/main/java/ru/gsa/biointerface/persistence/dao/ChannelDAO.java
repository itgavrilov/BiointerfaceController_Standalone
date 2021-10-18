package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelDAO extends AbstractDAO<ChannelEntity, Integer> {
    protected static ChannelDAO dao;

    private ChannelDAO() throws PersistenceException {
        super();
    }

    public static ChannelDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new ChannelDAO();

        return dao;
    }

    @Override
    public ChannelEntity read(Integer key) throws PersistenceException {
        ChannelEntity entity;

        try (final Session session = sessionFactory.openSession()) {
            entity = session.get(ChannelEntity.class, key);
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }

    @Override
    public List<ChannelEntity> getAll() throws PersistenceException {
        List<ChannelEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ChannelEntity> cq = cb.createQuery(ChannelEntity.class);
            cq.from(ChannelEntity.class);

            entities = session.createQuery(cq).getResultList();

        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}
