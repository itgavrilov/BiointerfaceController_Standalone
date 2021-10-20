package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "device")
@Table(name = "device")
public class DeviceEntity implements Comparable<DeviceEntity> {
    @Id
    private int id = -1;

    @Column(nullable = false)
    private int amountChannels;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "deviceEntity", fetch = FetchType.LAZY)
    private List<ExaminationEntity> examinationEntities;

    public DeviceEntity() {
    }

    public DeviceEntity(int id, int amountChannels, String comment) {
        this.id = id;
        this.amountChannels = amountChannels;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmountChannels() {
        return amountChannels;
    }

    public void setAmountChannels(int amountChannels) {
        this.amountChannels = amountChannels;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ExaminationEntity> getExaminationEntities() {
        return examinationEntities;
    }

    public void setExaminationEntities(List<ExaminationEntity> examinationEntities) {
        this.examinationEntities = examinationEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceEntity device = (DeviceEntity) o;
        return id == device.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(DeviceEntity o) {
        return id - o.id;
    }

    @Override
    public String toString() {
        return "DeviceEntity{" +
                "id=" + id +
                ", amountChannels=" + amountChannels +
                '}';
    }
}
