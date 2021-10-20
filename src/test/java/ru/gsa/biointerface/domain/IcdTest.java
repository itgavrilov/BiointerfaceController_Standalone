package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;

class IcdTest {
    private final String name = "nameTest";
    private final Integer version = 10;
    private String comment = "commentTest";

    private final IcdEntity entity = new IcdEntity(-1, name, version, comment);
    private Icd icd;
    private IcdDAO dao;

    @BeforeEach
    void setUp() {
        try {
            icd = new Icd(name, version, comment);
            entity.setId(icd.getEntity().getId());
            dao = IcdDAO.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("icd is null");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            icd.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Icd.getAll().contains(icd));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new NullPointerException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            icd.delete();
            Assertions.assertFalse(Icd.getAll().contains(icd));
            icd = new Icd(name, version, comment);
            entity.setId(icd.getEntity().getId());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new NullPointerException("Error delete");
        }
    }

    @Test
    void getEntity() {
        Assertions.assertEquals(entity, icd.getEntity());
    }

    @Test
    void getId() {
        Assertions.assertEquals(entity.getId(), icd.getId());
    }

    @Test
    void getICD() {
        Assertions.assertEquals(name, icd.getICD());
    }

    @Test
    void getVersion() {
        Assertions.assertEquals(version, icd.getVersion());
    }

    @Test
    void getComment() {
        Assertions.assertEquals(comment, icd.getComment());
    }

    @Test
    void setComment() {
        comment = "test_setComment";
        try {
            icd.setComment(comment);
            Assertions.assertEquals(comment, icd.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEquals() {
        Icd testIcd;
        try {
            testIcd = new Icd(dao.read(icd.getId()));
            Assertions.assertEquals(testIcd, icd);

            entity.setId(-1);
            testIcd = new Icd(entity);
            Assertions.assertNotEquals(icd, testIcd);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHashCode() {
        entity.setId(-1);
        Assertions.assertNotEquals(entity.hashCode(), icd.hashCode());

        entity.setId(icd.getEntity().getId());
        Assertions.assertEquals(entity.hashCode(), icd.hashCode());
    }

    @Test
    void compareTo() {
        entity.setId(icd.getEntity().getId()-1);
        Icd testIcd = new Icd(entity);
        Assertions.assertTrue(icd.compareTo(testIcd) > 0);

        entity.setId(icd.getEntity().getId());
        testIcd = new Icd(entity);
        Assertions.assertEquals(0, icd.compareTo(testIcd));

        entity.setId(icd.getEntity().getId() + 1);
        testIcd = new Icd(entity);
        Assertions.assertTrue(icd.compareTo(testIcd) < 0);

    }
}