package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "graph")
@Table(name = "graph")
@IdClass(GraphId.class)
public class Graph implements Serializable, Comparable<Graph> {
    @Id
    int numberOfChannel;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", referencedColumnName = "id")
    private Examination examination;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;

    @OneToMany(mappedBy = "graph", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Sample> samples;

    public Graph() {

    }

    public Graph(int numberOfChannel, Examination examination, Channel channel, List<Sample> samples) {
        this.numberOfChannel = numberOfChannel;
        this.examination = examination;
        this.channel = channel;
        this.samples = samples;
    }

    public int getNumberOfChannel() {
        return numberOfChannel;
    }

    public void setNumberOfChannel(int numberOfChannel) {
        this.numberOfChannel = numberOfChannel;
    }

    public Examination getExaminationEntity() {
        return examination;
    }

    public void setExaminationEntity(Examination examination) {
        this.examination = examination;
    }

    public Channel getChannelEntity() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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
        Graph that = (Graph) o;
        return numberOfChannel == that.numberOfChannel && Objects.equals(examination, that.examination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfChannel, examination);
    }

    @Override
    public int compareTo(Graph o) {
        int result = examination.compareTo(o.examination);

        if (result == 0)
            result = numberOfChannel - o.numberOfChannel;

        return result;
    }

    @Override
    public String toString() {
        String channelId = "-";
        String examinationId = "-";

        if (channel != null)
            channelId = String.valueOf(channel.getId());

        if (examination != null)
            examinationId = String.valueOf(examination.getId());

        return "Graph{" +
                "numberOfChannel=" + numberOfChannel +
                ", examinationEntity_id=" + examinationId +
                ", channel_id=" + channelId +
                '}';
    }
}

