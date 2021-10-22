package ru.gsa.biointerface.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class GraphId implements Serializable, Comparable<GraphId> {
    private int numberOfChannel;
    private Examination examination;

    public GraphId() {
    }

    public GraphId(int numberOfChannel, Examination examination) {
        this.numberOfChannel = numberOfChannel;
        this.examination = examination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphId that = (GraphId) o;
        return numberOfChannel == that.numberOfChannel && Objects.equals(examination, that.examination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfChannel, examination);
    }

    @Override
    public int compareTo(GraphId o) {
        int result = examination.compareTo(o.examination);

        if (result == 0)
            result = numberOfChannel - o.numberOfChannel;

        return result;
    }

    @Override
    public String toString() {
        return "GraphId{" +
                "numberOfChannel=" + numberOfChannel +
                ", examination=" + examination +
                '}';
    }
}
