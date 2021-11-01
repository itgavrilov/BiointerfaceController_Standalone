package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Examination;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface ChannelRepository {
    List<Channel> getAllByExamination(Examination examination) throws Exception;
}
