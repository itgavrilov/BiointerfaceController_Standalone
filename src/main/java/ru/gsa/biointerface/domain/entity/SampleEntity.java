package ru.gsa.biointerface.domain.entity;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleEntity implements Comparable<SampleEntity> {
    private int id;
    private GraphEntity graph;
    private int value;

    public SampleEntity(int id, GraphEntity graph, int value) {
        this.id = id;
        this.graph = graph;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GraphEntity getGraph() {
        return graph;
    }

    public void setGraph(GraphEntity graph) {
        this.graph = graph;
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
        return id == that.id && graph.equals(that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, graph);
    }

    @Override
    public int compareTo(SampleEntity o) {
        int result = graph.compareTo(o.graph);

        if (result == 0)
            result = id - o.id;

        return result;
    }
}
