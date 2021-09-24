package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.ui.window.ExaminationNew.ChannelController;

import java.util.List;

public interface Connection {
    Device getDevice();

    void registerChannelGUIs(List<ChannelController> channelControllerGUIS) throws DomainException;

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
