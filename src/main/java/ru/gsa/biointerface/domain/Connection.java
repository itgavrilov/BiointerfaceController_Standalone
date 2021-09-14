package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.ui.window.channel.Channel;

import java.util.Set;

public interface Connection {
    Device getDevice();

    void setSamplesOfChannels(Set<Channel> channelGUIs);

    void setCapacity(int capacity);

    boolean isConnected();

    void disconnect();

    void controllerTransmissionStart();

    void controllerTransmissionStop();

    boolean isControllerTransmission();

    void controllerReboot();

    void recordingStart(String comment);

    void recordingStop();

    boolean isRecording();

    void changeCommentOnExamination(String comment);
}
