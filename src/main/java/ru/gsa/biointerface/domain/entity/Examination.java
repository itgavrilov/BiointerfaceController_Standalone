package ru.gsa.biointerface.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "examination")
@Table(name = "examination")
public class Examination implements Serializable, Comparable<Examination> {
    @NotNull(message = "Id can't be null")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Start time can't be null")
    @Past(message = "Start time should be in past")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date starttime;

    @NotNull(message = "Patient can't be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    @NotNull(message = "Device can't be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "device_id", referencedColumnName = "id", nullable = false)
    private Device device;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Channels can't be null")
    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Channel> channels;

    public Examination(Patient patient, Device device, String comment) {
        id  = -1;
        this.starttime = Timestamp.valueOf(LocalDateTime.now());
        this.comment = comment;
        this.device = device;
        this.patient = patient;
        channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        channel.setExamination(this);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
        channel.setExamination(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Examination entity = (Examination) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Examination o) {
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyy-MM-dd hh:mm:ss");

        return "Examination{" +
                "id=" + id +
                ", datetime=" + formatter.format(starttime) +
                ", patientRecord_id=" + patient.getId() +
                ", device_id=" + device.getId() +
                '}';
    }
}
