package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "device")
@Table(name = "device")
public class Device implements Serializable, Comparable<Device> {
    @Id
    private long id;

    @Column(nullable = false)
    private int amountChannels;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<Examination> examinations;

    public Device() {
    }

    public Device(int id, int amountChannels, String comment) {
        this.id = id;
        this.amountChannels = amountChannels;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public List<Examination> getExaminations() {
        return examinations;
    }

    public void setExaminations(List<Examination> examinations) {
        this.examinations = examinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return id == device.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Device o) {
        int result =  0;

        if(id > o.id) {
            result = 1;
        } else if(id < o.id){
            result = -1;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", amountChannels=" + amountChannels +
                '}';
    }
}
