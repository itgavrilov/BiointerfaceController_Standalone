package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

public class PatientRecordDAO extends AbstractDAO<PatientRecord> {
    protected static PatientRecordDAO dao;

    private PatientRecordDAO() throws DAOException {
        super();
    }

    public static DAO<PatientRecord> getInstance() throws DAOException {
        if (dao == null)
            dao = new PatientRecordDAO();

        return dao;
    }

    @Override
    public PatientRecord insert(PatientRecord patientRecord) throws DAOException {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, patientRecord.getId());
            statement.setString(2, patientRecord.getSecondName());
            statement.setString(3, patientRecord.getFirstName());
            statement.setString(4, patientRecord.getMiddleName());
            statement.setDate(5, Date.valueOf(patientRecord.getBirthday()));
            if (patientRecord.getIcd() != null) statement.setInt(6, patientRecord.getIcd().getId());
            else statement.setNull(6, java.sql.Types.NULL);
            if (patientRecord.getComment() != null) statement.setString(7, patientRecord.getComment());
            else statement.setNull(7, java.sql.Types.NULL);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return patientRecord;
    }

    @Override
    public PatientRecord getById(int key) throws DAOException {
        PatientRecord patientRecord = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Icd icd = null;
                    if (resultSet.getInt("Icd_id") > 0) {
                        icd = new Icd(resultSet.getInt("Icd_id"),
                                resultSet.getString("ICD"),
                                resultSet.getInt("version"),
                                resultSet.getString("icdComment")
                        );
                    }

                    patientRecord = new PatientRecord(
                            resultSet.getInt("id"),
                            resultSet.getString("secondName"),
                            resultSet.getString("firstName"),
                            resultSet.getString("middleName"),
                            resultSet.getDate("birthday").toLocalDate(),
                            icd,
                            resultSet.getString("comment")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return patientRecord;
    }

    @Override
    public boolean update(PatientRecord patientRecord) throws DAOException {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (patientRecord.getIcd() != null)
                statement.setInt(1, patientRecord.getIcd().getId());
            else
                statement.setNull(1, Types.NULL);

            if (patientRecord.getComment() != null)
                statement.setString(2, patientRecord.getComment());
            else
                statement.setNull(2, Types.NULL);

            statement.setInt(3, patientRecord.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(PatientRecord patientRecord) throws DAOException {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, patientRecord.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<PatientRecord> getAll() throws DAOException {
        Set<PatientRecord> patientRecords = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                Icd icd = null;
                if (resultSet.getInt("Icd_id") > 0)
                    icd = new Icd(resultSet.getInt("Icd_id"),
                            resultSet.getString("ICD"),
                            resultSet.getInt("version"),
                            resultSet.getString("icdComment")
                    );

                PatientRecord patientRecord = new PatientRecord(
                        resultSet.getInt("id"),
                        resultSet.getString("secondName"),
                        resultSet.getString("firstName"),
                        resultSet.getString("middleName"),
                        resultSet.getDate("birthday").toLocalDate(),
                        icd,
                        resultSet.getString("comment")
                );
                patientRecords.add(patientRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement or resultSet error", e);
        }

        return patientRecords;
    }

    private enum SQL {
        INSERT("INSERT INTO PatientRecord (id,secondName,firstName,middleName,birthday,icd_id,comment)" +
                "VALUES ((?), (?), (?), (?), (?), (?), (?));"),

        SELECT("SELECT pr.*, i.ICD, i.version, i.comment AS icdComment " +
                "FROM PatientRecord AS pr " +
                "LEFT JOIN Icd i ON i.id = pr.icd_id " +
                "WHERE pr.id = (?)" +
                ";"),

        UPDATE("UPDATE PatientRecord SET icd_id = (?), comment = (?) WHERE id = (?);"),

        DELETE("DELETE FROM PatientRecord WHERE id = (?);"),

        SELECT_ALL("SELECT pr.*, i.ICD, i.version, i.comment AS icdComment " +
                "FROM PatientRecord AS pr " +
                "LEFT JOIN Icd i ON i.id = pr.icd_id" +
                ";");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
