package ru.gsa.biointerface.persistence.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.DBHandler;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public abstract class AbstractDAO<Entity, Key> implements DAO<Entity, Key> {
    protected final SessionFactory sessionFactory;

    protected AbstractDAO() throws PersistenceException {
        sessionFactory = DBHandler.getInstance().getSessionFactory();
    }

    @Override
    public Entity insert(Entity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
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
}
