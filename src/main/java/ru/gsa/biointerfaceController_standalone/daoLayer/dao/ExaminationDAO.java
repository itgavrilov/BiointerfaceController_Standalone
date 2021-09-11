package ru.gsa.biointerfaceController_standalone.daoLayer.dao;

import ru.gsa.biointerfaceController_standalone.businessLayer.Examination;
import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

public class ExaminationDAO extends AbstractDAO<Examination> {
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
    public boolean insert(Examination examination) throws DAOException {
        if (examination == null)
            throw new NullPointerException("examination is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.INSERT.QUERY)) {
                statement.setTimestamp(1, Timestamp.valueOf(examination.getDateTime()));
                statement.setInt(2, examination.getPatientRecord().getId());
                statement.setInt(3, examination.getDevice().getId());
                statement.setString(4, examination.getDevice().getComment());

                result = statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return result;
    }

    @Override
    public Examination getById(int key) throws DAOException {
        Examination examination = null;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.SELECT_BY_ID.QUERY)) {
                statement.setInt(1, key);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        examination = new Examination(
                                resultSet.getInt("id"),
                                resultSet.getTimestamp("timestamp").toLocalDateTime(),
                                PatientRecordDAO.getInstance().getById(resultSet.getInt("patientRecord_id")),
                                DeviceDAO.getInstance().getById(resultSet.getInt("device_id")),
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
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return examination;
    }

    public Set<Examination> getByPatientRecordId(int key) throws DAOException {
        Set<Examination> examinations = new TreeSet<>();

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.SELECT_BY_PATIENT_RECORD_ID.QUERY)) {
                statement.setInt(1, key);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Examination examination = new Examination(
                                resultSet.getInt("id"),
                                resultSet.getTimestamp("timestamp").toLocalDateTime(),
                                PatientRecordDAO.getInstance().getById(resultSet.getInt("patientRecord_id")),
                                DeviceDAO.getInstance().getById(resultSet.getInt("device_id")),
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
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return examinations;
    }

    @Override
    public boolean update(Examination examination) throws DAOException {
        if (examination == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.UPDATE.QUERY)) {
                if (examination.getComment() != null) statement.setString(1, examination.getComment());
                else statement.setNull(1, java.sql.Types.NULL);
                statement.setInt(2, examination.getId());
                result = statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return result;
    }

    @Override
    public boolean delete(Examination examination) throws DAOException {
        if (examination == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.DELETE.QUERY)) {
                statement.setInt(1, examination.getId());

                result = statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return result;
    }

    @Override
    public Set<Examination> getAll() throws DAOException {
        Set<Examination> examinations = new TreeSet<>();

        try (Connection connection = db.getConnection()) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
                while (resultSet.next()) {
                    Examination examination = new Examination(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            PatientRecordDAO.getInstance().getById(resultSet.getInt("patientRecord_id")),
                            DeviceDAO.getInstance().getById(resultSet.getInt("device_id")),
                            resultSet.getString("comment")
                    );
                    examinations.add(examination);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return examinations;
    }

    private enum SQL {
        INSERT("INSERT INTO Examination (timestamp, patientRecord_id, device_id, comment)" +
                "VALUES ((?), (?), (?), (?));"),
        SELECT_BY_ID("SELECT * FROM Examination WHERE id = (?);"),
        SELECT_BY_PATIENT_RECORD_ID("SELECT * FROM Examination WHERE patientRecord_id = (?);"),
        UPDATE("UPDATE Examination SET comment = (?) WHERE id = (?)"),
        DELETE("DELETE FROM Examination WHERE id = (?);"),
        SELECT_ALL("SELECT * FROM Examination;");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
