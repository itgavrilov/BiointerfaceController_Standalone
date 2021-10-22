package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "sample")
@Table(name = "sample")
@IdClass(SampleId.class)
public class Sample implements Serializable, Comparable<Sample> {
    @Id
    private long id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "examination_id", referencedColumnName = "examination_id"),
            @JoinColumn(name = "numberOfChannel", referencedColumnName = "numberOfChannel")
    })
    private Graph graph;

    private int value;

    public Sample() {
    }

    public Sample(long id, Graph graph, int value) {
        this.id = id;
        this.graph = graph;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Graph getGraphEntity() {
        return graph;
    }

    public void setGraphEntity(Graph graph) {
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
        Sample that = (Sample) o;
        return id == that.id && Objects.equals(graph, that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, graph);
    }

    @Override
    public int compareTo(Sample o) {
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

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + id +
                ", examination_id=" + graph.getExaminationEntity().getId() +
                ", numberOfChannel=" + graph.getNumberOfChannel() +
                ", value=" + value +
                '}';
    }
}
