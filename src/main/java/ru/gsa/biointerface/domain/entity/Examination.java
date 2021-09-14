package ru.gsa.biointerface.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Examination implements Comparable<Examination> {
    private final int id;
    private final LocalDateTime dateTime;
    private PatientRecord patientRecord;
    private Device device;
    private String comment;


    public Examination(int id, LocalDateTime dateTime, PatientRecord patientRecord, Device device, String comment) {
        if (dateTime == null)
            throw new NullPointerException("dateTime is null");

        this.id = id;
        this.dateTime = dateTime;
        this.patientRecord = patientRecord;
        this.device = device;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    public void setPatientRecord(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        this.patientRecord = patientRecord;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        if (device == null)
            throw new NullPointerException("device is null");

        this.device = device;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null)
            throw new NullPointerException("comment is null");

        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Examination that = (Examination) o;
        return dateTime.equals(that.dateTime) && patientRecord.equals(that.patientRecord) && device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, patientRecord, device);
    }

    @Override
    public int compareTo(Examination o) {
        return dateTime.compareTo(o.dateTime);
    }
}
