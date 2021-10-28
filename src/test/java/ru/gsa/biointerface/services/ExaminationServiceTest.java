package ru.gsa.biointerface.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.repository.DeviceRepository;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.PatientRecordRepository;
import ru.gsa.biointerface.repository.database.DatabaseHandler;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
class ExaminationServiceTest {
    private static final String comment = "testComment";
    private static final Date startTime = Timestamp.valueOf(LocalDateTime.now());
    private static final PatientRecord patientRecord = new PatientRecord(
            1,
            "secondNameTest",
            "firstNameTest",
            "middleNameTest",
            new GregorianCalendar(2021, Calendar.NOVEMBER, 27),
            null,
            comment);
    private static final Device device = new Device(1, 1, comment);
    private static final List<ChannelName> channelNames = new ArrayList<>();
    private static final List<Channel> channels = new ArrayList<>();
    private static ExaminationService service;
    private static ExaminationRepository repository;

    @BeforeAll
    static void setUp() throws Exception {
        DatabaseHandler.constructInstanceForTest();
        service = ExaminationService.getInstance();
        repository = ExaminationRepository.getInstance();
        PatientRecordRepository.getInstance().insert(patientRecord);
        DeviceRepository.getInstance().insert(device);
        channels.add(new Channel(0, null, null, new ArrayList<>()));
        channelNames.add(null);
    }

    @AfterAll
    static void tearDown() throws Exception {
        PatientRecordRepository.getInstance().delete(patientRecord);
        DeviceRepository.getInstance().delete(device);
    }

    @Test
    void getInstance() throws Exception {
        Assertions.assertSame(service, ExaminationService.getInstance());
    }

    @Test
    void create() throws Exception {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.create(null, device, channelNames, comment));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.create(patientRecord, null, channelNames, comment));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.create(patientRecord, device, null, comment));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(patientRecord, device, new ArrayList<>(), comment));
        Assertions.assertDoesNotThrow(
                () -> service.create(patientRecord, device, channelNames, null));
        Assertions.assertDoesNotThrow(
                () -> service.create(patientRecord, device, channelNames, ""));
        Examination entity =
                service.create(patientRecord, device, channelNames, comment);
        channels.get(0).setExamination(entity);
        Assertions.assertEquals(patientRecord, entity.getPatientRecord());
        Assertions.assertEquals(device, entity.getDevice());
        Assertions.assertEquals(channels.get(0), entity.getChannels().get(0));
    }

    @Test
    void getAll() throws Exception {
        Examination entity =
                service.create(patientRecord, device, channelNames, comment);
        channels.get(0).setExamination(entity);
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        List<Examination> channelNames = service.getAll();
        Assertions.assertTrue(channelNames.contains(entity));
        repository.delete(entity);
        Assertions.assertNull(repository.read(entity.getId()));
    }

    @Test
    void getByPatientRecord() throws Exception {
        Examination entity =
                service.create(patientRecord, device, channelNames, comment);
        PatientRecord patientRecordTest = entity.getPatientRecord();
        Assertions.assertEquals(patientRecord, patientRecordTest);
    }

    @Test
    void getById() throws Exception {
        Examination entity =
                new Examination(startTime, patientRecord, device, comment, channels);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        channels.get(0).setExamination(entity);
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(0));

        Examination entityTest = service.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
        Assertions.assertNull(repository.read(entity.getId()));
    }

    @Test
    void recordingStart() throws Exception {
        Examination entity =
                new Examination(startTime, patientRecord, device, comment, channels);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        channels.get(0).setExamination(entity);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        entity.getChannels().get(0).setExamination(entity);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        entity.recordingStart();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(entity));
        entity.recordingStop();
        entity.setPatientRecord(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(entity));
        entity.setPatientRecord(patientRecord);
        entity.setDevice(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(entity));
        entity.setDevice(device);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(entity));
        entity.setChannels(new ArrayList<>());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(entity));
        entity.setChannels(channels);
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.recordingStart(entity));
        repository.delete(entity);
        Assertions.assertDoesNotThrow(
                () -> service.recordingStart(entity));
        Assertions.assertTrue(entity.isRecording());
        Assertions.assertTrue(repository.transactionIsOpen());
        entity.recordingStop();
        repository.transactionClose();
        Assertions.assertEquals(entity, repository.read(entity.getId()));
        Channel channel = ChannelRepository.getInstance().read(new ChannelID(
                entity.getChannels().get(0).getId(),
                entity
        ));
        Assertions.assertEquals(channels.get(0), channel);
        repository.delete(entity);
        Assertions.assertNull(repository.read(entity.getId()));
    }

    @Test
    void recordingStop() throws Exception {
        Examination entity =
                new Examination(startTime, patientRecord, device, comment, channels);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        channels.get(0).setExamination(entity);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        entity.getChannels().get(0).setExamination(entity);
        repository.transactionOpen();
        repository.insert(entity);
        entity.recordingStart();
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.recordingStart(null));
        Assertions.assertDoesNotThrow(
                () -> service.recordingStop(entity));

        Assertions.assertEquals(entity, repository.read(entity.getId()));
        repository.delete(entity);
        Assertions.assertNull(repository.read(entity.getId()));
    }

    @Test
    void delete() throws Exception {
        Examination entity =
                new Examination(startTime, patientRecord, device, comment, channels);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        channels.get(0).setExamination(entity);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        entity.getChannels().get(0).setExamination(entity);
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        long id = entity.getId();
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.delete(null));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setId(-1);
                    service.delete(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setId(0);
                    service.delete(entity);
                });
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    entity.setId(id + 1);
                    service.delete(entity);
                });

        entity.setId(id);
        Assertions.assertEquals(entity, repository.read(id));
        service.delete(entity);
        Assertions.assertNull(repository.read(id));
    }

    @Test
    void update() throws Exception {
        Examination entity =
                new Examination(startTime, patientRecord, device, comment, channels);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        channels.get(0).setExamination(entity);
        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();
        long id = entity.getId();
        Date startTimeTest = Timestamp.valueOf(LocalDateTime.now());
        String commentTest = comment + "Update";
        entity.setStartTime(startTimeTest);
        entity.setComment(commentTest);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(null));
        entity.setId(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setId(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setId(id + 1);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(entity));
        entity.setId(id);
        entity.setStartTime(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setStartTime(startTimeTest);
        entity.setPatientRecord(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setPatientRecord(patientRecord);
        entity.setDevice(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setDevice(device);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setChannels(new ArrayList<>());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setChannels(channels);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        repository.delete(entity);
        Assertions.assertNull(repository.read(entity.getId()));
    }

    @Test
    void loadWithGraphsById() throws Exception {
        Examination entity =
                new Examination(startTime, patientRecord, device, comment, channels);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);
        channels.get(0).setExamination(entity);
        channels.get(0).getSamples().add(new Sample(0, channels.get(0), 10));

        repository.transactionOpen();
        repository.insert(entity);
        repository.transactionClose();

        Examination entityTest = service.loadWithGraphsById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        Assertions.assertEquals(device, entityTest.getDevice());
        Assertions.assertEquals(patientRecord, entityTest.getPatientRecord());
        Assertions.assertEquals(comment, entityTest.getComment());
        Assertions.assertEquals(channels.get(0), entityTest.getChannels().get(0));
        Assertions.assertEquals(
                channels.get(0).getSamples().get(0),
                entityTest.getChannels().get(0).getSamples().get(0));

        repository.delete(entity);
        Assertions.assertNull(repository.read(entity.getId()));
    }
}