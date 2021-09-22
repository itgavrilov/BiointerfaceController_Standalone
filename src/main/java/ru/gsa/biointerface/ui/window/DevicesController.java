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

public class DevicesController extends AbstractWindow {
    int idSelectedRow = -1;

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
            list.addAll(Device.getSetAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        tableView.setItems(list);

        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (idSelectedRow != tableView.getFocusModel().getFocusedCell().getRow()) {
            idSelectedRow = tableView.getFocusModel().getFocusedCell().getRow();
            commentField.setText(
                    tableView.getItems().get(idSelectedRow).getComment()
            );
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        if (!commentField.getText().equals(tableView.getItems().get(idSelectedRow).getComment())) {
            Device device = tableView.getItems().get(idSelectedRow);
            device.setComment(commentField.getText());
            try {
                device.update();
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBack() {
        try {
            generateNewWindow("PatientRecords.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDelete() {
        Device device = tableView.getItems().get(idSelectedRow);
        try {
            device.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
