package ru.gsa.biointerface.persistence;

import org.hibernate.SessionFactory;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface DB {
    SessionFactory getSessionFactory();
}
