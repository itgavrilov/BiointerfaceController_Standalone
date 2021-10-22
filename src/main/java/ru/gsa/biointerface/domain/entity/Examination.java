package ru.gsa.biointerface.domain.entity;

import ru.gsa.biointerface.services.ServiceException;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "examination")
@Table(name = "examination")
public class Examination implements Serializable, Comparable<Examination> {
    @Id
    @GeneratedValue(generator = "sqlite_examination")
    @TableGenerator(name = "sqlite_examination", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "examination",
            initialValue = 1, allocationSize = 1)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patientRecord_id", referencedColumnName = "id")
    private PatientRecord patientRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private Device device;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Graph> graphs;

    @Transient
    private boolean recording = false;

    public Examination() {
    }

    public Examination(long id, Date startTime, PatientRecord patientRecord, Device device, String comment, List<Graph> graphs) {
        this.id = id;
        this.startTime = startTime;
        this.patientRecord = patientRecord;
        this.device = device;
        this.comment = comment;
        this.graphs = graphs;
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
    public LocalDateTime getStartTimeInLocalDateTime() {
        return startTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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

    public List<Graph> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<Graph> graphs) {
        this.graphs = graphs;
    }

    public void setChannelInGraph(int numberOfChannel, Channel channel) throws ServiceException {
        if (numberOfChannel < 0)
            throw new IllegalArgumentException("numberOfChannel < 0");
        if (graphs == null)
            throw new ServiceException("Graphs is null");
        if (numberOfChannel >= graphs.size())
            throw new IllegalArgumentException("I > amount graphs");

        graphs.get(numberOfChannel).setChannel(channel);
    }

    public void setSampleInGraph(int numberOfChannel, int value) throws ServiceException {
        if (numberOfChannel >= graphs.size() || numberOfChannel < 0)
            throw new ServiceException("NumberOfChannel < 0 or > amount graphs");

        Graph graph = graphs.get(numberOfChannel);
        List<Sample> entities = graph.getSamples();
        Sample sample =
                new Sample(
                        entities.size(),
                        graph,
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
