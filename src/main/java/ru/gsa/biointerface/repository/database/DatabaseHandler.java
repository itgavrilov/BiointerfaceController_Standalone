package ru.gsa.biointerface.repository.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

import javax.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DatabaseHandler implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandler.class);
    private static DatabaseHandler instance = null;
    private final SessionFactory sessionFactory;

    private DatabaseHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static Database getInstance() throws NoConnectionException {
        if (instance == null) {
            try {
                Configuration cfg = new Configuration()
                        .addAnnotatedClass(Sample.class)
                        .addAnnotatedClass(Channel.class)
                        .addAnnotatedClass(Examination.class)
                        .addAnnotatedClass(Device.class)
                        .addAnnotatedClass(ChannelName.class)
                        .addAnnotatedClass(Patient.class)
                        .addAnnotatedClass(Icd.class);
                instance = new DatabaseHandler(cfg.buildSessionFactory());
                LOGGER.info("Successful database connection");
            } catch (PersistenceException e) {
                LOGGER.error("Error connecting to database", e);
                throw new NoConnectionException(e);
            }
        }

        return instance;
    }

    public static void constructInstanceForTest() throws NoConnectionException {
        if (instance == null) {
            try {
                Configuration cfg = new Configuration()
                        .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                        .setProperty("hibernate.connection.url", "jdbc:h2:mem:test")
                        .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                        .addAnnotatedClass(Sample.class)
                        .addAnnotatedClass(Channel.class)
                        .addAnnotatedClass(Examination.class)
                        .addAnnotatedClass(Device.class)
                        .addAnnotatedClass(ChannelName.class)
                        .addAnnotatedClass(Patient.class)
                        .addAnnotatedClass(Icd.class);
                instance = new DatabaseHandler(cfg.buildSessionFactory());
                LOGGER.info("Successful database connection");
            } catch (PersistenceException e) {
                LOGGER.error("Error connecting to database", e);
                throw new NoConnectionException(e);
            }
        }

    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
