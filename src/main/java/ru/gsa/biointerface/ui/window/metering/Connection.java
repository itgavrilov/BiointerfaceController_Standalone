package ru.gsa.biointerface.ui.window.metering;

import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.host.HostException;
import ru.gsa.biointerface.services.ServiceChannel;
import ru.gsa.biointerface.services.ServicePatientRecord;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.host.cash.DataListener;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Connection {
    void setPatientRecord(PatientRecord patientRecord) throws HostException;

    int getAmountChannels();

    void setChannelInGraph(int numberOfChannel, Channel channel) throws HostException;

    void addListenerInCash(int numberOfChannel, DataListener listener) throws HostException;

    void connect() throws HostException;

    void disconnect() throws HostException;

    boolean isConnected();

    void transmissionStart() throws HostException;

    void transmissionStop() throws HostException;

    boolean isTransmission();

    void controllerReboot() throws HostException;

    void recordingStart() throws HostException;

    void recordingStop() throws HostException;

    boolean isRecording();

    String setCommentForExamination(String comment) throws HostException;
}
