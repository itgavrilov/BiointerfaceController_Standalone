package ru.gsa.biointerface.repository.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

import javax.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DatabaseHandler implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandler.class);
    private static DatabaseHandler instance = null;
    private final SessionFactory sessionFactory;

    public static Database getInstance() throws NoConnectionException {
        if (instance == null)
            instance = new DatabaseHandler();
        return instance;
    }

    private DatabaseHandler() throws NoConnectionException {
        try {
            Configuration cfg = new Configuration()
                    .addAnnotatedClass(Sample.class)
                    .addAnnotatedClass(Channel.class)
                    .addAnnotatedClass(Examination.class)
                    .addAnnotatedClass(Device.class)
                    .addAnnotatedClass(ChannelName.class)
                    .addAnnotatedClass(PatientRecord.class)
                    .addAnnotatedClass(Icd.class);

            sessionFactory = cfg.buildSessionFactory();
            LOGGER.info("Successful database connection");
        } catch (PersistenceException e){
            LOGGER.error("Error connecting to database", e);
            throw new NoConnectionException(e);
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
