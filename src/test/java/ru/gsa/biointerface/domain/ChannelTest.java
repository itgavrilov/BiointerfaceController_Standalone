package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;

class ChannelTest {
    private final String name = "nameTest";
    private String comment = "commentTest";

    private final ChannelEntity entity =
            new ChannelEntity(name, comment);
    private Channel channel;
    private ChannelDAO dao;

    @BeforeEach
    void setUp() {
        try {
            channel = new Channel(name, comment);
            dao = ChannelDAO.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            channel.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Channel.getAll().contains(channel));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new NullPointerException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            channel.delete();
            Assertions.assertFalse(Channel.getAll().contains(channel));
            channel = new Channel(name, comment);
        } catch (DomainException e) {
            e.printStackTrace();
            throw new NullPointerException("Error delete");
        }
    }

    @Test
    void getEntity() {
        Assertions.assertEquals(entity, channel.getEntity());
    }

    @Test
    void getName() {
        Assertions.assertEquals(name, channel.getName());
    }

    @Test
    void getComment() {
        Assertions.assertEquals(comment, channel.getComment());
    }

    @Test
    void setComment() {
        comment = "test_setComment";
        try {
            channel.setComment(comment);
            Assertions.assertEquals(comment, channel.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEquals() {
        Channel test;
        try {
            entity.setName("test");
            test = new Channel(entity);
            Assertions.assertNotEquals(test, channel);

            entity.setName(name);
            test = new Channel(dao.read(channel.getEntity().getName()));
            Assertions.assertEquals(test, channel);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHashCode() {
        entity.setName("test");
        Assertions.assertNotEquals(entity.hashCode(), channel.hashCode());

        entity.setName(name);
        Assertions.assertEquals(entity.hashCode(), channel.hashCode());
    }

    @Test
    void compareTo() {
        Channel test;

        entity.setName("");
        test = new Channel(entity);
        Assertions.assertEquals(channel.getEntity().compareTo(test.getEntity()), channel.compareTo(test));

        entity.setName(name);
        test = new Channel(entity);
        Assertions.assertEquals(channel.getEntity().compareTo(test.getEntity()), channel.compareTo(test));

        entity.setName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        test= new Channel(entity);
        Assertions.assertEquals(channel.getEntity().compareTo(test.getEntity()), channel.compareTo(test));
    }
}