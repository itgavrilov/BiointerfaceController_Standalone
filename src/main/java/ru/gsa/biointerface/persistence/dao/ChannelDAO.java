package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelDAO extends AbstractDAO<Channel, Long> {
    protected static ChannelDAO dao;

    private ChannelDAO() throws PersistenceException {
        super();
    }

    public static ChannelDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new ChannelDAO();

        return dao;
    }
}
