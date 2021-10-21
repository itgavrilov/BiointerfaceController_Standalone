package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "patientRecord")
@Table(name = "patientRecord")
public class PatientRecordEntity implements Serializable, Comparable<PatientRecordEntity> {
    @Id
    private int id = -1;

    @Column(nullable = false, length = 35)
    private String secondName;

    @Column(nullable = false, length = 35)
    private String firstName;

    @Column(nullable = false, length = 35)
    private String middleName;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Calendar birthday;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "icd_id", referencedColumnName = "id")
    private IcdEntity icdEntity;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "patientRecordEntity", fetch = FetchType.LAZY)
    private List<ExaminationEntity> examinationEntities;

    public PatientRecordEntity() {
    }

    public PatientRecordEntity(int id, String secondName, String firstName, String middleName, Calendar birthday, IcdEntity icdEntity, String comment) {
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

    public void setId(int id) {
        this.id = id;
    }

    public String getSecondName() {
        return secondName;
    }


    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public IcdEntity getIcdEntity() {
        return icdEntity;
    }

    public void setIcdEntity(IcdEntity icdEntity) {
        this.icdEntity = icdEntity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ExaminationEntity> getExaminationEntities() {
        return examinationEntities;
    }

    public void setExaminationEntities(List<ExaminationEntity> examinationEntities) {
        this.examinationEntities = examinationEntities;
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

    @Override
    public String toString() {
        return "PatientRecordEntity{" +
                "id=" + id +
                ", secondName='" + secondName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", birthday=" + birthday +
                ", icd_id=" + icdEntity.getIcd() +
                '}';
    }
}
