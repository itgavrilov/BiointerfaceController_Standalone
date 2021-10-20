package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "examination")
@Table(name = "examination")
public class ExaminationEntity implements Comparable<ExaminationEntity> {
    @Id
    @GeneratedValue(generator = "sqlite_examination")
    @TableGenerator(name = "sqlite_examination", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "examination",
            initialValue = 1, allocationSize=1)
    private long id = -1;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateTime = Timestamp.valueOf(LocalDateTime.now());

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patientRecord_id", referencedColumnName = "id")
    private PatientRecordEntity patientRecordEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private DeviceEntity deviceEntity;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "examinationEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GraphEntity> graphEntities;

    public ExaminationEntity() {
    }

    public ExaminationEntity(long id, Date dateTime, PatientRecordEntity patientRecordEntity, DeviceEntity deviceEntity, String comment, List<GraphEntity> graphEntities) {
        this.id = id;
        this.dateTime = dateTime;
        this.patientRecordEntity = patientRecordEntity;
        this.deviceEntity = deviceEntity;
        this.comment = comment;
        this.graphEntities = graphEntities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public PatientRecordEntity getPatientRecordEntity() {
        return patientRecordEntity;
    }

    public void setPatientRecordEntity(PatientRecordEntity patientRecordEntity) {
        this.patientRecordEntity = patientRecordEntity;
    }

    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<GraphEntity> getGraphEntities() {
        return graphEntities;
    }

    public void setGraphEntities(List<GraphEntity> graphEntities) {
        this.graphEntities = graphEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExaminationEntity entity = (ExaminationEntity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ExaminationEntity o) {
        return (int) (id - o.id);
    }

    @Override
    public String toString() {
        return "ExaminationEntity{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", patientRecord_id=" + patientRecordEntity.getId() +
                ", device_id=" + deviceEntity.getId() +
                '}';
    }
}
