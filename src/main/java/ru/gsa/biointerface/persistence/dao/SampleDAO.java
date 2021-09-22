package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class SampleDAO extends AbstractDAO<SampleEntity> {
    protected static SampleDAO dao;

    private SampleDAO() throws DAOException {
        super();
    }

    public static SampleDAO getInstance() throws DAOException {
        if (dao == null)
            dao = new SampleDAO();

        return dao;
    }

    @Override
    public SampleEntity insert(SampleEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("patientRecord is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setInt(1, entity.getId());
            statement.setInt(2, entity.getExamination_id());
            statement.setInt(3, entity.getChannel_id());
            statement.setInt(4, entity.getValue());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
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
    public Set<SampleEntity> getAll() throws DAOException {
        return null;
    }


    public Set<SampleEntity> getAllByExamination(Examination examination) throws DAOException {
        Set<SampleEntity> entities = new TreeSet<>();

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT_BY_EXAMINATION.QUERY)){
            statement.setInt(1, examination.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SampleEntity entity = new SampleEntity(
                            resultSet.getInt("id"),
                            resultSet.getInt("examination_id"),
                            resultSet.getInt("channel_id"),
                            resultSet.getInt("value")
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

    public void beginTransaction() throws DAOException {
        try {
            if (db.getConnection().getAutoCommit()) {
                db.getConnection().setAutoCommit(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DAOException("setAutoCommit error", e);
        }
    }

    public void endTransaction() throws DAOException {
        try {
            if (!db.getConnection().getAutoCommit()){
                db.getConnection().commit();
                db.getConnection().setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DAOException("setAutoCommit error", e);
        }
    }

    private enum SQL {
        INSERT("INSERT INTO Samples (id,examination_id,channel_id,value)" +
                "VALUES ((?),(?),(?),(?))"),
        SELECT_BY_EXAMINATION("SELECT * FROM Samples WHERE examination_id = (?);");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
