package ru.gsa.biointerface.ui.window.channel;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public interface ChannelUpdater {
    int getId();

    void update(List<Integer> data);

    void setCapacity(int capacity);

    boolean isReady();

    void setReady(boolean Ready);
}
