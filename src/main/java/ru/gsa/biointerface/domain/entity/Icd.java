package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "icd")
@Table(name = "icd")
public class Icd implements Serializable, Comparable<Icd> {
    @Id
    @GeneratedValue()
//    @GeneratedValue(generator = "sqlite_icd")
//    @TableGenerator(name = "sqlite_icd", table = "sqlite_sequence",
//            pkColumnName = "name", valueColumnName = "seq",
//            pkColumnValue = "icd",
//            initialValue = 1, allocationSize = 1)
    private long id;

    @Column(nullable = false, length = 35)
    private String name;

    @Column(nullable = false)
    private int version;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "icd", fetch = FetchType.LAZY)
    private List<PatientRecord> patientRecords;

    public Icd() {
    }

    public Icd(long id, String name, int version, String comment) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public List<PatientRecord> getPatientRecords() {
        return patientRecords;
    }

    public void setPatientRecords(List<PatientRecord> patientRecords) {
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

        if(id > o.id) {
            result = 1;
        } else if(id < o.id){
            result = -1;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Icd{" +
                "ICD='" + name + '\'' +
                ", version=" + version +
                ", id=" + id +
                '}';
    }
}
