package ru.gsa.biointerfaceController_standalone.uiLayer.window;

public interface WindowControllerWithProperty<Property> extends WindowController {

    WindowControllerWithProperty<Property> setProperty(Property property);
}
