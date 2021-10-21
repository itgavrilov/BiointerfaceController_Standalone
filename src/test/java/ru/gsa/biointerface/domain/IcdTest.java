package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.IcdEntity;

class IcdTest {
    private final String name = "nameTest";
    private final Integer version = 10;
    private final String comment = "commentTest";
    private int id;

    private IcdEntity entity;
    private Icd icd;

    @BeforeEach
    void setUp() {
        try {
            icd = new Icd(name, version, comment);
            id = icd.getEntity().getId();
            entity = new IcdEntity(id, name, version, comment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (icd != null)
                icd.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error tearDown");
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Icd.getAll().contains(icd));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            icd.delete();
            Assertions.assertFalse(Icd.getAll().contains(icd));
            icd = null;
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error delete");
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
        String testComment = "test_setComment";
        try {
            icd.setComment(testComment);
            Assertions.assertEquals(testComment, icd.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setComment");
        }
    }

    @Test
    void testEquals() {
        Icd test = new Icd(entity);

        Assertions.assertEquals(test, icd);

        entity.setId(-1);
        test = new Icd(entity);
        Assertions.assertNotEquals(test, icd);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(entity.hashCode(), icd.hashCode());

        entity.setId(-1);
        Assertions.assertNotEquals(entity.hashCode(), icd.hashCode());
    }

    @Test
    void compareTo() {
        Icd test = new Icd(entity);

        Assertions.assertEquals(
                icd.getEntity().compareTo(test.getEntity()),
                icd.compareTo(test)
        );

        entity.setId(id - 1);
        test = new Icd(entity);
        Assertions.assertEquals(
                icd.getEntity().compareTo(test.getEntity()),
                icd.compareTo(test)
        );

        entity.setId(id + 1);
        test = new Icd(entity);
        Assertions.assertEquals(
                icd.getEntity().compareTo(test.getEntity()),
                icd.compareTo(test)
        );
    }
}