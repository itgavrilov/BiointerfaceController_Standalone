package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

public class DeviceDAO extends AbstractDAO<DeviceEntity> {
    protected static DeviceDAO dao;

    private DeviceDAO() throws DAOException {
        super();
    }

    public static DAO<DeviceEntity> getInstance() throws DAOException {
        if (dao == null)
            dao = new DeviceDAO();

        return dao;
    }

    @Override
    public DeviceEntity insert(DeviceEntity device) throws DAOException {
        if (device == null)
            throw new NullPointerException("device is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, device.getId());
            statement.setInt(2, device.getAmountChannels());
            if (device.getComment() != null) statement.setString(3, device.getComment());
            else statement.setNull(3, java.sql.Types.NULL);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return device;
    }

    @Override
    public DeviceEntity getById(int key) throws DAOException {
        DeviceEntity device = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    device = new DeviceEntity(
                            resultSet.getInt("id"),
                            resultSet.getInt("amountChannels"),
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

        return device;
    }

    @Override
    public boolean update(DeviceEntity device) throws DAOException {
        if (device == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (device.getComment() != null) statement.setString(1, device.getComment());
            else statement.setNull(1, java.sql.Types.NULL);
            statement.setInt(2, device.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(DeviceEntity device) throws DAOException {
        if (device == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, device.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<DeviceEntity> getAll() throws DAOException {
        Set<DeviceEntity> devices = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                DeviceEntity device = new DeviceEntity(
                        resultSet.getInt("id"),
                        resultSet.getInt("amountChannels"),
                        resultSet.getString("comment")
                );
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return devices;
    }

    private enum SQL {
        INSERT("INSERT INTO Device (id, amountChannels, comment)" +
                "VALUES ((?),(?),(?));"),
        SELECT("SELECT * FROM Device WHERE id = (?);"),
        UPDATE("UPDATE Device SET comment = (?) WHERE id = (?)"),
        DELETE("DELETE FROM Device WHERE id = (?)"),
        SELECT_ALL("SELECT * FROM Device;");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
