package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "channel")
@Table(name = "channel")
@IdClass(ChannelID.class)
public class Channel implements Serializable, Comparable<Channel> {
    @Id
    int id;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_id", referencedColumnName = "id")
    private Examination examination;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channelName_id", referencedColumnName = "id")
    private ChannelName channelName;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Sample> samples;

    public Channel() {

    }

    public Channel(int id, Examination examination, ChannelName channelName, List<Sample> samples) {
        this.id = id;
        this.examination = examination;
        this.channelName = channelName;
        this.samples = samples;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Examination getExamination() {
        return examination;
    }

    public void setExamination(Examination examination) {
        this.examination = examination;
    }

    public ChannelName getChannelName() {
        return channelName;
    }

    public void setChannelName(ChannelName channelName) {
        this.channelName = channelName;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel that = (Channel) o;
        return id == that.id && Objects.equals(examination, that.examination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examination);
    }

    @Override
    public int compareTo(Channel o) {
        int result = examination.compareTo(o.examination);

        if (result == 0)
            result = id - o.id;

        return result;
    }

    @Override
    public String toString() {
        String channelId = "-";
        String examinationId = "-";

        if (channelName != null)
            channelId = String.valueOf(channelName.getId());

        if (examination != null)
            examinationId = String.valueOf(examination.getId());

        return "Channel{" +
                "number=" + id +
                ", examinationEntity_id=" + examinationId +
                ", channelName_id=" + channelId +
                '}';
    }
}

