package ru.gsa.biointerfaceController_standalone.uiLayer.channel;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;

import java.util.Objects;

public class CheckBoxOfChannel extends CheckBox implements Comparable<CheckBoxOfChannel> {
    private final char index;

    public CheckBoxOfChannel(char index) {
        super("Channel " + (index + 1));
        this.index = index;
        this.setSelected(true);
        this.setPadding(new Insets(2, 5, 2, 5));
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckBoxOfChannel that = (CheckBoxOfChannel) o;
        return index + 1 == that.index + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index + 1);
    }

    @Override
    public int compareTo(CheckBoxOfChannel o) {
        return (index + 1) - (o.index + 1);
    }
}
