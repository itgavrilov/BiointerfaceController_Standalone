package ru.gsa.biointerface.domain.entity;


import java.io.Serializable;
import java.util.Objects;

public class SampleID implements Serializable, Comparable<SampleID> {
    private long id;
    private Channel channel;

    public SampleID() {
    }

    public SampleID(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleID that = (SampleID) o;
        return id == that.id && Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel);
    }

    @Override
    public String toString() {
        return "SampleID{" +
                "id=" + id +
                ", channel=" + channel +
                '}';
    }

    @Override
    public int compareTo(SampleID o) {
        int result = channel.compareTo(o.channel);

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
