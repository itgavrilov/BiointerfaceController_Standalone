package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class GraphDAO extends AbstractDAO<GraphEntity> {
    protected static GraphDAO dao;

    private GraphDAO() throws DAOException {
        super();
    }

    public static GraphDAO getInstance() throws DAOException {
        if (dao == null)
            dao = new GraphDAO();

        return dao;
    }

    @Override
    public GraphEntity insert(GraphEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, entity.getNumberOfChannel());
            statement.setInt(2, entity.getExaminationEntity().getId());

            ChannelEntity channelEntity = entity.getChannelEntity();

            if (channelEntity != null)
                statement.setInt(3, channelEntity.getId());
            else
                statement.setNull(3, java.sql.Types.NULL);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entity;
    }

    @Override
    public GraphEntity getById(int key) {
        return null;
    }

    @Override
    public boolean update(GraphEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            statement.setInt(1, entity.getNumberOfChannel());
            statement.setInt(2, entity.getChannelEntity().getId());

            ChannelEntity channelEntity = entity.getChannelEntity();

            if (channelEntity != null)
                statement.setInt(3, channelEntity.getId());
            else
                statement.setNull(3, java.sql.Types.NULL);

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(GraphEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, entity.getNumberOfChannel());
            statement.setInt(2, entity.getExaminationEntity().getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<GraphEntity> getAll() throws DAOException {
        return null;
    }

    public Set<GraphEntity> getAllByExamination(Examination examination) throws DAOException {
        Set<GraphEntity> entities = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_EXAMINATION.QUERY)) {
            statement.setInt(1, examination.getEntity().getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ChannelEntity channelEntity = null;
                    if (resultSet.getInt("channel_id") > 0) {
                        channelEntity = new ChannelEntity(
                                resultSet.getInt("channel_id"),
                                resultSet.getString("name"),
                                null
                        );
                    }

                    GraphEntity entity = new GraphEntity(
                            resultSet.getInt("numberOfChannel"),
                            examination.getEntity(),
                            channelEntity
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

    private enum SQL {
        INSERT("INSERT INTO Graph (numberOfChannel, examination_id, channel_id)" +
                "VALUES ((?),(?),(?));"),
        UPDATE("UPDATE Graph SET channel_id = (?) WHERE numberOfChannel = (?) AND examination_id = (?);"),
        DELETE("DELETE FROM Graph WHERE numberOfChannel = (?) AND examination_id = (?);"),
        SELECT_BY_EXAMINATION("""
                SELECT g.*, c.name FROM Graph AS g
                LEFT JOIN Channel c ON c.id = g.channel_id
                WHERE examination_id = (?);
                """);

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
