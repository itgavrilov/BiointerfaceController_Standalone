package ru.gsa.biointerfaceController_standalone.daoLayer;

import org.sqlite.JDBC;

import java.sql.*;

public class DBHandler implements DB {
    private static DBHandler instance = null;
    private String url = "jdbc:sqlite:./BCsqLite.s3db";

    private DBHandler() throws DAOException {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("registerDriver error", e);
        }

        try (Connection connection = getConnection()) {
            boolean patientRecordIsNotPresent = true;
            boolean deviceIsNotPresent = true;
            boolean examinationIsNotPresent = true;
            boolean samplesIsNotPresent = true;
            boolean IcdIsNotPresent = true;

            try (PreparedStatement statement = connection.prepareStatement(SQL.SHOW_TABLES.QUERY);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if ("PatientRecord".equals(resultSet.getString(1)))
                        patientRecordIsNotPresent = false;

                    if ("Device".equals(resultSet.getString(1)))
                        deviceIsNotPresent = false;

                    if ("Examination".equals(resultSet.getString(1)))
                        examinationIsNotPresent = false;

                    if ("Samples".equals(resultSet.getString(1)))
                        samplesIsNotPresent = false;

                    if ("Icd".equals(resultSet.getString(1)))
                        IcdIsNotPresent = false;
                }
            }

            try (Statement statement = connection.createStatement()) {
                if (patientRecordIsNotPresent)
                    statement.execute(SQL.CREATE_TABLE_PATIENT_RECORD.QUERY);

                if (deviceIsNotPresent)
                    statement.execute(SQL.CREATE_TABLE_DEVICE.QUERY);

                if (examinationIsNotPresent)
                    statement.execute(SQL.CREATE_TABLE_EXAMINATION.QUERY);

                if (samplesIsNotPresent)
                    statement.execute(SQL.CREATE_TABLE_SAMPLES.QUERY);

                if (IcdIsNotPresent) {
                    statement.execute(SQL.CREATE_TABLE_ICD.QUERY);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("error when initializing the database", e);
        }
    }

    public static synchronized DB getInstance() throws DAOException {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }

    @Override
    public Connection getConnection() throws DAOException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }
    }

    private enum SQL {
        SHOW_TABLES("SELECT name FROM sqlite_master \n" +
                "WHERE type IN ('table') \n" +
                "AND name NOT LIKE 'sqlite_%';"),
        CREATE_TABLE_PATIENT_RECORD("CREATE TABLE PatientRecord (\n" +
                "id INTEGER PRIMARY KEY UNIQUE NOT NULL,\n" +
                "secondName VARCHAR(35)  NOT NULL,\n" +
                "firstName VARCHAR(35)  NOT NULL,\n" +
                "middleName VARCHAR(35)  NULL,\n" +
                "birthday DATE  NOT NULL,\n" +
                "Icd_id INTEGER NULL,\n" +
                "comment TEXT  NULL,\n" +
                "FOREIGN KEY (Icd_id) REFERENCES Icd (id)\n" +
                ");"),
        CREATE_TABLE_ICD("CREATE TABLE Icd (\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "ICD VARCHAR(35) NOT NULL,\n" +
                "version INTEGER NOT NULL,\n" +
                "comment TEXT NULL\n" +
                ");"),
        CREATE_TABLE_DEVICE("CREATE TABLE Device (\n" +
                "id INTEGER PRIMARY KEY NOT NULL,\n" +
                "countOfChannels INTEGER DEFAULT '8' NOT NULL,\n" +
                "comment TEXT NULL\n" +
                ");"),
        CREATE_TABLE_EXAMINATION("CREATE TABLE Examination (\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "patientRecord_id INTEGER NOT NULL,\n" +
                "device_id INTEGER NOT NULL,\n" +
                "comment TEXT NULL,\n" +
                "FOREIGN KEY (patientRecord_id) REFERENCES PatientRecord (id),\n" +
                "FOREIGN KEY (device_id) REFERENCES Device (id)\n" +
                ");"),
        CREATE_TABLE_SAMPLES("CREATE TABLE Samples (\n" +
                "id INTEGER NOT NULL,\n" +
                "Examination_id INTEGER NOT NULL,\n" +
                "channel_id INTEGER NOT NULL,\n" +
                "value INTEGER NOT NULL,\n" +
                "PRIMARY KEY (id,Examination_id,channel_id),\n" +
                "FOREIGN KEY (Examination_id) REFERENCES Examination (id)\n" +
                ");");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
