package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "channel")
@Table(name = "channel")
public class ChannelEntity implements Serializable, Comparable<ChannelEntity> {
    @Id
    @Column(length = 35)
    private String name;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "channelEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GraphEntity> graphEntities;

    public ChannelEntity() {
    }

    public ChannelEntity(String name, String comment) {
        this.name = name;
        this.comment = comment;
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
                "name='" + name + '\'' +
                '}';
    }
}
