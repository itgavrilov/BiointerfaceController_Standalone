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
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.save(null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Icd entity = new Icd(-1, null, version, comment, patientRecords);
                    service.save(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Icd entity = new Icd(-1, "", version, comment, patientRecords);
                    service.save(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Icd entity = new Icd(-1, name, -1, comment, patientRecords);
                    service.save(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Icd entity = new Icd(-1, name, 0, comment, patientRecords);
                    service.save(entity);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    Icd entity = new Icd(-1, name, version, comment, null);
                    service.save(entity);
                });
        Assertions.assertDoesNotThrow(
                () -> {
                    Icd entity = new Icd(-1, name, version, null, patientRecords);
                    service.save(entity);
                    repository.delete(entity);
                });
        Assertions.assertDoesNotThrow(
                () -> {
                    Icd entity = new Icd(-1, name, version, "", patientRecords);
                    service.save(entity);
                    repository.delete(entity);
                });
        Icd entity = new Icd(-1, name, version, comment, patientRecords);
        repository.insert(entity);
        Icd entityTest = repository.read(entity.getId());
        Assertions.assertEquals(entity, entityTest);
        repository.delete(entity);
    }

    @Test
    void delete() throws Exception {
        Icd entity = new Icd(-1, name, version, comment, patientRecords);
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
        Icd entity = new Icd(-1, name, version, comment, patientRecords);
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
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setId(-1);
                    service.update(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setId(0);
                    service.update(entity);
                });
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> {
                    entity.setId(idTest + 1);
                    service.update(entity);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    entity.setId(idTest);
                    entity.setName(null);
                    service.update(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setName("");
                    service.update(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setName(nameTest);
                    entity.setVersion(-1);
                    service.update(entity);
                });
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    entity.setVersion(0);
                    service.update(entity);
                });
        Assertions.assertThrows(
                NullPointerException.class,
                () -> {
                    entity.setVersion(versionTest);
                    entity.setPatientRecords(null);
                    service.update(entity);
                });
        Assertions.assertDoesNotThrow(
                () -> {
                    entity.setPatientRecords(patientRecords);
                    entity.setComment(null);
                    service.update(entity);
                });
        Assertions.assertDoesNotThrow(
                () -> {
                    entity.setComment("");
                    service.update(entity);
                });
        repository.delete(entity);
    }
}