package ru.gsa.biointerfaceController_standalone.controllers.channel;

import ru.gsa.biointerfaceController_standalone.controllers.Content;

public interface ContentComparable extends Content, Comparable<ContentComparable> {
    int compareTo(ContentComparable o);
    int getIndex();
}
