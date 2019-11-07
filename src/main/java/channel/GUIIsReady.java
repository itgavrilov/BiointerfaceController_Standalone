package channel;

import java.util.ArrayList;

public interface GUIIsReady<T extends Number> {
    void update(ArrayList<T> data);
    boolean getReady();
    void setReady(boolean Ready);
}
