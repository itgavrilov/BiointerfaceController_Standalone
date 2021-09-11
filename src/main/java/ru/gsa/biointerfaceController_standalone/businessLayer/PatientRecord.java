package ru.gsa.biointerfaceController_standalone.businessLayer;

import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;
import ru.gsa.biointerfaceController_standalone.daoLayer.dao.IcdDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PatientRecord implements Comparable<PatientRecord> {
    private final int id;
    private final String secondName;
    private final String firstName;
    private final String middleName;
    private final LocalDate birthday;
    private Icd icd;
    private String comment;

    public PatientRecord(int id, String secondName, String firstName, String middleName, LocalDate birthday, Icd icd, String comment) {
        if (id == 0)
            throw new NullPointerException("id is null");
        if (secondName == null)
            throw new NullPointerException("secondName is null");
        if (firstName == null)
            throw new NullPointerException("firstName is null");
        if (middleName == null)
            throw new NullPointerException("middleName is null");
        if (birthday == null)
            throw new NullPointerException("birthday is null");

        this.id = id;
        this.secondName = secondName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthday = birthday;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.birthday.format(dateFormatter);
        this.icd = icd;
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

    public Icd getIcd() {
        return icd;
    }

    public void setIcd(Icd icd) {
        this.icd = icd;
    }

    public void setIcdId(int icd_id) throws BusinessException {
        try {
            this.icd = IcdDAO.getInstance().getById(icd_id);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new BusinessException("icd getById is error", e);
        }
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
        PatientRecord that = (PatientRecord) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(PatientRecord o) {
        return secondName.compareTo(o.secondName);
    }
}
