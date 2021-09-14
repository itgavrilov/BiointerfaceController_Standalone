package ru.gsa.biointerface.persistence;

import java.sql.Connection;

public interface DB {
    Connection getConnection() throws DAOException;

    void disconnect();
}
