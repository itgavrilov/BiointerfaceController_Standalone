package ru.gsa.biointerface.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationEntity implements Comparable<ExaminationEntity> {
    private int id;
    private LocalDateTime dateTime;
    private PatientRecordEntity patientRecordEntity;
    private DeviceEntity deviceEntity;
    private String comment;

    public ExaminationEntity() {
        this.id = -1;
        this.dateTime = LocalDateTime.now();
        this.patientRecordEntity = null;
        this.deviceEntity = null;
        this.comment = null;
    }

    public ExaminationEntity(int id, LocalDateTime dateTime, PatientRecordEntity patientRecordEntity, DeviceEntity device, String comment) {
        if (dateTime == null)
            throw new NullPointerException("dateTime is null");

        this.id = id;
        this.dateTime = dateTime;
        this.patientRecordEntity = patientRecordEntity;
        this.deviceEntity = device;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setPatientRecordEntity(PatientRecordEntity patientRecordEntity) {
        this.patientRecordEntity = patientRecordEntity;
    }

    public PatientRecordEntity getPatientRecord() {
        return patientRecordEntity;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExaminationEntity that = (ExaminationEntity) o;
        return dateTime.equals(that.dateTime) && patientRecordEntity.equals(that.patientRecordEntity) && deviceEntity.equals(that.deviceEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, patientRecordEntity, deviceEntity);
    }

    @Override
    public int compareTo(ExaminationEntity o) {
        return dateTime.compareTo(o.dateTime);
    }
}
