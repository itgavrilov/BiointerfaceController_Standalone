package ru.gsa.biointerface.ui.window.channel;

import java.util.ArrayList;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public interface ChannelUpdater<T extends Number> {
    int getId();

    void update(ArrayList<T> data);

    void setCapacity(int capacity);

    boolean isReady();

    void setReady(boolean Ready);
}
