package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationDAO extends AbstractDAO<ExaminationEntity> {
    protected static ExaminationDAO dao;

    private ExaminationDAO() throws PersistenceException {
        super();
    }

    public static ExaminationDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new ExaminationDAO();

        return dao;
    }

    @Override
    public ExaminationEntity insert(ExaminationEntity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Examination is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDateTime()));
            statement.setInt(2, entity.getPatientRecord().getId());
            statement.setInt(3, entity.getDeviceEntity().getId());
            statement.setString(4, entity.getDeviceEntity().getComment());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getInt("id"));
                }
            } catch (SQLException e) {
                throw new PersistenceException("ResultSet error", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return entity;
    }

    @Override
    public ExaminationEntity getById(int key) throws PersistenceException {
        ExaminationEntity entity = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_ID.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity = new ExaminationEntity(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            null,
                            null,
                            resultSet.getString("comment")
                    );
                }
            } catch (SQLException e) {
                throw new PersistenceException("ResultSet error", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return entity;
    }


    public Set<ExaminationEntity> getByDevice(DeviceEntity device) throws PersistenceException {
        Set<ExaminationEntity> entities = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_DEVICE_ID.QUERY)) {
            statement.setInt(1, device.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ExaminationEntity entity = new ExaminationEntity(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            getPatientRecordFromResult(resultSet),
                            device,
                            resultSet.getString("comment")
                    );
                    entities.add(entity);
                }
            } catch (SQLException e) {
                throw new PersistenceException("ResultSet error", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return entities;
    }

    public Set<ExaminationEntity> getByPatientRecord(PatientRecordEntity patientRecord) throws PersistenceException {
        Set<ExaminationEntity> entities = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_PATIENT_RECORD_ID.QUERY)) {
            statement.setInt(1, patientRecord.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ExaminationEntity entity = new ExaminationEntity(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            patientRecord,
                            new DeviceEntity(
                                    resultSet.getInt("device_id"),
                                    resultSet.getInt("amountChannels"),
                                    resultSet.getString("deviceComment")
                            ),
                            resultSet.getString("comment")
                    );
                    entities.add(entity);
                }
            } catch (SQLException e) {
                throw new PersistenceException("ResultSet error", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return entities;
    }

    @Override
    public boolean update(ExaminationEntity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (entity.getComment() != null) statement.setString(1, entity.getComment());
            else statement.setNull(1, java.sql.Types.NULL);
            statement.setInt(2, entity.getId());
            result = statement.execute();
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(ExaminationEntity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, entity.getId());
            result = statement.execute();
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return result;
    }

    @Override
    public Set<ExaminationEntity> getAll() throws PersistenceException {
        Set<ExaminationEntity> entities = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                ExaminationEntity entity = new ExaminationEntity(
                        resultSet.getInt("id"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime(),
                        getPatientRecordFromResult(resultSet),
                        new DeviceEntity(
                                resultSet.getInt("device_id"),
                                resultSet.getInt("amountChannels"),
                                resultSet.getString("deviceComment")
                        ),
                        resultSet.getString("comment")
                );
                entities.add(entity);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Statement or resultSet error", e);
        }

        return entities;
    }

    private PatientRecordEntity getPatientRecordFromResult(ResultSet resultSet) throws SQLException {
        IcdEntity entity = null;
        if (resultSet.getInt("Icd_id") > 0)
            entity = new IcdEntity(resultSet.getInt("Icd_id"),
                    resultSet.getString("ICD"),
                    resultSet.getInt("version"),
                    resultSet.getString("icdComment")
            );
        return new PatientRecordEntity(
                resultSet.getInt("patientRecord_id"),
                resultSet.getString("secondName"),
                resultSet.getString("firstName"),
                resultSet.getString("middleName"),
                resultSet.getDate("birthday").toLocalDate(),
                entity,
                resultSet.getString("patientRecordComment")
        );
    }

    private enum SQL {
        INSERT("INSERT INTO Examination (timestamp, patientRecord_id, device_id, comment)" +
                "VALUES ((?), (?), (?), (?))" +
                "RETURNING id;"),
        SELECT_BY_ID("SELECT * FROM Examination WHERE id = (?);"),
        SELECT_BY_DEVICE_ID("SELECT e.*, " +
                "pr.secondName, pr.firstName, pr.middleName, pr.birthday, pr.icd_id, pr.comment AS patientRecordComment, " +
                "i.ICD, i.version, i.comment AS icdComment " +
                "FROM Examination AS e " +
                "LEFT JOIN PatientRecord pr ON pr.id = e.patientRecord_id " +
                "LEFT JOIN Icd i ON i.id = pr.icd_id " +
                "WHERE device_id = (?);"),

        SELECT_BY_PATIENT_RECORD_ID("SELECT e.*, d.amountChannels, d.comment AS  deviceComment " +
                "FROM Examination AS e " +
                "LEFT JOIN Device d ON d.id = e.device_id " +
                "WHERE e.patientRecord_id = (?)" +
                ";"),

        UPDATE("UPDATE Examination SET comment = (?) WHERE id = (?);"),

        DELETE("DELETE FROM Examination WHERE id = (?);"),

        SELECT_ALL("SELECT e.*, " +
                "pr.secondName, pr.firstName, pr.middleName, pr.birthday, pr.icd_id, pr.comment AS patientRecordComment, " +
                "i.ICD, i.version, i.comment AS icdComment, " +
                "d.amountChannels, d.comment AS deviceComment " +
                "FROM Examination AS e " +
                "LEFT JOIN Device d ON d.id = e.device_id " +
                "LEFT JOIN PatientRecord pr ON pr.id = e.patientRecord_id " +
                "LEFT JOIN Icd i ON i.id = pr.icd_id" +
                ";");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
