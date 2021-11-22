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
import ru.gsa.biointerface.service.DeviceService;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DevicesController extends AbstractWindow {
    private final DeviceService deviceService;
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

    public DevicesController() throws Exception {
        deviceService = DeviceService.getInstance();
    }

    @Override
    public String getTitleWindow() {
        return ": Devices";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException(
                    "resourceSource or transitionGUI is null. First call setResourceAndTransition()"
            );

        ObservableList<Device> devices = FXCollections.observableArrayList();
        devices.addAll(deviceService.findAll());
        tableView.setItems(devices);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        amountChannelsCol.setCellValueFactory(new PropertyValueFactory<>("amountChannels"));
        transitionGUI.show();
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
        if (Objects.equals(device.getComment(), commentField.getText())) {
            String comment = device.getComment();
            device.setComment(commentField.getText());
            try {
                deviceService.save(device);
            } catch (Exception e) {
                device.setComment(comment);
                commentField.setText(comment);
                new AlertError("Error change comment for device: " + e.getMessage());
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Patients.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient records: " + e.getMessage());
        }
    }

    public void onDeleteButtonPush() {
        try {
            deviceService.delete(device);
            tableView.getItems().remove(device);
            commentField.setText("");
        } catch (Exception e) {
            new AlertError("Error delete device: " + e.getMessage());
        }
    }
}
