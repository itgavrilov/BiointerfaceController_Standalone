package ru.gsa.biointerface.ui.window.ExaminationNew;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public interface ChannelUpdater {
    void update(List<Integer> data);

    void setCapacity(int capacity);

    boolean isReady();

    void setReady(boolean Ready);
}
