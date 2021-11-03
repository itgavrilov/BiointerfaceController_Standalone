package ru.gsa.biointerface.host;

import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.host.cash.DataListener;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface HostHandler {
    void setPatientRecord(PatientRecord patientRecord);

    int getAmountChannels();

    Device getDevice();

    void setNameInChannel(int numberOfChannel, ChannelName channelName);

    void setListenerInChannel(int numberOfChannel, DataListener listener);

    void connect() throws Exception;

    void disconnect() throws Exception;

    boolean isConnected();

    void transmissionStart() throws Exception;

    void transmissionStop() throws Exception;

    boolean isTransmission();

    void controllerReboot() throws Exception;

    void recordingStart() throws Exception;

    void recordingStop() throws Exception;

    boolean isRecording();

    void setCommentForExamination(String comment) throws Exception;
}
