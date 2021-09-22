package ru.gsa.biointerface.domain.entity;

import java.util.Objects;

public class IcdEntity implements Comparable<IcdEntity> {

    private final int id;
    private final String ICD;
    private final int version;
    private String comment;

    public IcdEntity(int id, String ICD, int version, String comment) {
        this.id = id;
        this.ICD = ICD;
        this.version = version;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public String getICD() {
        return ICD;
    }

    public int getVersion() {
        return version;
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
}
