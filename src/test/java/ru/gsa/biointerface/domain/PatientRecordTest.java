package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;

import java.time.LocalDate;

class PatientRecordTest {
    private final int id = 1;
    private final String secondName = "testSecondName";
    private final String firstName = "testFirstName";
    private final String middleName = "testSecondName";
    private final String comment = "commentTest";
    private final LocalDate birthday = LocalDate.now();

    private PatientRecord patientRecord;
    private PatientRecordEntity entity;

    @BeforeEach
    void setUp() {
        try {
            patientRecord = new PatientRecord(
                    id,
                    secondName,
                    firstName,
                    middleName,
                    birthday,
                    new Icd(null),
                    comment
            );

            entity = new PatientRecordEntity(
                    id,
                    secondName,
                    firstName,
                    middleName,
                    patientRecord.getEntity().getBirthday(),
                    null,
                    comment
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (patientRecord != null)
                patientRecord.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error tearDown");
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(PatientRecord.getAll().contains(patientRecord));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            patientRecord.delete();
            Assertions.assertFalse(PatientRecord.getAll().contains(patientRecord));
            patientRecord = null;
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error delete");
        }
    }

    @Test
    void getEntity() {
        Assertions.assertEquals(entity, patientRecord.getEntity());
    }

    @Test
    void getId() {
        Assertions.assertEquals(entity.getId(), patientRecord.getId());
    }

    @Test
    void getSecondName() {
        Assertions.assertEquals(secondName, patientRecord.getSecondName());
    }

    @Test
    void getFirstName() {
        Assertions.assertEquals(firstName, patientRecord.getFirstName());
    }

    @Test
    void getMiddleName() {
        Assertions.assertEquals(middleName, patientRecord.getMiddleName());
    }

    @Test
    void getBirthday() {
        Assertions.assertEquals(birthday, patientRecord.getBirthday());
    }

    @Test
    void getIcd() {
        try {
            Icd icd = new Icd(
                    "patientRecordTest",
                    10,
                    comment
            );


            Assertions.assertNotEquals(icd, patientRecord.getIcd());

            patientRecord.getEntity().setIcdEntity(icd.getEntity());
            Assertions.assertEquals(icd, patientRecord.getIcd());

            icd.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setIcd");
        }
    }

    @Test
    void setIcd() {
        try {
            Icd icd = new Icd(
                    "patientRecordTest",
                    10,
                    comment
            );

            patientRecord.setIcd(icd);
            Assertions.assertEquals(icd, patientRecord.getIcd());

            icd.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setIcd");
        }
    }

    @Test
    void getComment() {
        Assertions.assertEquals(comment, patientRecord.getComment());
    }

    @Test
    void setComment() {
        String testComment = "test_setComment";
        try {
            patientRecord.setComment(testComment);
            Assertions.assertEquals(testComment, patientRecord.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setComment");
        }
    }

    @Test
    void testEquals() {
        PatientRecord test = new PatientRecord(entity);

        Assertions.assertEquals(test, patientRecord);

        entity.setId(-1);
        test = new PatientRecord(entity);
        Assertions.assertNotEquals(test, patientRecord);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(entity.hashCode(), patientRecord.hashCode());

        entity.setId(-1);
        Assertions.assertNotEquals(entity.hashCode(), patientRecord.hashCode());
    }

    @Test
    void compareTo() {
        PatientRecord test = new PatientRecord(entity);

        Assertions.assertEquals(
                patientRecord.getEntity().compareTo(test.getEntity()),
                patientRecord.compareTo(test)
        );

        entity.setId(id - 1);
        test = new PatientRecord(entity);
        Assertions.assertEquals(
                patientRecord.getEntity().compareTo(test.getEntity()),
                patientRecord.compareTo(test)
        );

        entity.setId(id + 1);
        test = new PatientRecord(entity);
        Assertions.assertEquals(
                patientRecord.getEntity().compareTo(test.getEntity()),
                patientRecord.compareTo(test)
        );
    }
}