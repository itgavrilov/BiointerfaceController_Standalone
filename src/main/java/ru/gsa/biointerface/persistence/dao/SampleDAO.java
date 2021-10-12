package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.Graph;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleDAO extends AbstractDAO<SampleEntity> {
    protected static SampleDAO dao;

    private SampleDAO() throws PersistenceException {
        super();
    }

    public static SampleDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new SampleDAO();

        return dao;
    }

    @Override
    public SampleEntity insert(SampleEntity entity) throws PersistenceException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, entity.getId());
            statement.setInt(2, entity.getGraph().getNumberOfChannel());
            statement.setInt(3, entity.getGraph().getExaminationEntity().getId());
            statement.setInt(4, entity.getValue());
            statement.execute();
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return entity;
    }

    @Override
    public SampleEntity getById(int key) {
        return null;
    }

    @Override
    public boolean update(SampleEntity entity) {
        return false;
    }

    @Override
    public boolean delete(SampleEntity entity) {
        return false;
    }

    @Override
    public Set<SampleEntity> getAll() throws PersistenceException {
        return null;
    }

    public Set<SampleEntity> getAllByGraph(Graph graph) throws PersistenceException {
        Set<SampleEntity> entities = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_GRAPH.QUERY)) {
            statement.setInt(1, graph.getEntity().getNumberOfChannel());
            statement.setInt(2, graph.getEntity().getExaminationEntity().getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SampleEntity entity = new SampleEntity(
                            resultSet.getInt("id"),
                            graph.getEntity(),
                            resultSet.getInt("value")
                    );
                    entities.add(entity);
                }
            } catch (SQLException e) {
                throw new PersistenceException("ResultSet error", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Statement error", e);
        }

        return entities;
    }

    public void beginTransaction() throws PersistenceException {
        try {
            if (db.getConnection().getAutoCommit()) {
                db.getConnection().setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new PersistenceException("ResetAutoCommit error", e);
        }
    }

    public void endTransaction() throws PersistenceException {
        try {
            if (!db.getConnection().getAutoCommit()) {
                db.getConnection().commit();
                db.getConnection().setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenceException("SetAutoCommit error", e);
        }
    }

    private enum SQL {
        INSERT("INSERT INTO Sample (id, numberOfChannel, examination_id,value) " +
                "VALUES ((?),(?),(?),(?));"),
        SELECT_BY_GRAPH("SELECT * FROM Sample WHERE numberOfChannel = (?) AND examination_id = (?);");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
