package ru.gsa.biointerface.domain.entity;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Embeddable
public class ChannelID implements Serializable, Comparable<ChannelID> {
    @NotNull(message = "Id can't be null")
    private int number;

    @NotNull(message = "Id can't be null")
    private int examination_id;

    public ChannelID() {
    }

    public ChannelID(int id, int examination_id) {
        this.number = id;
        this.examination_id = examination_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getExamination_id() {
        return examination_id;
    }

    public void setExamination_id(int examination_id) {
        this.examination_id = examination_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelID that = (ChannelID) o;
        return number == that.number && examination_id == that.examination_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, examination_id);
    }

    @Override
    public int compareTo(ChannelID o) {
        int result = examination_id - o.examination_id;

        if (result == 0)
            result = number - o.number;

        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "number=" + number +
                ", examinationEntity_id=" + examination_id +
                '}';
    }
}
