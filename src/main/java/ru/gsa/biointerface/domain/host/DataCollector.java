package ru.gsa.biointerface.domain.host;

import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.Graph;

import java.util.List;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(Device devise);

    List<Graph> getGraphs();
}
