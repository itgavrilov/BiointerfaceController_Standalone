package ru.gsa.biointerfaceController_standalone.daoLayer.dao;

import ru.gsa.biointerfaceController_standalone.businessLayer.Icd;
import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

public class IcdDAO extends AbstractDAO<Icd> {
    protected static IcdDAO dao;

    private IcdDAO() throws DAOException {
        super();
    }

    public static DAO<Icd> getInstance() throws DAOException {
        if (dao == null)
            dao = new IcdDAO();

        return dao;
    }

    @Override
    public boolean insert(Icd icd) throws DAOException {
        if (icd == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.INSERT.QUERY)) {
                statement.setString(1, icd.getICD());
                statement.setInt(2, icd.getVersion());
                if (icd.getComment() != null) statement.setString(3, icd.getComment());
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
    public Icd getById(int key) throws DAOException {
        Icd icd = null;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.SELECT.QUERY)) {
                statement.setInt(1, key);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        icd = new Icd(
                                resultSet.getInt("id"),
                                resultSet.getString("ICD"),
                                resultSet.getInt("version"),
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

        return icd;
    }

    @Override
    public boolean update(Icd icd) throws DAOException {
        if (icd == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.UPDATE.QUERY)) {
                if (icd.getComment() != null) statement.setString(1, icd.getComment());
                else statement.setNull(1, java.sql.Types.NULL);
                statement.setInt(2, icd.getId());
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
    public boolean delete(Icd icd) throws DAOException {
        if (icd == null)
            throw new NullPointerException("patientRecord is null");

        boolean result = false;

        try (Connection connection = db.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL.DELETE.QUERY)) {
                statement.setInt(1, icd.getId());

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
    public Set<Icd> getAll() throws DAOException {
        Set<Icd> icds = new TreeSet<>();

        try (Connection connection = db.getConnection()) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
                while (resultSet.next()) {
                    Icd icd = new Icd(
                            resultSet.getInt("id"),
                            resultSet.getString("ICD"),
                            resultSet.getInt("version"),
                            resultSet.getString("comment")
                    );
                    icds.add(icd);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("statement error", e);
            }
        } catch (DAOException | SQLException e) {
            e.printStackTrace();
            throw new DAOException("database connection error", e);
        }

        return icds;
    }

    private enum SQL {
        INSERT("INSERT INTO Icd (ICD,version,comment)" +
                "VALUES ((?), (?), (?));"),
        SELECT("SELECT * FROM Icd WHERE id = (?);"),
        UPDATE("UPDATE Icd SET comment = (?) WHERE id = (?)"),
        DELETE("DELETE FROM Icd WHERE id = (?)"),
        SELECT_ALL("SELECT * FROM Icd;");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
