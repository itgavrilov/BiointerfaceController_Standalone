package ru.gsa.biointerface.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class ExaminationEntity implements Comparable<ExaminationEntity> {
    private final int id;
    private final LocalDateTime dateTime;
    private PatientRecordEntity patientRecordEntity;
    private DeviceEntity device;
    private String comment;

    public ExaminationEntity(){
        this.id = -1;
        this.dateTime = LocalDateTime.now();
        this.patientRecordEntity = null;
        this.device = null;
        this.comment = null;
    }

    public ExaminationEntity(int id, LocalDateTime dateTime, PatientRecordEntity patientRecordEntity, DeviceEntity device, String comment) {
        if (dateTime == null)
            throw new NullPointerException("dateTime is null");

        this.id = id;
        this.dateTime = dateTime;
        this.patientRecordEntity = patientRecordEntity;
        this.device = device;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public PatientRecordEntity getPatientRecord() {
        return patientRecordEntity;
    }

    public void setPatientRecord(PatientRecordEntity patientRecordEntity) {
        this.patientRecordEntity = patientRecordEntity;
    }

    public DeviceEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceEntity device) {
        this.device = device;
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
        return dateTime.equals(that.dateTime) && patientRecordEntity.equals(that.patientRecordEntity) && device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, patientRecordEntity, device);
    }

    @Override
    public int compareTo(ExaminationEntity o) {
        return dateTime.compareTo(o.dateTime);
    }
}
