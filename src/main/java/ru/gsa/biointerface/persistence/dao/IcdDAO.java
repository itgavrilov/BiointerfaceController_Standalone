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
    public IcdEntity insert(IcdEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");


        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setString(1, entity.getICD());
            statement.setInt(2, entity.getVersion());
            if (entity.getComment() != null) statement.setString(3, entity.getComment());
            else statement.setNull(3, java.sql.Types.NULL);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getInt("id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entity;
    }

    @Override
    public IcdEntity getById(int key) throws DAOException {
        IcdEntity entity = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
            statement.setInt(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity = new IcdEntity(
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

        return entity;
    }

    @Override
    public boolean update(IcdEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (entity.getComment() != null) statement.setString(1, entity.getComment());
            else statement.setNull(1, java.sql.Types.NULL);
            statement.setInt(2, entity.getId());
            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(IcdEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, entity.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<IcdEntity> getAll() throws DAOException {
        Set<IcdEntity> entities = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                IcdEntity entity = new IcdEntity(
                        resultSet.getInt("id"),
                        resultSet.getString("ICD"),
                        resultSet.getInt("version"),
                        resultSet.getString("comment")
                );
                entities.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entities;
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
