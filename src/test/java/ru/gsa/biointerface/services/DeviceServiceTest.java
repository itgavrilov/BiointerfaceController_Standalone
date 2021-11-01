package ru.gsa.biointerface.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.repository.DeviceRepository;
import ru.gsa.biointerface.repository.database.DatabaseHandler;
import ru.gsa.biointerface.repository.impl.DeviceRepositoryImpl;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
class DeviceServiceTest {
    private static final long id = 1;
    private static final int amountChannels = 1;
    private static final String comment = "testComment";
    private static final Set<Examination> examinations = new TreeSet<>();
    private static DeviceService service;
    private static DeviceRepository repository;

    @BeforeAll
    static void setUp() throws Exception {
        DatabaseHandler.constructInstanceForTest();
        service = DeviceService.getInstance();
        repository = DeviceRepositoryImpl.getInstance();
    }

    @Test
    void getInstance() throws Exception {
        Assertions.assertSame(service, DeviceService.getInstance());
    }

    @Test
    void getAll() throws Exception {
        Device entity = new Device(id, amountChannels);
        repository.insert(entity);
        List<Device> channelNames = service.getAll();
        Assertions.assertTrue(channelNames.contains(entity));
        repository.delete(entity);
    }

    @Test
    void getById() throws Exception {
        Device entity = new Device(id, amountChannels);
        repository.insert(entity);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(0));

        Device entityTest = service.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
    }

    @Test
    void save() throws Exception {
        Device entity = new Device(-1, amountChannels);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(null));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setId(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setId(id);
        entity.setAmountChannels(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setAmountChannels(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setAmountChannels(amountChannels);
        entity.setExaminations(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(entity));
        entity.setExaminations(examinations);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.save(entity));
        repository.delete(entity);
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.save(entity));

        repository.delete(entity);
        entity.setComment(comment);
        repository.insert(entity);
        Device entityTest = repository.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
        Assertions.assertNull(repository.getById(entity.getId()));
    }

    @Test
    void delete() throws Exception {
        Device entity = new Device(id, amountChannels);
        repository.insert(entity);

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
        Assertions.assertEquals(entity, repository.getById(id));
        service.delete(entity);
        Assertions.assertNull(repository.getById(id));
    }

    @Test
    void update() throws Exception {
        Device entity = new Device(id, amountChannels);
        repository.insert(entity);
        int amountChannelsTest = amountChannels + 1;
        String commentTest = comment + "Update";
        entity.setAmountChannels(amountChannelsTest);
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
        entity.setAmountChannels(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setAmountChannels(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setAmountChannels(amountChannelsTest);
        entity.setExaminations(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setExaminations(examinations);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        repository.delete(entity);
    }
}