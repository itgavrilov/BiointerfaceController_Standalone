package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.ChannelRepository;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.SampleRepository;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaminationService.class);
    private static ExaminationService instance = null;
    private final ExaminationRepository dao;
    private final ChannelRepository daoGraph;
    private final SampleRepository daoSample;

    private ExaminationService() throws Exception {
        dao = ExaminationRepository.getInstance();
        daoGraph = ChannelRepository.getInstance();
        daoSample = SampleRepository.getInstance();
    }

    public static ExaminationService getInstance() throws Exception {
        if (instance == null) {
            instance = new ExaminationService();
        }

        return instance;
    }

    public Examination create(
            PatientRecord patientRecord,
            Device device,
            List<ChannelName> channelNames,
            String comment
    ) throws Exception {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new NullPointerException("Device is null");
        if (channelNames == null)
            throw new NullPointerException("ChannelNames is null");
        if (channelNames.size() != device.getAmountChannels())
            throw new IllegalArgumentException("Amount channelNames differs from amount in device");

        Examination entity = new Examination(
                Timestamp.valueOf(LocalDateTime.now()),
                patientRecord,
                device,
                comment);
        patientRecord.getExaminations().add(entity);
        device.getExaminations().add(entity);

        for (int i = 0; i < device.getAmountChannels(); i++) {
            ChannelName channelName = channelNames.get(i);
            Channel channel = new Channel(i, entity, channelName);
            entity.getChannels().add(channel);

            if(channelName != null) {
                channelName.getChannels().add(channel);
            }
        }

        LOGGER.info("New examination created");

        return entity;
    }

    public List<Examination> getAll() throws Exception {
        List<Examination> entities = dao.getAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all examinations from database");
        } else {
            LOGGER.info("Examinations is not found in database");
        }

        return entities;
    }

    public List<Examination> getByPatientRecord(PatientRecord patientRecord) throws Exception {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");

        List<Examination> entities = dao.getByPatientRecord(patientRecord);

        if (entities.size() > 0) {
            LOGGER.info("Get all examinations by patientRecord(id={}) from database", patientRecord.getId());
        } else {
            LOGGER.info("Examinations by patientRecord(id={}) is not found in database", patientRecord.getId());
        }

        return entities;
    }

    public Examination getById(long id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Examination entity = dao.read(id);

        if (entity != null) {
            LOGGER.info("Get examination(id={}) from database", entity.getId());
        } else {
            LOGGER.error("Examination(id={}) is not found in database", id);
            throw new EntityNotFoundException("Examination(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void recordingStart(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.isRecording())
            throw new IllegalArgumentException("Entity is already being recorded");
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");

        Examination readEntity = dao.read(entity.getId());

        if (readEntity == null) {
            dao.transactionOpen();
            LOGGER.info("Transaction started");
            dao.insert(entity);
            LOGGER.info("Examination(id={}) is recorded", entity.getId());
            entity.recordingStart();
        } else {
            LOGGER.error(
                    "Examination(id={}) already exists in database. Recording is not start",
                    entity.getId());
            throw new IllegalArgumentException(
                    "Examination(id=" + entity.getId() + ") already exists in database. Recording is not start"
            );
        }
    }

    public void recordingStop(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        entity.recordingStop();
        dao.transactionClose();
    }

    public void delete(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Examination readEntity = dao.read(entity.getId());

        if (readEntity != null) {
            dao.delete(entity);
            LOGGER.info("Examination(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.info("Examination(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "Examination(id=" + entity.getId() + ") is not found in database"
            );
        }
    }

    public void update(Examination entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getStartTime() == null)
            throw new NullPointerException("StartTime is null");
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");
        if (entity.getChannels().size() != entity.getDevice().getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");


        Examination readEntity = dao.read(entity.getId());

        if (readEntity != null) {
            dao.update(entity);
            LOGGER.info("Examination(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("Examination(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Examination(id=" + entity.getId() + ") not found in database");
        }
    }

    public Examination loadWithGraphsById(Long id) throws Exception {
        Examination entity = getById(id);
        entity.setChannels(daoGraph.getAllByExamination(entity));

        for (Channel channel : entity.getChannels()) {
            channel.setSamples(daoSample.getAllByGraph(channel));
        }

        LOGGER.info("Examination(id={}) load with graphs from database", entity.getId());

        return entity;
    }
}
