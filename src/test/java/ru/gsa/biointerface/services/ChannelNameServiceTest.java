package ru.gsa.biointerface.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.database.DatabaseHandler;
import ru.gsa.biointerface.repository.exception.InsertException;
import ru.gsa.biointerface.repository.impl.ChannelNameRepositoryImpl;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
class ChannelNameServiceTest {
    private static final String name = "testName";
    private static final String comment = "testComment";
    private static final Set<Channel> channels = new TreeSet<>();
    private static ChannelNameService service;
    private static ChannelNameRepository repository;

    @BeforeAll
    static void setUp() throws Exception {
        DatabaseHandler.constructInstanceForTest();
        service = ChannelNameService.getInstance();
        repository = ChannelNameRepositoryImpl.getInstance();
    }

    @Test
    void getInstance() throws Exception {
        Assertions.assertSame(service, ChannelNameService.getInstance());
    }

    @Test
    void getAll() throws Exception {
        ChannelName entity = new ChannelName(name, comment);
        repository.insert(entity);
        List<ChannelName> entities = service.getAll();
        Assertions.assertTrue(entities.contains(entity));
        repository.delete(entity);
    }

    @Test
    void getById() throws Exception {
        ChannelName entity = new ChannelName(name, comment);
        repository.insert(entity);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(0));
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getById(entity.getId() + 1));

        ChannelName entityTest = service.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
    }

    @Test
    void save() throws Exception {
        ChannelName entity = new ChannelName(null, comment);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(entity));
        entity.setName("");
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setName(name);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(entity));
        entity.setChannels(channels);
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
        Assertions.assertThrows(
                InsertException.class,
                () -> service.save(new ChannelName(name, comment)));
        ChannelName entityTest = repository.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
    }

    @Test
    void delete() throws Exception {
        ChannelName entity = new ChannelName(name, comment);
        repository.insert(entity);
        long idTest = entity.getId();

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
                    entity.setId(idTest + 1);
                    service.delete(entity);
                });

        entity.setId(idTest);
        Assertions.assertEquals(entity, repository.getById(idTest));
        service.delete(entity);
        Assertions.assertNull(repository.getById(idTest));
    }

    @Test
    void update() throws Exception {
        ChannelName entity = new ChannelName(name, comment);
        repository.insert(entity);
        long idTest = entity.getId();
        String nameTest = name + "Update";
        String commentTest = comment + "Update";
        entity.setName(nameTest);
        entity.setComment(commentTest);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(null)
        );
        entity.setId(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setId(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setId(idTest + 1);
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(entity));
        entity.setId(idTest);
        entity.setName(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setName("");
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setName(nameTest);
        entity.setChannels(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setChannels(channels);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        repository.delete(entity);
    }
}