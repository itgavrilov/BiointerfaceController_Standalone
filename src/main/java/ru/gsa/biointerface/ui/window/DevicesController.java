package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.services.ServiceDevice;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.services.ServiceIcd;
import ru.gsa.biointerface.ui.UIException;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DevicesController extends AbstractWindow {
    private ServiceDevice serviceDevice;
    private Device device;

    @FXML
    private TableView<Device> tableView;
    @FXML
    private TableColumn<Device, Long> idCol;
    @FXML
    private TableColumn<Device, Integer> amountChannelsCol;
    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;


    @Override
    public String getTitleWindow() {
        return ": Devices";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        try {
            serviceDevice = ServiceDevice.getInstance();
            ObservableList<Device> devices = FXCollections.observableArrayList();
            devices.addAll(serviceDevice.getAll());
            tableView.setItems(devices);
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            amountChannelsCol.setCellValueFactory(new PropertyValueFactory<>("amountChannels"));
            amountChannelsCol.setStyle("-fx-alignment: center;");
            transitionGUI.show();
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
    }

    public void onMouseClickedTableView() {
        if (device != tableView.getFocusModel().getFocusedItem()) {
            device = tableView.getFocusModel().getFocusedItem();
            commentField.setText(device.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        String comment = device.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                device.setComment(commentField.getText());
                serviceDevice.update(device);
            } catch (ServiceException e) {
                device.setComment(comment);
                e.printStackTrace();
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            serviceDevice.delete(device);
            tableView.getItems().remove(device);
            commentField.setText("");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
