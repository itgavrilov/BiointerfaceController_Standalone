package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;
import java.util.TreeSet;

public class ChannelDAO extends AbstractDAO<ChannelEntity> {
    protected static ChannelDAO dao;

    private ChannelDAO() throws DAOException {
        super();
    }

    public static ChannelDAO getInstance() throws DAOException {
        if (dao == null)
            dao = new ChannelDAO();

        return dao;
    }

    @Override
    public ChannelEntity insert(ChannelEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, entity.getId());
            statement.setInt(2, entity.getExaminationEntity().getId());
            statement.setString(3, entity.getName());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entity;
    }

    @Override
    public ChannelEntity getById(int id) {
        return null;
    }

    public Set<ChannelEntity> getByExamination(ExaminationEntity examination) throws DAOException {
        Set<ChannelEntity> entities = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_EXAMINATION_ID.QUERY)) {
            statement.setInt(1, examination.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ChannelEntity entity = new ChannelEntity(
                            resultSet.getInt("id"),
                            examination,
                            resultSet.getString("name")
                    );
                    entities.add(entity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entities;
    }

    @Override
    public boolean update(ChannelEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            if (entity.getName() != null) statement.setString(1, entity.getName());
            else statement.setNull(1, Types.NULL);
            statement.setInt(2, entity.getId());
            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(ChannelEntity entity) {
        return false;
    }

    @Override
    public Set<ChannelEntity> getAll() {
        return null;
    }


    private enum SQL {
        INSERT("INSERT INTO Channel (id, examination_id, name)" +
                "VALUES ((?), (?), (?));"),
        SELECT_BY_EXAMINATION_ID("SELECT * " +
                "FROM Channel" +
                "WHERE examination_id = (?);"),

        UPDATE("UPDATE Channel SET name = (?) WHERE id = (?)");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
