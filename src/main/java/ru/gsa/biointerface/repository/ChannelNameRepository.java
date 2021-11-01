package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.ChannelName;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface ChannelNameRepository {
    void insert(ChannelName channelName) throws Exception;

    ChannelName getById(Long id) throws Exception;

    void update(ChannelName channelName) throws Exception;

    void delete(ChannelName channelName) throws Exception;

    List<ChannelName> getAll() throws Exception;
}
