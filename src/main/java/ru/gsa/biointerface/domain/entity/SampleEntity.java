package ru.gsa.biointerface.domain.entity;

import java.util.Objects;

public class SampleEntity implements Comparable<SampleEntity> {
    private int id;
    private int Examination_id;
    private int channel_id;
    private int value;

    public SampleEntity(int id, int examination_id, int channel_id, int value) {
        this.id = id;
        Examination_id = examination_id;
        this.channel_id = channel_id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamination_id() {
        return Examination_id;
    }

    public void setExamination_id(int examination_id) {
        Examination_id = examination_id;
    }

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
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
        SampleEntity that = (SampleEntity) o;
        return id == that.id && Examination_id == that.Examination_id && channel_id == that.channel_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Examination_id, channel_id);
    }

    @Override
    public int compareTo(SampleEntity o) {
        return id * Examination_id * channel_id - o.id * o.Examination_id * o.channel_id;
    }
}
