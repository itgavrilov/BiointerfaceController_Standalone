package ru.gsa.biointerface.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.ChannelEntity;

class ChannelTest {
    private final String name = "nameTest";
    private final String comment = "commentTest";

    private ChannelEntity entity;
    private Channel channel;

    @BeforeEach
    void setUp() {
        entity = new ChannelEntity(name, comment);

        try {
            channel = new Channel(name, comment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error setUp");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (channel != null)
                channel.delete();
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error tearDown");
        }
    }

    @Test
    void getAll() {
        try {
            Assertions.assertTrue(Channel.getAll().contains(channel));
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getAll");
        }
    }

    @Test
    void delete() {
        try {
            channel.delete();
            Assertions.assertFalse(Channel.getAll().contains(channel));
            channel = null;
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error delete");
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
        String testComment = "test_setComment";
        try {
            channel.setComment(testComment);
            Assertions.assertEquals(testComment, channel.getComment());
        } catch (DomainException e) {
            e.printStackTrace();
            throw new RuntimeException("Error setComment");
        }
    }

    @Test
    void testEquals() {
        Channel test = new Channel(entity);

        Assertions.assertEquals(test, channel);

        entity.setName("test");
        test = new Channel(entity);
        Assertions.assertNotEquals(test, channel);
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(entity.hashCode(), channel.hashCode());

        entity.setName("test");
        Assertions.assertNotEquals(entity.hashCode(), channel.hashCode());
    }

    @Test
    void compareTo() {
        Channel test = new Channel(entity);

        Assertions.assertEquals(channel.getEntity().compareTo(test.getEntity()), channel.compareTo(test));

        entity.setName("");
        test = new Channel(entity);
        Assertions.assertEquals(channel.getEntity().compareTo(test.getEntity()), channel.compareTo(test));

        entity.setName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        test = new Channel(entity);
        Assertions.assertEquals(channel.getEntity().compareTo(test.getEntity()), channel.compareTo(test));
    }
}