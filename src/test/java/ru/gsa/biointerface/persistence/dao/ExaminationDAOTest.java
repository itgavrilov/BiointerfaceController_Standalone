package ru.gsa.biointerface.persistence.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.*;
import ru.gsa.biointerface.domain.host.dataCash.Cash;
import ru.gsa.biointerface.domain.host.dataCash.SampleCash;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

class ExaminationDAOTest {
    private final ExaminationDAO examinationDAO;

    public ExaminationDAOTest() throws PersistenceException {
        this.examinationDAO = ExaminationDAO.getInstance();
    }

    @BeforeEach
    private void beginTransaction() {

    }

    @AfterEach
    private void endTransaction() {

    }


    @Test
    void insert() throws DomainException, InterruptedException {
        List<Cash> cashList = new ArrayList<>();
        List<Graph> graphList = new ArrayList<>();
        var patientRecord = new PatientRecord(1,
                "G",
                "S",
                "A",
                LocalDate.now(),
                null,
                "test");
        var device = new Device(1, 1, "test");

        for (int i = 0; i < device.getAmountChannels(); i++) {
            Cash cash = new SampleCash();
            Graph graph = new Graph(i);

            cash.addListener(graph);

            cashList.add(cash);
            graphList.add(graph);
        }

        var examination = new Examination(patientRecord, device, graphList, "test");

        Assertions.assertFalse(examination.getEntity().getGraphEntities().isEmpty());

        examination.recordingStart();

        for (int i = 1000; i > 0; i--) {
            sleep(1);
            for (Cash cash : cashList) {
                cash.add(i);
            }
        }

        examination.recordingStop();
    }

    @Test
    void insert_ExaminationEntity_Null() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> examinationDAO.insert(null),
                "Entity is null");
    }
}