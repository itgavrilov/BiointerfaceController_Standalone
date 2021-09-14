package ru.gsa.biointerface.persistence;

import org.sqlite.JDBC;

import java.sql.*;

public class DBHandler implements DB {

    private final static String url = "jdbc:sqlite:./BCsqLite.s3db";
    private static DBHandler instance = null;
    private static Connection connection = null;


    private DBHandler() throws DAOException {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("registerDriver error", e);
        }

        connection = getConnection();

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static synchronized DB getInstance() throws DAOException {
        if (instance == null)
            instance = new DBHandler();
        return instance;
    }

    @Override
    public Connection getConnection() throws DAOException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("database connection error", e);
            }
        }
        return connection;
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private enum SQL {
        SHOW_TABLES("SELECT name FROM sqlite_master " +
                "WHERE type IN ('table') " +
                "AND name NOT LIKE 'sqlite_%';"),
        CREATE_TABLE_PATIENT_RECORD("CREATE TABLE PatientRecord (" +
                "id INTEGER PRIMARY KEY UNIQUE NOT NULL," +
                "secondName VARCHAR(35)  NOT NULL," +
                "firstName VARCHAR(35)  NOT NULL," +
                "middleName VARCHAR(35)  NULL," +
                "birthday DATE  NOT NULL," +
                "Icd_id INTEGER NULL," +
                "comment TEXT  NULL," +
                "FOREIGN KEY (Icd_id) REFERENCES Icd (id)" +
                ");"),
        CREATE_TABLE_ICD("CREATE TABLE Icd (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "ICD VARCHAR(35) NOT NULL," +
                "version INTEGER NOT NULL," +
                "comment TEXT NULL" +
                ");"),
        CREATE_TABLE_DEVICE("CREATE TABLE Device (" +
                "id INTEGER PRIMARY KEY NOT NULL," +
                "amountChannels INTEGER DEFAULT '8' NOT NULL," +
                "comment TEXT NULL" +
                ");"),
        CREATE_TABLE_EXAMINATION("CREATE TABLE Examination (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "patientRecord_id INTEGER NOT NULL," +
                "device_id INTEGER NOT NULL," +
                "comment TEXT NULL," +
                "FOREIGN KEY (patientRecord_id) REFERENCES PatientRecord (id)," +
                "FOREIGN KEY (device_id) REFERENCES Device (id)" +
                ");"),
        CREATE_TABLE_SAMPLES("CREATE TABLE Samples (" +
                "id INTEGER NOT NULL," +
                "Examination_id INTEGER NOT NULL," +
                "channel_id INTEGER NOT NULL," +
                "value INTEGER NOT NULL," +
                "PRIMARY KEY (id, Examination_id, channel_id)," +
                "FOREIGN KEY (Examination_id) REFERENCES Examination (id)" +
                ");");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
