package ru.gsa.biointerfaceController_standalone.daoLayer;

import java.sql.Connection;

public interface DB {
    Connection getConnection() throws DAOException;
}
