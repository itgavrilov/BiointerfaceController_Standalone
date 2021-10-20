package ru.gsa.biointerface.ui.window.metering;

import ru.gsa.biointerface.domain.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Connection {
    void setPatientRecord(PatientRecord patientRecord) throws DomainException;

    Device getDevice();

    void setChannelInGraph(int numberOfChannel, Channel channel) throws DomainException;

    void addListenerInCash(int numberOfChannel, DataListener listener) throws DomainException;

    boolean isConnected();

    void disconnect() throws DomainException;

    void transmissionStart() throws DomainException;

    void transmissionStop() throws DomainException;

    boolean isTransmission();

    void controllerReboot() throws DomainException;

    void recordingStart() throws DomainException;

    void recordingStop() throws DomainException;

    boolean isRecording();

    void setCommentForExamination(String comment) throws DomainException;
}
