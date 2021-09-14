package ru.gsa.biointerface.ui.window.channel;

import javafx.fxml.Initializable;

public interface ContentComparable extends Initializable, Comparable<ContentComparable> {
    void resizeWindow(double height, double width);

    int getId();

    int compareTo(ContentComparable o);
}
