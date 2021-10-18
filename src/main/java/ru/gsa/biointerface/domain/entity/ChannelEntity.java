package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;

import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "channel")
@Table(name = "channel")
public class ChannelEntity implements Comparable<ChannelEntity> {
    @Id
    @GeneratedValue(generator="sqlite_channel")
    @TableGenerator(name="sqlite_channel", table="sqlite_sequence",
            pkColumnName="name", valueColumnName="seq",
            pkColumnValue="channel",
            initialValue=1)
    private int id = -1;

    @Column(nullable = false, length = 35)
    private String name;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "channelEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GraphEntity> graphEntities;

    public ChannelEntity() {
    }

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

    public List<GraphEntity> getGraphEntities() {
        return graphEntities;
    }

    public void setGraphEntities(List<GraphEntity> graphEntities) {
        this.graphEntities = graphEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelEntity that = (ChannelEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
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
                '}';
    }
}
