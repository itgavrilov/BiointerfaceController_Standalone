package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.repository.SampleRepository;
import ru.gsa.biointerface.repository.impl.SampleRepositoryImpl;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 03/11/2021
 */
public class SampleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleService.class);
    private static SampleService instance = null;
    private final SampleRepository repository;

    private SampleService() throws Exception {
        this.repository = SampleRepositoryImpl.getInstance();
    }

    public static SampleService getInstance() throws Exception {
        if (instance == null) {
            instance = new SampleService();
        }

        return instance;
    }

    public List<Sample> findAllByChannel(Channel channel) throws Exception {
        List<Sample> entities = repository.findAllByChannel(channel);

        if (entities.size() > 0) {
            LOGGER.info("Get all samples by channel from database");
        } else {
            LOGGER.info("Samples by channel is not found in database");
        }

        return entities;
    }

    public void transactionOpen() throws Exception {
        repository.transactionOpen();
        LOGGER.info("Transaction is open");
    }

    public void transactionClose() throws Exception {
        repository.transactionClose();
        LOGGER.info("Transaction is close");
    }

    public boolean transactionIsOpen() {
        return repository.transactionIsOpen();
    }

    public void setSampleInChannel(Channel channel, int value) throws Exception {
        if (!transactionIsOpen())
            throw new ServiceException("Recording not started");
        if (channel == null)
            throw new NullPointerException("Channel is null");

        List<Sample> samples = channel.getSamples();
        Sample sample =
                new Sample(
                        samples.size(),
                        channel,
                        value
                );
        sample = repository.insert(sample);
        samples.add(samples.size(), sample);
    }
}
