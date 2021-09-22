package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

public class IcdDAO extends AbstractDAO<IcdEntity> {
    protected static IcdDAO dao;

    private IcdDAO() throws DAOException {
        super();
    }

    public static DAO<IcdEntity> getInstance() throws DAOException {
        if (dao == null)
            dao = new IcdDAO();

        return dao;
    }

    @Override
    public IcdEntity insert(IcdEntity icdEntity) throws DAOException {
        if (icdEntity == null)
            throw new NullPointerException("patientRecord is null");

        IcdEntity result = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setString(1, icdEntity.getICD());
            statement.setInt(2, icdEntity.getVersion());
            if (icdEntity.getComment() != null) statement.setString(3, icdEntity.getComment());
            else statement.setNull(3, java.sql.Types.NULL);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = new IcdEntity(
                            resultSet.getInt("id"),
                            icdEntity.getICD(),
                            icdEntity.getVersion(),
                            icdEntity.getComment()
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
    public IcdEntity getById(int key) throws DAOException {
        IcdEntity icdEntity = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    icdEntity = new IcdEntity(
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

        return icdEntity;
    }

    @Override
    public boolean update(IcdEntity icdEntity) throws DAOException {
        if (icdEntity == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (icdEntity.getComment() != null) statement.setString(1, icdEntity.getComment());
            else statement.setNull(1, java.sql.Types.NULL);
            statement.setInt(2, icdEntity.getId());
            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(IcdEntity icdEntity) throws DAOException {
        if (icdEntity == null)
            throw new NullPointerException("patientRecord is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, icdEntity.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<IcdEntity> getAll() throws DAOException {
        Set<IcdEntity> icdEntities = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                IcdEntity icdEntity = new IcdEntity(
                        resultSet.getInt("id"),
                        resultSet.getString("ICD"),
                        resultSet.getInt("version"),
                        resultSet.getString("comment")
                );
                icdEntities.add(icdEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return icdEntities;
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
