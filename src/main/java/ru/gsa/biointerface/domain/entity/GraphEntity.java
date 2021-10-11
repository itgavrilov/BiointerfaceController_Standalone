package ru.gsa.biointerface.domain.entity;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class GraphEntity implements Comparable<GraphEntity> {
    private int numberOfChannel;
    private ExaminationEntity examinationEntity;
    private ChannelEntity channelEntity;

    public GraphEntity(int numberOfChannel, ExaminationEntity examinationEntity, ChannelEntity channelEntity) {
        this.numberOfChannel = numberOfChannel;
        this.examinationEntity = examinationEntity;
        this.channelEntity = channelEntity;
    }

    public int getNumberOfChannel() {
        return numberOfChannel;
    }

    public void setNumberOfChannel(int numberOfChannel) {
        this.numberOfChannel = numberOfChannel;
    }

    public ExaminationEntity getExaminationEntity() {
        return examinationEntity;
    }

    public void setExaminationEntity(ExaminationEntity examinationEntity) {
        this.examinationEntity = examinationEntity;
    }

    public ChannelEntity getChannelEntity() {
        return channelEntity;
    }

    public void setChannelEntity(ChannelEntity channelEntity) {
        this.channelEntity = channelEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphEntity that = (GraphEntity) o;
        return numberOfChannel == that.numberOfChannel && Objects.equals(examinationEntity, that.examinationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfChannel, examinationEntity);
    }

    @Override
    public int compareTo(GraphEntity o) {
        int result = examinationEntity.compareTo(o.examinationEntity);

        if (result == 0)
            result = numberOfChannel - o.numberOfChannel;

        return result;
    }
}
