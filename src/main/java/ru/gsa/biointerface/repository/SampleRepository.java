package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface SampleRepository {
    List<Sample> getAllByChannel(Channel channel) throws Exception;
}
