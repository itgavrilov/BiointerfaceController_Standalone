package ru.gsa.biointerfaceController_standalone.daoLayer.dao;

import ru.gsa.biointerfaceController_standalone.businessLayer.Device;
import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

public class DeviceDAO extends AbstractDAO<Device> {
    protected static DeviceDAO dao;

    private DeviceDAO() throws DAOException {
        super();
    }

    public static DAO<Device> getInstance() throws DAOException {
        if (dao == null)
            dao = new DeviceDAO();

        return dao;
    }

    @Override
    public boolean insert(Device device) throws DAOException {
        if (device == null)
            throw new NullPointerException("device is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.INSERT.QUERY)) {
                statement.setInt(1, device.getId());
                statement.setInt(2, device.getCountOfChannels());
                if (device.getComment() != null) statement.setString(3, device.getComment());
                else statement.setNull(3, java.sql.Types.NULL);

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
    public Device getById(int key) throws DAOException {
        Device device = null;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.SELECT.QUERY)) {
                statement.setInt(1, key);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        device = new Device(
                                resultSet.getInt("id"),
                                resultSet.getInt("countOfChannels"),
                                resultSet.getString("comment")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return device;
    }

    @Override
    public boolean update(Device device) throws DAOException {
        if (device == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.UPDATE.QUERY)) {
                if (device.getComment() != null) statement.setString(1, device.getComment());
                else statement.setNull(1, java.sql.Types.NULL);
                statement.setInt(2, device.getId());

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
    public boolean delete(Device device) throws DAOException {
        if (device == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.DELETE.QUERY)) {
                statement.setInt(1, device.getId());

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
    public Set<Device> getAll() throws DAOException {
        Set<Device> devices = new TreeSet<>();

        try (Connection connection = db.getConnection()) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
                while (resultSet.next()) {
                    Device device = new Device(
                            resultSet.getInt("id"),
                            resultSet.getInt("countOfChannels"),
                            resultSet.getString("comment")
                    );
                    devices.add(device);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return devices;
    }

    private enum SQL {
        INSERT("INSERT INTO Device (id, countOfChannels, comment)" +
                "VALUES ((?),(?),(?));"),
        SELECT("SELECT * FROM Device WHERE id = (?);"),
        UPDATE("UPDATE PatientRecord SET comment = (?) WHERE id = (?)"),
        DELETE("DELETE FROM Device WHERE id = (?)"),
        SELECT_ALL("SELECT * FROM Device;");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
