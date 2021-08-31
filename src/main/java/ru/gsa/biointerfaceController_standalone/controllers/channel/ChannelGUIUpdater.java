package ru.gsa.biointerfaceController_standalone.controllers.channel;

import java.util.ArrayList;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public interface ChannelGUIUpdater<T extends Number> {
    void update(ArrayList<T> data);

    void setCapacity(int capacity);

    boolean isReady();

    void setReady(boolean Ready);
}
