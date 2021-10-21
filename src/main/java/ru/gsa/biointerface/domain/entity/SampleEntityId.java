package ru.gsa.biointerface.domain.entity;


import java.io.Serializable;
import java.util.Objects;

public class SampleEntityId implements Serializable, Comparable<SampleEntityId> {
    private long id = -1;
    private GraphEntity graphEntity;

    public SampleEntityId() {
    }

    public SampleEntityId(long id, GraphEntity graphEntity) {
        this.id = id;
        this.graphEntity = graphEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleEntityId that = (SampleEntityId) o;
        return id == that.id && Objects.equals(graphEntity, that.graphEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, graphEntity);
    }

    @Override
    public String toString() {
        return "SampleEntityId{" +
                "id=" + id +
                ", graphEntity=" + graphEntity +
                '}';
    }

    @Override
    public int compareTo(SampleEntityId o) {
        long result = graphEntity.compareTo(o.graphEntity);

        if (result == 0)
            result = id - o.id;

        return (int) result;
    }
}
