package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.ChannelName;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface ChannelNameRepository {
    List<ChannelName> findAll() throws Exception;

    Optional<ChannelName> findById(Integer id) throws Exception;

    ChannelName save(ChannelName channelName) throws Exception;

    void delete(ChannelName channelName) throws Exception;

    boolean existsById(Integer id) throws Exception;
}
