package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "patientRecord")
@Table(name = "patientRecord")
public class PatientRecord implements Serializable, Comparable<PatientRecord> {
    @Id
    private long id;

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
    private Icd icd;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "patientRecord", fetch = FetchType.LAZY)
    private List<Examination> examinations;

    public PatientRecord() {
    }

    public PatientRecord(long id, String secondName, String firstName, String middleName, Calendar birthday, Icd icd, String comment, List<Examination> examinations) {
        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.icd = icd;
        this.comment = comment;
        this.examinations = examinations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public LocalDate getBirthdayInLocalDate() {
        return LocalDateTime.ofInstant(
                        birthday.toInstant(),
                        ZoneId.systemDefault()
                )
                .toLocalDate();
    }

    public Icd getIcd() {
        return icd;
    }

    public void setIcd(Icd icd) {
        this.icd = icd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Examination> getExaminations() {
        return examinations;
    }

    public void setExaminations(List<Examination> examinations) {
        this.examinations = examinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientRecord that = (PatientRecord) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(PatientRecord o) {
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
        String icd_id = "-";
        String birthday = this.getBirthdayInLocalDate().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        );

        if (icd != null)
            icd_id = String.valueOf(icd.getId());

        return "PatientRecord{" +
                "id=" + id +
                ", secondName='" + secondName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", birthday=" + birthday +
                ", icd_id=" + icd_id +
                '}';
    }
}
