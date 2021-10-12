package ru.gsa.biointerface.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;

import java.sql.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DBHandler implements DB {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHandler.class);
    private static final String url = "jdbc:sqlite:./BCsqLite.s3db";
    private static DBHandler instance = null;
    private static Connection connection = null;

    private DBHandler() throws PersistenceException {

        try {
            DriverManager.registerDriver(new JDBC());
            LOGGER.info("Registered database driver");
        } catch (SQLException e) {
            throw new PersistenceException("Database driver registration error", e);
        }

        connection = getConnection();

        boolean IcdIsNotPresent = true;
        boolean patientRecordIsNotPresent = true;
        boolean deviceIsNotPresent = true;
        boolean channelIsNotPresent = true;
        boolean examinationIsNotPresent = true;
        boolean graphIsNotPresent = true;
        boolean samplesIsNotPresent = true;

        try (PreparedStatement statement = connection.prepareStatement(SQL.SHOW_TABLES.QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                if ("Icd".equals(resultSet.getString(1)))
                    IcdIsNotPresent = false;

                if ("PatientRecord".equals(resultSet.getString(1)))
                    patientRecordIsNotPresent = false;

                if ("Device".equals(resultSet.getString(1)))
                    deviceIsNotPresent = false;

                if ("Channel".equals(resultSet.getString(1)))
                    channelIsNotPresent = false;

                if ("Examination".equals(resultSet.getString(1)))
                    examinationIsNotPresent = false;

                if ("Graph".equals(resultSet.getString(1)))
                    graphIsNotPresent = false;

                if ("Sample".equals(resultSet.getString(1)))
                    samplesIsNotPresent = false;

            }
        } catch (SQLException e) {
            throw new PersistenceException("SQL statement creation error", e);
        }

        try (Statement statement = connection.createStatement()) {
            if (IcdIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_ICD.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'Icd' not found and was created");
            }

            if (patientRecordIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_PATIENT_RECORD.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'PatientRecord' not found and was created");
            }

            if (deviceIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_DEVICE.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'Device' not found and was created");
            }

            if (channelIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_CHANNEL.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'Channel' not found and was created");
            }

            if (examinationIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_EXAMINATION.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'Examination' not found and was created");
            }

            if (graphIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_GRAPH.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'Graph' not found and was created");
            }

            if (samplesIsNotPresent) {
                statement.execute(SQL.FOREIGN_KEYS_OFF.QUERY);
                statement.execute(SQL.CREATE_TABLE_SAMPLE.QUERY);
                statement.execute(SQL.FOREIGN_KEYS_ON.QUERY);
                LOGGER.info("Table 'Sample' not found and was created");
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQL statement creation error", e);
        }
    }

    public static synchronized DB getInstance() throws PersistenceException {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }

    @Override
    public Connection getConnection() throws PersistenceException {
        if (connection == null) {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            try {
                connection = DriverManager.getConnection(url, config.toProperties());
                LOGGER.info("Created a connection to database");
            } catch (SQLException e) {
                throw new PersistenceException("Error connecting to database", e);
            }
        }
        return connection;
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
            LOGGER.info("Connection to database is close");
        } catch (SQLException e) {
            LOGGER.error("Error closing connecting to database: {}", e.getMessage());
        }
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
