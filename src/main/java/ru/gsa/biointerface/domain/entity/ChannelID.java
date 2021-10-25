package ru.gsa.biointerface.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChannelID implements Serializable, Comparable<ChannelID> {
    private int number;
    private Examination examination;

    public ChannelID() {
    }

    public ChannelID(int number, Examination examination) {
        this.number = number;
        this.examination = examination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelID that = (ChannelID) o;
        return number == that.number && Objects.equals(examination, that.examination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, examination);
    }

    @Override
    public int compareTo(ChannelID o) {
        int result = examination.compareTo(o.examination);

        if (result == 0)
            result = number - o.number;

        return result;
    }

    @Override
    public String toString() {
        return "ChannelID{" +
                "number=" + number +
                ", examination=" + examination +
                '}';
    }
}
