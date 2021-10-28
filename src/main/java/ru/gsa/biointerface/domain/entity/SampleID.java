package ru.gsa.biointerface.domain.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleID implements Serializable, Comparable<SampleID> {
    @NotNull
    @Min(0)
    private long id;

    @NotNull
    private Channel channel;

    public SampleID() {
    }

    public SampleID(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    public SampleID getPK(){
        return new SampleID(id, channel);
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
        String channelId = "-";

        if (channel != null)
            channelId = String.valueOf(channel.getId());

        return "SampleID{" +
                "id=" + id +
                ", channel=" + channelId +
                '}';
    }

    @Override
    public int compareTo(SampleID o) {
        int result = channel.compareTo(o.channel);

        if (result == 0) {
            if (id > o.id) {
                result = 1;
            } else if (id < o.id) {
                result = -1;
            }
        }

        return result;
    }
}
