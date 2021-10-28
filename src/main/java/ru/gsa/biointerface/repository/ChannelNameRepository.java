package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.database.AbstractRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameRepository extends AbstractRepository<ChannelName, Long> {
    private static ChannelNameRepository dao;

    private ChannelNameRepository() throws Exception {
        super();
    }

    public static ChannelNameRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new ChannelNameRepository();
        }

        return dao;
    }
}
