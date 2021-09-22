package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

public class ExaminationDAO extends AbstractDAO<ExaminationEntity> {
    protected static ExaminationDAO dao;

    private ExaminationDAO() throws DAOException {
        super();
    }

    public static ExaminationDAO getInstance() throws DAOException {
        if (dao == null)
            dao = new ExaminationDAO();

        return dao;
    }

    @Override
    public ExaminationEntity insert(ExaminationEntity examination) throws DAOException {
        if (examination == null)
            throw new NullPointerException("examination is null");

        ExaminationEntity result = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setTimestamp(1, Timestamp.valueOf(examination.getDateTime()));
            statement.setInt(2, examination.getPatientRecord().getId());
            statement.setInt(3, examination.getDevice().getId());
            statement.setString(4, examination.getDevice().getComment());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = new ExaminationEntity(
                            resultSet.getInt("id"),
                            examination.getDateTime(),
                            examination.getPatientRecord(),
                            examination.getDevice(),
                            examination.getComment()
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

        return result;
    }

    @Override
    public ExaminationEntity getById(int key) throws DAOException {
        ExaminationEntity examination = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_ID.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    examination = new ExaminationEntity(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            null,
                            null,
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

        return examination;
    }


    public Set<ExaminationEntity> getByDevice(DeviceEntity device) throws DAOException {
        Set<ExaminationEntity> examinations = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_DEVICE_ID.QUERY)) {
            statement.setInt(1, device.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ExaminationEntity examination = new ExaminationEntity(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            getPatientRecordFromResult(resultSet),
                            device,
                            resultSet.getString("comment")
                    );
                    examinations.add(examination);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return examinations;
    }

    public Set<ExaminationEntity> getByPatientRecord(PatientRecordEntity patientRecordEntity) throws DAOException {
        Set<ExaminationEntity> examinations = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_PATIENT_RECORD_ID.QUERY)) {
            statement.setInt(1, patientRecordEntity.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ExaminationEntity examination = new ExaminationEntity(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            patientRecordEntity,
                            new DeviceEntity(
                                    resultSet.getInt("device_id"),
                                    resultSet.getInt("amountChannels"),
                                    resultSet.getString("deviceComment")
                            ),
                            resultSet.getString("comment")
                    );
                    examinations.add(examination);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return examinations;
    }

    @Override
    public boolean update(ExaminationEntity examination) throws DAOException {
        if (examination == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (examination.getComment() != null) statement.setString(1, examination.getComment());
            else statement.setNull(1, java.sql.Types.NULL);
            statement.setInt(2, examination.getId());
            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(ExaminationEntity examination) throws DAOException {
        if (examination == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, examination.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<ExaminationEntity> getAll() throws DAOException {
        Set<ExaminationEntity> examinations = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                ExaminationEntity examination = new ExaminationEntity(
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
                examinations.add(examination);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return examinations;
    }

    private PatientRecordEntity getPatientRecordFromResult(ResultSet resultSet) throws SQLException {
        IcdEntity icdEntity = null;
        if (resultSet.getInt("Icd_id") > 0)
            icdEntity = new IcdEntity(resultSet.getInt("Icd_id"),
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
                icdEntity,
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

        UPDATE("UPDATE Examination SET comment = (?) WHERE id = (?)"),

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
