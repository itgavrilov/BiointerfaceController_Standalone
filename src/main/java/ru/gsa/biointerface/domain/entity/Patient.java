package ru.gsa.biointerface.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "patient")
@Table(name = "patient")
public class Patient implements Serializable, Comparable<Patient> {
    @NotNull(message = "Id can't be null")
    @Min(value = 1, message = "Id can't be lass then 1")
    @Id
    private int id;

    @NotNull(message = "Second name can't be null")
    @NotBlank(message = "Second name can't be blank")
    @Size(min = 3, max = 20, message = "Second name should be have chars between 3-20")
    @Column(name = "second_name", nullable = false, length = 20)
    private String secondName;

    @NotNull(message = "First name can't be null")
    @NotBlank(message = "First name can't be blank")
    @Size(min = 3, max = 20, message = "First name should be have chars between 3-20")
    @Column(name = "first_name", nullable = false, length = 20)
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
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private Set<Examination> examinations;

    public Patient(int id, String secondName, String firstName, String patronymic, Calendar birthday, Icd icd, String comment) {
        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.comment = comment;
        this.icd = icd;
        examinations = new TreeSet<>();
    }

    public void addExamination(Examination examination) {
        examinations.add(examination);
        examination.setPatient(this);
    }

    public void removeExamination(Examination examination) {
        examinations.remove(examination);
        examination.setPatient(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient that = (Patient) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Patient o) {
        if (o == null || getClass() != o.getClass()) return -1;
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String icd_id = "-";

        if (icd != null) {
            icd_id = String.valueOf(icd.getId());
        }

        return "PatientRecord{" +
                "id=" + id +
                ", second_name='" + secondName + '\'' +
                ", first_name='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthday=" + formatter.format(birthday.getTime()) +
                ", icd_id=" + icd_id +
                '}';
    }
}
