package ru.gsa.biointerface.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DBHandler implements DB {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandler.class);
    private static DBHandler instance = null;

    private final SessionFactory sessionFactory;

    private DBHandler() throws PersistenceException {

        try {
            System.out.println(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);
            Configuration cfg = new Configuration()
                    .addAnnotatedClass(SampleEntity.class)
                    .addAnnotatedClass(GraphEntity.class)
                    .addAnnotatedClass(ExaminationEntity.class)
                    .addAnnotatedClass(DeviceEntity.class)
                    .addAnnotatedClass(ChannelEntity.class)
                    .addAnnotatedClass(PatientRecordEntity.class)
                    .addAnnotatedClass(IcdEntity.class);

            sessionFactory = cfg.buildSessionFactory();
            LOGGER.info("Successful database connection");
        } catch (Exception e) {
            throw new PersistenceException("Error connecting to database", e);
        }
    }

    public static DB getInstance() throws PersistenceException {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private enum SQL {
        SHOW_TABLES("""
                SELECT name FROM sqlite_master
                WHERE type IN ('table')
                AND name NOT LIKE 'sqlite_%';
                """),
        CREATE_TABLE_PATIENT_RECORD("""
                CREATE TABLE PatientRecord (
                id INTEGER PRIMARY KEY UNIQUE NOT NULL,
                secondName VARCHAR(35)  NOT NULL,
                firstName VARCHAR(35)  NOT NULL,
                middleName VARCHAR(35)  NULL,
                birthday DATE  NOT NULL,
                Icd_id INTEGER NULL,
                comment TEXT  NULL,
                CONSTRAINT fk_icd FOREIGN KEY (Icd_id) REFERENCES Icd (id)
                );
                """),
        CREATE_TABLE_ICD("""
                CREATE TABLE Icd (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ICD VARCHAR(35) NOT NULL,
                version INTEGER NOT NULL,
                comment TEXT NULL
                );
                """),
        CREATE_TABLE_DEVICE("""
                CREATE TABLE Device (
                id INTEGER PRIMARY KEY NOT NULL,
                amountChannels INTEGER DEFAULT '8' NOT NULL,
                comment TEXT NULL
                );
                """),
        CREATE_TABLE_EXAMINATION("""
                CREATE TABLE Examination (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                patientRecord_id INTEGER NOT NULL,
                device_id INTEGER NOT NULL,
                comment TEXT NULL,
                CONSTRAINT fk_patientRecord FOREIGN KEY (patientRecord_id) REFERENCES PatientRecord(id) ON DELETE CASCADE,
                CONSTRAINT fk_device FOREIGN KEY (device_id) REFERENCES Device(id) ON DELETE CASCADE
                );
                """),
        CREATE_TABLE_CHANNEL("""
                CREATE TABLE Channel (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name VARCHAR(35) UNIQUE NOT NULL,
                comment TEXT NULL
                );
                """),
        CREATE_TABLE_GRAPH("""
                CREATE TABLE Graph (
                numberOfChannel INTEGER NOT NULL,
                examination_id INTEGER NOT NULL,
                channel_id INTEGER  NULL,
                PRIMARY KEY (numberOfChannel,examination_id)
                CONSTRAINT fk_Examination FOREIGN KEY (examination_id) REFERENCES Examination(id) ON DELETE CASCADE,
                CONSTRAINT fk_Channel FOREIGN KEY (channel_id) REFERENCES Channel(id)
                );
                """),

        CREATE_TABLE_SAMPLE("""
                CREATE TABLE Sample (
                id INTEGER NOT NULL,
                numberOfChannel INTEGER NOT NULL,
                examination_id INTEGER NOT NULL,
                value INTEGER NOT NULL,
                PRIMARY KEY (id, numberOfChannel, examination_id),
                CONSTRAINT fk_Graph FOREIGN KEY (numberOfChannel, examination_id) REFERENCES Graph(numberOfChannel, examination_id) ON DELETE CASCADE
                );
                """),
        FOREIGN_KEYS_ON("PRAGMA foreign_keys=on;"),
        FOREIGN_KEYS_OFF("PRAGMA foreign_keys=off;");
        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
