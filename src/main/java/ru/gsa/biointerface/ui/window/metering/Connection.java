package ru.gsa.biointerface.ui.window.metering;

import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.domain.host.cash.DataListener;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Connection {
    void setPatientRecord(PatientRecord patientRecord) throws DomainException;

    int getAmountChannels();

    void setChannelInGraph(int numberOfChannel, Channel channel) throws DomainException;

    void addListenerInCash(int numberOfChannel, DataListener listener) throws DomainException;

    void connect() throws DomainException;

    void disconnect() throws DomainException;

    boolean isConnected();

    void transmissionStart() throws DomainException;

    void transmissionStop() throws DomainException;

    boolean isTransmission();

    void controllerReboot() throws DomainException;

    void recordingStart() throws DomainException;

    void recordingStop() throws DomainException;

    boolean isRecording();

    void setCommentForExamination(String comment) throws DomainException;
}
