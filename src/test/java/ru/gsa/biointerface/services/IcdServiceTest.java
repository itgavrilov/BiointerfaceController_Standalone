package ru.gsa.biointerface.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.IcdRepository;
import ru.gsa.biointerface.repository.database.DatabaseHandler;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
class IcdServiceTest {
    private static final String name = "testName";
    private static final int version = 10;
    private static final String comment = "testComment";
    private static final List<PatientRecord> patientRecords = new ArrayList<>();
    private static IcdService service;
    private static IcdRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseHandler.constructInstanceForTest();
        service = IcdService.getInstance();
        repository = IcdRepository.getInstance();
    }

    @Test
    void getInstance() throws Exception {
        Assertions.assertSame(service, IcdService.getInstance());
    }

    @Test
    void create() throws Exception {
        Icd entity = service.create(name, version, comment);
        Assertions.assertEquals(name, entity.getName());
        Assertions.assertEquals(comment, entity.getComment());
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.create(null, version, comment));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create("", version, comment));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(name, -1, comment));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(name, 0, comment));
        Assertions.assertDoesNotThrow(
                () -> {
                    service.create(name, version, null);
                });
        Assertions.assertDoesNotThrow(
                () -> {
                    service.create(name, version, "");
                });
    }

    @Test
    void getAll() throws Exception {
        Icd entity = service.create(name, version, comment);
        repository.insert(entity);
        List<Icd> entities = service.getAll();
        Assertions.assertTrue(entities.contains(entity));
        repository.delete(entity);
    }

    @Test
    void getById() throws Exception {
        Icd entity = service.create(name, version, comment);
        repository.insert(entity);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(-1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.getById(0));

        Icd entityTest = service.getById(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
    }

    @Test
    void save() throws Exception {
        Icd entity = new Icd(null, version, comment);
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
        entity.setVersion(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setVersion(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.save(entity));
        entity.setVersion(version);
        entity.setPatientRecords(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(entity));
        entity.setPatientRecords(patientRecords);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.save(entity));
        repository.delete(entity);
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.save(entity));
        Icd entityTest = repository.read(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
    }

    @Test
    void delete() throws Exception {
        Icd entity = new Icd(name, version, comment);
        repository.insert(entity);
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
        Icd entity = new Icd(name, version, comment);
        repository.insert(entity);
        long idTest = entity.getId();
        String nameTest = name + "Update";
        int versionTest = version + 1;
        String commentTest = comment + "Update";
        entity.setName(nameTest);
        entity.setVersion(versionTest);
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
        entity.setVersion(-1);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setVersion(0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(entity));
        entity.setVersion(versionTest);
        entity.setPatientRecords(null);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.update(entity));
        entity.setPatientRecords(patientRecords);
        entity.setComment(null);
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        entity.setComment("");
        Assertions.assertDoesNotThrow(
                () -> service.update(entity));
        repository.delete(entity);
    }
}