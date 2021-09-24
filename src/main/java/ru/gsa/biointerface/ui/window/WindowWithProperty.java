package ru.gsa.biointerface.ui.window;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface WindowWithProperty<Property> extends Window {
    WindowWithProperty<Property> setProperty(Property property);
}
