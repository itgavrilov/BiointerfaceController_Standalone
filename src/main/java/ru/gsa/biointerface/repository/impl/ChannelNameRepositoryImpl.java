package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameRepositoryImpl extends AbstractRepository<ChannelName, Long> implements ChannelNameRepository {
    private static ChannelNameRepository dao;

    private ChannelNameRepositoryImpl() throws Exception {
        super();
    }

    public static ChannelNameRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new ChannelNameRepositoryImpl();
        }

        return dao;
    }
}
