package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "patientRecord")
@Table(name = "patientRecord")
public class PatientRecord implements Serializable, Comparable<PatientRecord> {
    @NotNull(message = "Id can't be null")
    @Min(value = 1, message = "Id can't be lass then 1")
    @Id
    private int id;

    @NotNull(message = "Second name can't be null")
    @NotBlank(message = "Second name can't be blank")
    @Size(min = 3, max = 20, message = "Second name should be have chars between 3-20")
    @Column(nullable = false, length = 20)
    private String secondName;

    @NotNull(message = "First name can't be null")
    @NotBlank(message = "First name can't be blank")
    @Size(min = 3, max = 20, message = "First name should be have chars between 3-20")
    @Column(nullable = false, length = 20)
    private String firstName;

    @NotNull(message = "Patronymic can't be null")
    @NotBlank(message = "Patronymic can't be blank")
    @Size(min = 3, max = 20, message = "Patronymic should be have chars between 3-20")
    @Column(nullable = false, length = 20)
    private String patronymic;

    @NotNull(message = "Birthday can't be null")
    @Past(message = "Birthday should be in past")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Calendar birthday;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "icd_id", referencedColumnName = "id")
    private Icd icd;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Examinations can't be null")
    @OneToMany(mappedBy = "patientRecord", fetch = FetchType.LAZY)
    private Set<Examination> examinations;

    public PatientRecord() {
    }

    public PatientRecord(int id, String secondName, String firstName, String patronymic, Calendar birthday, Icd icd, String comment) {
        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.comment = comment;
        this.examinations = new TreeSet<>();
        this.icd = icd;
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

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String middleName) {
        this.patronymic = middleName;
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

    public Set<Examination> getExaminations() {
        return examinations;
    }

    public void setExaminations(Set<Examination> examinations) {
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
                ", patronymic='" + patronymic + '\'' +
                ", birthday=" + birthday +
                ", icd_id=" + icd_id +
                '}';
    }
}
