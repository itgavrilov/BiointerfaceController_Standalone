package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.ui.window.metering.GraphForMeteringController;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Connection {
    Device getDevice();

    void registerChannelGUIs(List<GraphForMeteringController> channelControllerGUIS) throws DomainException;

    void setCapacity(int capacity) throws DomainException;

    boolean isConnected();

    void disconnect();

    void controllerTransmissionStart() throws DomainException;

    void controllerTransmissionStop() throws DomainException;

    boolean isControllerTransmission();

    void controllerReboot();

    void recordingStart(String comment);

    void recordingStop();

    boolean isRecording();

    void changeCommentOnExamination(String comment);
}
