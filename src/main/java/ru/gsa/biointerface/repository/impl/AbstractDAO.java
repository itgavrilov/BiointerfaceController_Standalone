package ru.gsa.biointerface.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.repository.database.DatabaseHandler;
import ru.gsa.biointerface.repository.exception.DeleteException;
import ru.gsa.biointerface.repository.exception.InsertException;
import ru.gsa.biointerface.repository.exception.ReadException;
import ru.gsa.biointerface.repository.exception.UpdateException;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public abstract class AbstractDAO<Entity, Key> {
    @SuppressWarnings("unchecked")
    protected final Class<Entity> genericType = (Class<Entity>)
            ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
    protected final Logger LOGGER = LoggerFactory.getLogger(genericType.getSimpleName() + "Repository");
    protected final SessionFactory sessionFactory;

    protected AbstractDAO() throws Exception {
        sessionFactory = DatabaseHandler.getInstance().getSessionFactory();
    }

    public void insert(Entity entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
            LOGGER.info("Entity insert successful");
        } catch (Exception e) {
            LOGGER.error("Insert entity error", e);
            throw new InsertException(e);
        }
    }

    public Entity getById(Key key) throws Exception {
        if (key == null)
            throw new NullPointerException("Key is null");

        Entity entity;

        try (final Session session = sessionFactory.openSession()) {
            entity = session.get(genericType, (Serializable) key);
            LOGGER.info("Entity read successful");
        } catch (Exception e) {
            LOGGER.error("Read entity error", e);
            throw new ReadException(e);
        }

        return entity;
    }

    public void update(Entity entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
            LOGGER.info("Entity update successful");
        } catch (Exception e) {
            LOGGER.error("Update entity error", e);
            throw new UpdateException(e);
        }
    }

    public void delete(Entity entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
            LOGGER.info("Entity delete successful");
        } catch (Exception e) {
            LOGGER.error("Delete entity error", e);
            throw new DeleteException(e);
        }
    }
}
