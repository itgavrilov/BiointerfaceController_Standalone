package ru.gsa.biointerface.domain.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Entity(name = "icd")
@Table(name = "icd")
public class IcdEntity implements Comparable<IcdEntity> {
    @Id
    @GeneratedValue(generator = "sqlite_icd")
    @TableGenerator(name = "sqlite_icd", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "icd",
            initialValue = 1, allocationSize=1)
    private int id = -1;

    @Column(nullable = false, length = 35)
    private String icd;

    @Column(nullable = false)
    private int version;

    @Column(length = 400)
    private String comment;

    @OneToMany(mappedBy = "icdEntity", fetch = FetchType.LAZY)
    private List<PatientRecordEntity> patientRecordEntities;

    public IcdEntity() {
    }

    public IcdEntity(int id, String icd, int version, String comment) {
        this.id = id;
        this.icd = icd;
        this.version = version;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcd() {
        return icd;
    }

    public void setIcd(String icd) {
        this.icd = icd;
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

    public List<PatientRecordEntity> getPatientRecordEntities() {
        return patientRecordEntities;
    }

    public void setPatientRecordEntities(List<PatientRecordEntity> patientRecordEntities) {
        this.patientRecordEntities = patientRecordEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IcdEntity icdEntity = (IcdEntity) o;
        return id == icdEntity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(IcdEntity o) {
        return id - o.id;
    }

    @Override
    public String toString() {
        return "IcdEntity{" +
                "ICD='" + icd + '\'' +
                ", version=" + version +
                ", id=" + id +
                '}';
    }
}
