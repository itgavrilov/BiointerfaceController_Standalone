package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.DeviceDAO;

class DeviceTest {
    private final int id = 1;
    private final int amountChannels = 1;
    private String comment = "commentTest";

    private final DeviceEntity entity =
            new DeviceEntity(id, amountChannels, comment);
    private Device device;
    private DeviceDAO dao;

    @BeforeEach
    void setUp() {
        try {
            device = new Device(id, amountChannels);
            device.setComment(comment);
            entity.setId(device.getEntity().getId());
            dao = DeviceDAO.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            device.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Device.getAll().contains(device));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new NullPointerException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            device.delete();
            Assertions.assertFalse(Device.getAll().contains(device));
            device = new Device(id, amountChannels);
            device.setComment(comment);
        } catch (DomainException e) {
            e.printStackTrace();
            throw new NullPointerException("Error delete");
        }
    }

    @Test
    void getEntity() {
        entity.setId(id);
        Assertions.assertEquals(entity, device.getEntity());
    }

    @Test
    void getId() {
        Assertions.assertEquals(entity.getId(), device.getId());
    }

    @Test
    void getAmountChannels() {
        Assertions.assertEquals(amountChannels, device.getAmountChannels());
    }

    @Test
    void getComment() {
        Assertions.assertEquals(comment, device.getComment());
    }

    @Test
    void setComment() {
        comment = "test_setComment";
        try {
            device.setComment(comment);
            Assertions.assertEquals(comment, device.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEquals() {
        Device test;
        try {
            test = new Device(dao.read(device.getEntity().getId()));
            Assertions.assertEquals(test, device);

            entity.setId(id + 1);
            test = new Device(entity);
            Assertions.assertNotEquals(test, device);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHashCode() {
        entity.setId(id + 1);
        Assertions.assertNotEquals(entity.hashCode(), device.hashCode());

        entity.setId(id);
        Assertions.assertEquals(entity.hashCode(), device.hashCode());
    }

    @Test
    void compareTo() {
        Device test;

        entity.setId(id - 1);
        test = new Device(entity);
        Assertions.assertEquals(
                device.getEntity().compareTo(test.getEntity()),
                device.compareTo(test)
        );

        entity.setId(id);
        test = new Device(entity);
        Assertions.assertEquals(
                device.getEntity().compareTo(test.getEntity()),
                device.compareTo(test)
        );

        entity.setId(id + 1);
        test = new Device(entity);
        Assertions.assertEquals(
                device.getEntity().compareTo(test.getEntity()),
                device.compareTo(test)
        );
    }
}