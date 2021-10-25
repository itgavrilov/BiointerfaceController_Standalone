package ru.gsa.biointerface.repository.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.repository.exception.*;

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
    protected final Logger LOGGER = LoggerFactory.getLogger(genericType.getSimpleName()+"Repository");
    protected final SessionFactory sessionFactory;

    protected AbstractDAO() throws NoConnectionException {
        sessionFactory = DatabaseHandler.getInstance().getSessionFactory();
    }

    public void insert(Entity entity) throws InsertException, TransactionNotOpenException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
            LOGGER.info("Entity insert successful");
        } catch (Exception e){
            LOGGER.error("Insert entity error", e);
            throw new InsertException(e);
        }
    }

    public Entity read(Key key) throws ReadException {
        if (key == null)
            throw new NullPointerException("Key is null");

        Entity entity;

        try (final Session session = sessionFactory.openSession()) {
            entity = session.get(genericType, (Serializable) key);
            LOGGER.info("Entity read successful");
        } catch (Exception e){
            LOGGER.error("Read entity error", e);
            throw new ReadException(e);
        }

        return entity;
    }

    public void update(Entity entity) throws UpdateException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
            LOGGER.info("Entity update successful");
        } catch (Exception e){
            LOGGER.error("Update entity error", e);
            throw new UpdateException(e);
        }
    }

    public void delete(Entity entity) throws DeleteException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
            LOGGER.info("Entity delete successful");
        } catch (Exception e){
            LOGGER.error("Delete entity error", e);
            throw new DeleteException(e);
        }
    }
}
