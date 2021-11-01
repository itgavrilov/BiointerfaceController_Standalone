package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "examination")
@Table(name = "examination")
public class Examination implements Serializable, Comparable<Examination> {
    @NotNull(message = "Id can't be null")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Start time can't be null")
    @Past(message = "Start time should be in past")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime;

    @NotNull(message = "Patient record can't be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patientRecord_id", referencedColumnName = "id", nullable = false)
    private PatientRecord patientRecord;

    @NotNull(message = "Device can't be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", referencedColumnName = "id", nullable = false)
    private Device device;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Channels can't be null")
    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Channel> channels;

    @Transient
    private boolean recording = false;

    public Examination() {
    }

    public Examination(PatientRecord patientRecord, Device device, String comment) {
        this.id = -1;
        this.startTime = Timestamp.valueOf(LocalDateTime.now());
        this.comment = comment;
        this.channels = new ArrayList<>();
        patientRecord.addExamination(this);
        device.addExamination(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTimeInLocalDateTime() {
        return startTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    public void setPatientRecord(PatientRecord patientRecord) {
        this.patientRecord = patientRecord;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public void setNameInChannel(int number, ChannelName channelName) {
        if (channels == null)
            throw new NullPointerException("Channels is null");
        if (number < 0)
            throw new IllegalArgumentException("number < 0");
        if (number >= channels.size())
            throw new IllegalArgumentException("I > amount channels");

        if(channels.get(number).getChannelName() != null){
            channels.get(number).getChannelName().deleteChannel(channels.get(number));
        }

        if(channelName != null) {
            channelName.addChannel(channels.get(number));
        }
    }

    public void setSampleInChannel(int numberOfChannel, int value) {
        if (numberOfChannel >= channels.size() || numberOfChannel < 0)
            throw new IllegalArgumentException("NumberOfChannel < 0 or > amount channels");

        Channel channel = channels.get(numberOfChannel);
        List<Sample> entities = channel.getSamples();
        Sample sample =
                new Sample(
                        entities.size(),
                        channel,
                        value
                );
        entities.add(entities.size(), sample);
    }

    public boolean isRecording() {
        return recording;
    }

    public void recordingStart() {
        this.recording = true;
    }

    public void recordingStop() {
        this.recording = false;
    }


    public void addChannel(Channel channel) {
        if (channel == null)
            throw new NullPointerException("Channel is null");

        channel.setExamination(this);

        if (!channels.contains(channel)) {
            channels.add(channel);
        }
    }

    public void deleteChannel(Channel channel) {
        if (channel == null)
            throw new NullPointerException("Channel is null");

        channels.remove(channel);

        if (channel.getExamination().equals(this)) {
            channel.setExamination(null);
        }
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
        String startTime = this.getStartTimeInLocalDateTime().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        );

        return "Examination{" +
                "id=" + id +
                ", dateTime=" + startTime +
                ", patientRecord_id=" + patientRecord.getId() +
                ", device_id=" + device.getId() +
                '}';
    }
}
