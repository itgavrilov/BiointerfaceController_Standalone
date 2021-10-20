package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceDAO extends AbstractDAO<DeviceEntity, Integer> {
    protected static DeviceDAO dao;

    private DeviceDAO() throws PersistenceException {
        super();
    }

    public static DAO<DeviceEntity, Integer> getInstance() throws PersistenceException {
        if (dao == null)
            dao = new DeviceDAO();

        return dao;
    }

    @Override
    public DeviceEntity read(Integer key) throws PersistenceException {
        DeviceEntity entity;

        try (final Session session = sessionFactory.openSession()) {
            entity = session.get(DeviceEntity.class, key);
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }

    @Override
    public List<DeviceEntity> getAll() throws PersistenceException {
        List<DeviceEntity> entities;

        try (final Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DeviceEntity> cq = cb.createQuery(DeviceEntity.class);
            cq.from(DeviceEntity.class);

            entities = session.createQuery(cq).getResultList();

        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}
