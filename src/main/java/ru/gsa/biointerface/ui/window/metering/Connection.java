package ru.gsa.biointerface.ui.window.metering;

import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Graph;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.domain.host.dataCash.Cash;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Connection {
    void setPatientRecord(PatientRecord patientRecord) throws DomainException;

    Device getDevice();

    Graph getGraph(int i) throws DomainException;

    Cash getCash(int i) throws DomainException;

    boolean isConnected();

    void disconnect() throws DomainException;

    void transmissionStart() throws DomainException;

    void transmissionStop() throws DomainException;

    boolean isTransmission();

    void controllerReboot() throws DomainException;

    void recordingStart() throws DomainException;

    void recordingStop() throws DomainException;

    boolean isRecording();

    void changeCommentOnExamination(String comment) throws DomainException;
}
