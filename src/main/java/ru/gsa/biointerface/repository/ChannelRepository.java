package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.Examination;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface ChannelRepository {
    List<Channel> findAll() throws Exception;

    List<Channel> findAllByExamination(Examination examination) throws Exception;

    Optional<Channel> findById(ChannelID id) throws Exception;

    Channel save(Channel channel) throws Exception;

    void delete(Channel channel) throws Exception;

    boolean existsById(ChannelID id) throws Exception;
}
