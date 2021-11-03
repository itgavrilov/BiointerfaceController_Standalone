package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameRepositoryImpl extends AbstractRepository<ChannelName, Integer> implements ChannelNameRepository {
    private static ChannelNameRepository repository;

    private ChannelNameRepositoryImpl() throws Exception {
        super();
    }

    public static ChannelNameRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new ChannelNameRepositoryImpl();
        }

        return repository;
    }

    @Override
    public ChannelName save(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        if (entity.getId() > 0 && existsById(entity.getId())) {
            update(entity);
        } else {
            insert(entity);
        }

        return entity;
    }
}
