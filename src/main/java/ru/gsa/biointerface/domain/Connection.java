package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.ui.window.channel.Channel;

import java.util.Set;

public interface Connection {
    Device getDevice();

    void registerChannelGUIs(Set<Channel> channelGUIs) throws DomainException;

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
