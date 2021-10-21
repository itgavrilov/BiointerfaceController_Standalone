package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.DeviceEntity;

class DeviceTest {
    private final int id = 1;
    private final int amountChannels = 1;
    private final String comment = "commentTest";

    private DeviceEntity entity;
    private Device device;

    @BeforeEach
    void setUp() {
        try {
            device = new Device(id, amountChannels);
            device.setComment(comment);
            entity = new DeviceEntity(id, amountChannels, comment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (device != null)
                device.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error tearDown");
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Device.getAll().contains(device));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            device.delete();
            Assertions.assertFalse(Device.getAll().contains(device));
            device = null;
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error delete");
        }
    }

    @Test
    void getEntity() {
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
        String testComment = "test_setComment";
        try {
            device.setComment(testComment);
            Assertions.assertEquals(testComment, device.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setComment");
        }
    }

    @Test
    void testEquals() {
        Device test = new Device(entity);

        Assertions.assertEquals(test, device);

        entity.setId(id + 1);
        test = new Device(entity);
        Assertions.assertNotEquals(test, device);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(entity.hashCode(), device.hashCode());

        entity.setId(id + 1);
        Assertions.assertNotEquals(entity.hashCode(), device.hashCode());
    }

    @Test
    void compareTo() {
        Device test = new Device(entity);

        Assertions.assertEquals(
                device.getEntity().compareTo(test.getEntity()),
                device.compareTo(test)
        );

        entity.setId(id - 1);
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