package ru.gsa.biointerface.util;

import java.time.LocalDate;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.Graph;
import ru.gsa.biointerface.domain.PatientRecord;

public class EntityUtil {
    public static Examination getExamination() {
        var patientRecord = new PatientRecord(1,
                "G",
                "S",
                "A",
                LocalDate.now(),
                null,
                "test");
        var device = new Device(1, 1, null);

        return new Examination( patientRecord, device, getGraphList(),"test");
    }

    public static List<Graph> getGraphList() {
        var graphList = new LinkedList<Graph>();
        var graph = new Graph(0);

        graph.setNewSamples(getSampleList());
        graphList.add(graph);

        return graphList;
    }

    public static Deque<Integer> getSampleList() {
        var sampleEntities = new LinkedList<Integer>();

        for (int i = 10; i > 0; i--) {
            sampleEntities.add(i);
        }

        return  sampleEntities;
    }
}
