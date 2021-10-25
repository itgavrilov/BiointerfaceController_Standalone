package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameRepository extends AbstractRepository<ChannelName, Long> {
    protected static ChannelNameRepository dao;

    private ChannelNameRepository() throws NoConnectionException {
        super();
    }

    public static ChannelNameRepository getInstance() throws NoConnectionException {
        if (dao == null)
            dao = new ChannelNameRepository();

        return dao;
    }
}
