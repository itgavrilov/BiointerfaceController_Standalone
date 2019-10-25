package channel;

import java.util.ArrayList;

public interface ChartReady {
    void update(ArrayList<Double> data);
    boolean getReady();
    void setReady(boolean Ready);
}
