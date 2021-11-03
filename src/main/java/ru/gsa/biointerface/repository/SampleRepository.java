package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.domain.entity.SampleID;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface SampleRepository {
    List<Sample> findAllByChannel(Channel channel) throws Exception;

    Optional<Sample> findById(SampleID id) throws Exception;

    Sample insert(Sample sample) throws Exception;

    void transactionOpen() throws Exception;

    void transactionClose() throws Exception;

    boolean transactionIsOpen();

    boolean existsById(SampleID id) throws Exception;
}
