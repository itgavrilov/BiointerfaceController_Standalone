package ru.gsa.biointerface.persistence.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.*;
import ru.gsa.biointerface.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    void insert() throws DomainException {
        var patientRecord = new PatientRecord(1,
                "G",
                "S",
                "A",
                LocalDate.now(),
                null,
                "test");

        var device = new Device(1, 1);
        device.setComment("test");

        List<Channel> channels = new ArrayList<>();
        for (int i = 0; i < device.getAmountChannels(); i++) {
            channels.add(null);
        }

        var examination = new Examination(patientRecord, device, channels, "test");

        Assertions.assertEquals(examination.getEntity().getDeviceEntity(), device.getEntity());

        Assertions.assertEquals(examination.getEntity().getPatientRecordEntity(), patientRecord.getEntity());

        Assertions.assertFalse(examination.getEntity().getGraphEntities().isEmpty());

        examination.recordingStart();

        for (int i = 1000; i > 0; i--) {
            examination.setNewSamplesInGraph(0, i);
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