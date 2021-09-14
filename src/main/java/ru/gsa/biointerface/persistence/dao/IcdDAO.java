package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public Icd insert(Icd icd) throws DAOException {
        if (icd == null)
            throw new NullPointerException("patientRecord is null");

        Icd result = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setString(1, icd.getICD());
            statement.setInt(2, icd.getVersion());
            if (icd.getComment() != null) statement.setString(3, icd.getComment());
            else statement.setNull(3, java.sql.Types.NULL);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = new Icd(
                            resultSet.getInt("id"),
                            icd.getICD(),
                            icd.getVersion(),
                            icd.getComment()
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
    public Icd getById(int key) throws DAOException {
        Icd icd = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
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
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return icd;
    }

    @Override
    public boolean update(Icd icd) throws DAOException {
        if (icd == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (icd.getComment() != null) statement.setString(1, icd.getComment());
            else statement.setNull(1, java.sql.Types.NULL);
            statement.setInt(2, icd.getId());
            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(Icd icd) throws DAOException {
        if (icd == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, icd.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<Icd> getAll() throws DAOException {
        Set<Icd> icds = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
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

        return icds;
    }

    private enum SQL {
        INSERT("INSERT INTO Icd (ICD,version,comment)" +
                "VALUES ((?), (?), (?))" +
                "RETURNING id;"),
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
