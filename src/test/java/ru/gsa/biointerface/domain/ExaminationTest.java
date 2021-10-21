package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ExaminationDAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class ExaminationTest {
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final String comment = "commentTest";

    private PatientRecord patientRecord;
    private Device device;

    private Examination examination;
    private ExaminationEntity entity;
    private final ExaminationDAO dao = ExaminationDAO.getInstance();

    ExaminationTest() throws PersistenceException {
    }

    @BeforeEach
    void setUp() {
        try {
            patientRecord = new PatientRecord(
                    1,
                    "G",
                    "S",
                    "A",
                    dateTime.toLocalDate(),
                    new Icd(null),
                    comment
            );
            device = new Device(1, 1);

            List<Channel> channels = new ArrayList<>();
            for (int i = 0; i < device.getAmountChannels(); i++) {
                channels.add(new Channel(null));
            }

            examination = new Examination(
                    patientRecord,
                    device,
                    channels,
                    comment
            );
            dao.transactionStart();
            dao.insert(examination.getEntity());
            dao.transactionStop();

            entity = new ExaminationEntity(
                    examination.getId(),
                    examination.getEntity().getDateTime(),
                    patientRecord.getEntity(),
                    device.getEntity(),
                    comment,
                    examination.getEntity().getGraphEntities()
            );

        } catch (DomainException | PersistenceException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (examination != null)
                examination.delete();

            patientRecord.delete();
            device.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error tearDown");
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Examination.getAll().contains(examination));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getAll");
        }
    }

    @Test
    void getByPatientRecord() {
        try {
        Assertions.assertTrue(Examination.getByPatientRecord(patientRecord).contains(examination));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getByPatientRecord");
        }
    }

    @Test
    void delete() {
        try {
            examination.delete();
            Assertions.assertFalse(Examination.getAll().contains(examination));
            examination = null;
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error delete");
        }
    }

    @Test
    void getEntity() {
        Assertions.assertEquals(entity, examination.getEntity());
    }

    @Test
    void getId() {
        Assertions.assertEquals(entity.getId(), examination.getId());
    }

    @Test
    void getDateTime() {
        Assertions.assertEquals(entity.getDateTime(), Timestamp.valueOf(examination.getDateTime()));
    }

    @Test
    void getPatientRecord() {
        Assertions.assertEquals(patientRecord, examination.getPatientRecord());
    }

    @Test
    void getDevice() {
        Assertions.assertEquals(device, examination.getDevice());
    }

    @Test
    void getComment() {
        Assertions.assertEquals(comment, examination.getComment());
    }

    @Test
    void setComment() {
        String testComment = "test_setComment";
        try {
            examination.setComment(testComment);
            Assertions.assertEquals(testComment, examination.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setComment");
        }
    }

    @Test
    void recording() {
        try {
            examination.delete();
            Assertions.assertFalse(examination.isRecording());
            examination.recordingStart();
            Assertions.assertTrue(examination.isRecording());
            examination.recordingStop();
            Assertions.assertFalse(examination.isRecording());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error recordingStart");
        }
    }

    @Test
    void setChannelInGraph() {
        Channel channel = new Channel(new ChannelEntity("nameTest", comment));

        Assertions.assertNotEquals(
                channel.getEntity(),
                examination.getEntity().getGraphEntities().get(0).getChannelEntity()
        );
        examination.setChannelInGraph(0,channel);
        Assertions.assertEquals(
                channel.getEntity(),
                examination.getEntity().getGraphEntities().get(0).getChannelEntity()
        );
    }

    @Test
    void setNewSamplesInGraph() {
        int value = 10;
        int numberOfChannel = 0;
        GraphEntity graphEntity = examination.getEntity().getGraphEntities().get(numberOfChannel);
        int index = graphEntity.getSampleEntities().size();

        SampleEntity sampleEntity = new SampleEntity(
                index,
                graphEntity,
                value
                );

        Assertions.assertEquals(0, graphEntity.getSampleEntities().size());
        try {
            examination.setNewSamplesInGraph(numberOfChannel,value);
            Assertions.assertEquals(1, graphEntity.getSampleEntities().size());
            Assertions.assertEquals(sampleEntity, graphEntity.getSampleEntities().get(index));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setNewSamplesInGraph");
        }
    }

    @Test
    void testEquals() {
        Examination test = new Examination(entity);

        Assertions.assertEquals(test, examination);

        entity.setId(-1);
        test = new Examination(entity);
        Assertions.assertNotEquals(test, examination);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(entity.hashCode(), examination.hashCode());

        entity.setId(-1);
        Assertions.assertNotEquals(entity.hashCode(), examination.hashCode());
    }

    @Test
    void compareTo() {
        Examination test = new Examination(entity);

        Assertions.assertEquals(
                examination.getEntity().compareTo(test.getEntity()),
                examination.compareTo(test)
        );

        entity.setId(examination.getId() - 1);
        test = new Examination(entity);
        Assertions.assertEquals(
                examination.getEntity().compareTo(test.getEntity()),
                examination.compareTo(test)
        );

        entity.setId(examination.getId() + 1);
        test = new Examination(entity);
        Assertions.assertEquals(
                examination.getEntity().compareTo(test.getEntity()),
                examination.compareTo(test)
        );
    }
}