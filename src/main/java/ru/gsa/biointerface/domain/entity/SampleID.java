package ru.gsa.biointerface.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class SampleID implements Serializable, Comparable<SampleID> {
    private int id;

    @Embedded
    private ChannelID channel_id;

    public SampleID(Integer id, ChannelID channel_id) {
        this.id = id;
        this.channel_id = channel_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleID sampleID = (SampleID) o;
        return id == sampleID.id && Objects.equals(channel_id, sampleID.channel_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel_id);
    }

    @Override
    public int compareTo(SampleID o) {
        if (o == null || getClass() != o.getClass()) return -1;
        int result = channel_id.compareTo(o.channel_id);

        if (result == 0) {
            if (id > o.id) {
                result = 1;
            } else if (id < o.id) {
                result = -1;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        String channelId = "-";
        String examinationId = "-";

        if (channel_id != null) {
            channelId = String.valueOf(channel_id.getNumber());
            examinationId = String.valueOf(channel_id.getExamination_id());
        }
        return "Sample{" +
                "id=" + id +
                ", channel_id=" + channelId +
                ", examination_id=" + examinationId +
                '}';
    }
}
