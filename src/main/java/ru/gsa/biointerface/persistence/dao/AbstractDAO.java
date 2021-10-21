package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.gsa.biointerface.persistence.DBHandler;
import ru.gsa.biointerface.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public abstract class AbstractDAO<Entity, Key> implements DAO<Entity, Key> {
    protected final SessionFactory sessionFactory;
    @SuppressWarnings("unchecked")
    private final Class<Entity> genericType = (Class<Entity>)
            ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];

    protected AbstractDAO() throws PersistenceException {
        sessionFactory = DBHandler.getInstance().getSessionFactory();
    }

    @Override
    public Entity insert(Entity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }

    @Override
    public Entity read(Key key) throws PersistenceException {
        Entity entity;

        try (final Session session = sessionFactory.openSession()) {
            entity = session.get(genericType, (Serializable) key);
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entity;
    }

    @Override
    public boolean update(Entity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return true;
    }

    @Override
    public boolean delete(Entity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("PatientRecord is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return true;
    }

    public List<Entity> getAll() throws PersistenceException {
        List<Entity> entities;

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Entity> cq = cb.createQuery(genericType);
            cq.from(genericType);

            entities = session.createQuery(cq).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new PersistenceException("Session error", e);
        }

        return entities;
    }
}
