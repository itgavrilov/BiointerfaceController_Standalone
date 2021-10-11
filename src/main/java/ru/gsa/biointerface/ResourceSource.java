package ru.gsa.biointerface;

import java.net.URL;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface ResourceSource {
    URL getResource(String name);
}
