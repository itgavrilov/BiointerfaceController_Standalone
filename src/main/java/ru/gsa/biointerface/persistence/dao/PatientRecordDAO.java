package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

public class PatientRecordDAO extends AbstractDAO<PatientRecordEntity> {
    protected static PatientRecordDAO dao;

    private PatientRecordDAO() throws DAOException {
        super();
    }

    public static DAO<PatientRecordEntity> getInstance() throws DAOException {
        if (dao == null)
            dao = new PatientRecordDAO();

        return dao;
    }

    @Override
    public PatientRecordEntity insert(PatientRecordEntity patientRecordEntity) throws DAOException {
        if (patientRecordEntity == null)
            throw new NullPointerException("patientRecord is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, patientRecordEntity.getId());
            statement.setString(2, patientRecordEntity.getSecondName());
            statement.setString(3, patientRecordEntity.getFirstName());
            statement.setString(4, patientRecordEntity.getMiddleName());
            statement.setDate(5, Date.valueOf(patientRecordEntity.getBirthday()));
            if (patientRecordEntity.getIcdEntity() != null) statement.setInt(6, patientRecordEntity.getIcdEntity().getId());
            else statement.setNull(6, java.sql.Types.NULL);
            if (patientRecordEntity.getComment() != null) statement.setString(7, patientRecordEntity.getComment());
            else statement.setNull(7, java.sql.Types.NULL);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return patientRecordEntity;
    }

    @Override
    public PatientRecordEntity getById(int key) throws DAOException {
        PatientRecordEntity patientRecordEntity = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    IcdEntity icdEntity = null;
                    if (resultSet.getInt("Icd_id") > 0) {
                        icdEntity = new IcdEntity(resultSet.getInt("Icd_id"),
                                resultSet.getString("ICD"),
                                resultSet.getInt("version"),
                                resultSet.getString("icdComment")
                        );
                    }

                    patientRecordEntity = new PatientRecordEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("secondName"),
                            resultSet.getString("firstName"),
                            resultSet.getString("middleName"),
                            resultSet.getDate("birthday").toLocalDate(),
                            icdEntity,
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

        return patientRecordEntity;
    }

    @Override
    public boolean update(PatientRecordEntity patientRecordEntity) throws DAOException {
        if (patientRecordEntity == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (patientRecordEntity.getIcdEntity() != null)
                statement.setInt(1, patientRecordEntity.getIcdEntity().getId());
            else
                statement.setNull(1, Types.NULL);

            if (patientRecordEntity.getComment() != null)
                statement.setString(2, patientRecordEntity.getComment());
            else
                statement.setNull(2, Types.NULL);

            statement.setInt(3, patientRecordEntity.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(PatientRecordEntity patientRecordEntity) throws DAOException {
        if (patientRecordEntity == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, patientRecordEntity.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<PatientRecordEntity> getAll() throws DAOException {
        Set<PatientRecordEntity> patientRecordEntities = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                IcdEntity icdEntity = null;
                if (resultSet.getInt("Icd_id") > 0)
                    icdEntity = new IcdEntity(resultSet.getInt("Icd_id"),
                            resultSet.getString("ICD"),
                            resultSet.getInt("version"),
                            resultSet.getString("icdComment")
                    );

                PatientRecordEntity patientRecordEntity = new PatientRecordEntity(
                        resultSet.getInt("id"),
                        resultSet.getString("secondName"),
                        resultSet.getString("firstName"),
                        resultSet.getString("middleName"),
                        resultSet.getDate("birthday").toLocalDate(),
                        icdEntity,
                        resultSet.getString("comment")
                );
                patientRecordEntities.add(patientRecordEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement or resultSet error", e);
        }

        return patientRecordEntities;
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
