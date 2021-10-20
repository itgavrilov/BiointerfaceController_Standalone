package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "sample")
@Table(name = "sample")
@IdClass(SampleEntityId.class)
public class SampleEntity {
    @Id
    private long id = -1;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "examination_id", referencedColumnName = "examination_id"),
            @JoinColumn(name = "numberOfChannel", referencedColumnName = "numberOfChannel")
    })
    private GraphEntity graphEntity;

    private int value;

    public SampleEntity() {
    }

    public SampleEntity(long id, GraphEntity graphEntity, int value) {
        this.id = id;
        this.graphEntity = graphEntity;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GraphEntity getGraphEntity() {
        return graphEntity;
    }

    public void setGraphEntity(GraphEntity graphEntity) {
        this.graphEntity = graphEntity;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleEntity that = (SampleEntity) o;
        return id == that.id && Objects.equals(graphEntity, that.graphEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, graphEntity);
    }

    @Override
    public String toString() {
        return "SampleEntity{" +
                "id=" + id +
                ", examination_id=" + graphEntity.getExaminationEntity().getId() +
                ", numberOfChannel=" + graphEntity.getNumberOfChannel() +
                ", value=" + value +
                '}';
    }
}
