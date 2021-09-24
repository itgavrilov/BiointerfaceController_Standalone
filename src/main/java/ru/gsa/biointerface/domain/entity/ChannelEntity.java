package ru.gsa.biointerface.domain.entity;


import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelEntity implements Comparable<ChannelEntity> {
    private int id;
    private ExaminationEntity examinationEntity;
    private String name;

    public ChannelEntity(int id, ExaminationEntity examinationEntity, String name) {
        this.id = id;
        this.examinationEntity = examinationEntity;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public ExaminationEntity getExaminationEntity() {
        return examinationEntity;
    }

    public void setExaminationEntity(ExaminationEntity examinationEntity) {
        this.examinationEntity = examinationEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelEntity that = (ChannelEntity) o;
        return id == that.id && Objects.equals(examinationEntity, that.examinationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examinationEntity);
    }

    @Override
    public int compareTo(ChannelEntity o) {
        int result;
        if (examinationEntity.getId() == o.examinationEntity.getId())
            result = id - o.id;
        else
            result = examinationEntity.getId() - o.examinationEntity.getId();

        return result;
    }
}
