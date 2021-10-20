package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.ui.UIException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DevicesController extends AbstractWindow {
    private Device deviceSelected;

    @FXML
    private TableView<Device> tableView;
    @FXML
    private TableColumn<Device, Integer> idCol;
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

        tableView.getItems().clear();
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        amountChannelsCol.setCellValueFactory(new PropertyValueFactory<>("amountChannels"));
        amountChannelsCol.setStyle("-fx-alignment: center;");

        ObservableList<Device> list = FXCollections.observableArrayList();
        try {
            list.addAll(Device.getAll());
        } catch (DomainException e) {
            throw new UIException("Error getting a list of devices");
        }
        tableView.setItems(list);

        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (deviceSelected != tableView.getFocusModel().getFocusedItem()) {
            deviceSelected = tableView.getFocusModel().getFocusedItem();
            commentField.setText(deviceSelected.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        deviceSelected.setComment(commentField.getText());
        try {
            deviceSelected.update();
        } catch (DomainException e) {
            e.printStackTrace();
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
            deviceSelected.delete();
            tableView.getItems().remove(deviceSelected);
            commentField.setText("");
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }
}
