package ru.gsa.biointerface.ui.window.graph;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class CheckBoxOfGraph extends CheckBox implements Comparable<CheckBoxOfGraph> {
    private final char index;

    public CheckBoxOfGraph(int index) {
        this.index = (char) index;

        this.setSelected(true);
        this.setPadding(new Insets(0, 0, 3, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckBoxOfGraph that = (CheckBoxOfGraph) o;
        return index + 1 == that.index + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index + 1);
    }

    @Override
    public int compareTo(CheckBoxOfGraph o) {
        return (index + 1) - (o.index + 1);
    }
}
