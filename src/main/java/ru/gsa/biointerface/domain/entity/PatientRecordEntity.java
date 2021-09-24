package ru.gsa.biointerface.domain.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordEntity implements Comparable<PatientRecordEntity> {
    private final int id;
    private final String secondName;
    private final String firstName;
    private final String middleName;
    private final LocalDate birthday;
    private IcdEntity icdEntity;
    private String comment;

    public PatientRecordEntity(int id, String secondName, String firstName, String middleName, LocalDate birthday, IcdEntity icdEntity, String comment) {
        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.icdEntity = icdEntity;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public IcdEntity getIcdEntity() {
        return icdEntity;
    }

    public void setIcd(IcdEntity icdEntity) {
        this.icdEntity = icdEntity;
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
        PatientRecordEntity that = (PatientRecordEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(PatientRecordEntity o) {
        return id - o.id;
    }
}
