package ru.gsa.biointerface.persistence.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.Graph;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.util.EntityUtil;

class ExaminationDAOTest {
    private final ExaminationDAO examinationDAO;

    public ExaminationDAOTest() throws PersistenceException {
        this.examinationDAO = ExaminationDAO.getInstance();
    }

    @BeforeEach
    private void beginTransaction() throws PersistenceException {
        examinationDAO.beginTransaction();
    }

    @AfterEach
    private void endTransaction() throws PersistenceException {
        examinationDAO.endTransaction();
    }


    @Test
    void insert() throws PersistenceException {
        ExaminationEntity examination = EntityUtil.getExamination().getEntity();

        examinationDAO.insert(examination);

        Assertions.assertFalse(examination.getGraphEntities().isEmpty());

        GraphEntity graphEntity = examination.getGraphEntities().get(0);
        var graph = new Graph(graphEntity);

        for (int i = 0; i < 100; i++) {
            graph.setNewSamples(EntityUtil.getSampleList());
        }
    }

    @Test
    void insert_ExaminationEntity_Null() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> examinationDAO.insert(null),
                "Entity is null");
    }
}