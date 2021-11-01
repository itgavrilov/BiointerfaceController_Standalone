package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "device")
@Table(name = "device")
public class Device implements Serializable, Comparable<Device> {
    @NotNull(message = "Id can't be null")
    @Min(value = 1, message = "Id can't be lass then 1")
    @Id
    private long id;

    @NotNull(message = "Amount channels can't be null")
    @Min(value = 1, message = "Amount channels can't be lass then 1")
    @Max(value = 8, message = "Amount channels can't be more than 8")
    @Column(nullable = false)
    private int amountChannels;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Examinations can't be null")
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private Set<Examination> examinations;

    public Device() {
    }

    public Device(long id, int amountChannels) {
        this.id = id;
        this.amountChannels = amountChannels;
        this.comment = null;
        this.examinations = new TreeSet<>();
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

    public Set<Examination> getExaminations() {
        return examinations;
    }

    public void setExaminations(Set<Examination> examinations) {
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
        int result = 0;

        if (id > o.id) {
            result = 1;
        } else if (id < o.id) {
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
