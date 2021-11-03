package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "icd")
@Table(name = "icd")
public class Icd implements Serializable, Comparable<Icd> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    @Column(nullable = false, length = 35)
    private String name;

    @NotNull(message = "Version can't be null")
    @Min(value = 10, message = "Version can't be lass then 10")
    @Max(value = 99, message = "Version can't be more than 99")
    @Column(nullable = false)
    private int version;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Patient records can't be null")
    @OneToMany(mappedBy = "icd", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PatientRecord> patientRecords;

    public Icd() {
    }

    public Icd(String name, int version, String comment) {
        this.id = -1;
        this.name = name;
        this.version = version;
        this.comment = comment;
        this.patientRecords = new TreeSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<PatientRecord> getPatientRecords() {
        return patientRecords;
    }

    public void setPatientRecords(Set<PatientRecord> patientRecords) {
        this.patientRecords = patientRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Icd icd = (Icd) o;
        return id == icd.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Icd o) {
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
        return "Icd{" +
                "id=" + id +
                ", ICD='" + name + '\'' +
                ", version=" + version +
                '}';
    }
}
