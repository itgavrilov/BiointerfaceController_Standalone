package ru.gsa.biointerface.ui.window.channel;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelCheckBox extends CheckBox implements Comparable<ChannelCheckBox> {
    private final char index;

    public ChannelCheckBox(int index) {
        this.index = (char) index;

        this.setSelected(true);
        this.setPadding(new Insets(0, 0, 3, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelCheckBox that = (ChannelCheckBox) o;
        return index + 1 == that.index + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index + 1);
    }

    @Override
    public int compareTo(ChannelCheckBox o) {
        return (index + 1) - (o.index + 1);
    }
}
