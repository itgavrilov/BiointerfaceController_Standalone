package ru.gsa.biointerface.ui.window;

public interface WindowWithProperty<Property> extends Window {

    WindowWithProperty<Property> setProperty(Property property);
}
