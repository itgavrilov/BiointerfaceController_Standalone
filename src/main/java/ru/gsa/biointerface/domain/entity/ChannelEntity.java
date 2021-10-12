package ru.gsa.biointerface.domain.entity;


import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelEntity implements Comparable<ChannelEntity> {
    private int id;
    private String name;
    private String comment;


    public ChannelEntity(int id, String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelEntity that = (ChannelEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public int compareTo(ChannelEntity o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "ChannelEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
