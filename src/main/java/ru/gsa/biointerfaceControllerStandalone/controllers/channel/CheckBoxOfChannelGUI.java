package ru.gsa.biointerfaceControllerStandalone.controllers.channel;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;

import java.util.Objects;

public class CheckBoxOfChannelGUI extends CheckBox implements Comparable<CheckBoxOfChannelGUI> {
    private final char index;

    public CheckBoxOfChannelGUI(char index, boolean selected) {
        super("Channel " + (index + 1));
        this.index = index;
        this.setSelected(selected);
        this.setPadding(new Insets(2, 5, 2, 5));
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckBoxOfChannelGUI that = (CheckBoxOfChannelGUI) o;
        return index + 1 == that.index + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index+1);
    }

    @Override
    public int compareTo(CheckBoxOfChannelGUI o) {
        return (index + 1) - (o.index + 1);
    }
}
