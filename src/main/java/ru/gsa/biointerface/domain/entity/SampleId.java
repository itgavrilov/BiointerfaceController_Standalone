package ru.gsa.biointerface.domain.entity;


import java.io.Serializable;
import java.util.Objects;

public class SampleId implements Serializable, Comparable<SampleId> {
    private long id;
    private Graph graph;

    public SampleId() {
    }

    public SampleId(long id, Graph graph) {
        this.id = id;
        this.graph = graph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleId that = (SampleId) o;
        return id == that.id && Objects.equals(graph, that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, graph);
    }

    @Override
    public String toString() {
        return "SampleId{" +
                "id=" + id +
                ", graph=" + graph +
                '}';
    }

    @Override
    public int compareTo(SampleId o) {
        int result = graph.compareTo(o.graph);

        if (result == 0) {
            if(id > o.id) {
                result = 1;
            } else if(id < o.id){
                result = -1;
            }
        }

        return result;
    }
}
