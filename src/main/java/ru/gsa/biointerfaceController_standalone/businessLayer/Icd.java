package ru.gsa.biointerfaceController_standalone.businessLayer;

import javafx.util.StringConverter;

import java.util.Objects;

public class Icd implements Comparable<Icd> {
    public static StringConverter<Icd> converter = new StringConverter<>() {
        @Override
        public String toString(Icd icd) {
            String str = "";
            if (icd != null)
                str = icd.toString();
            return str;
        }

        @Override
        public Icd fromString(String string) {
            return null;
        }
    };
    private final int id;
    private final String ICD;
    private final int version;
    private String comment = "";

    public Icd(int id, String ICD, int version, String comment) {
        if (ICD == null)
            throw new NullPointerException("ICD is null");
        if (version <= 0)
            throw new IllegalArgumentException("version is null");

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
        Icd icd = (Icd) o;
        return id == icd.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Icd o) {
        return id - o.id;
    }

    @Override
    public String toString() {
        return getICD() + " (ICD-" + getVersion() + ")";
    }
}
