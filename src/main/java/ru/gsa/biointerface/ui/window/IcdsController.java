package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Icd;
import ru.gsa.biointerface.ui.UIException;


public class IcdsController extends AbstractWindow {
    int idSelectedRow = -1;

    @FXML
    private TableView<Icd> tableView;
    @FXML
    private TableColumn<Icd, String> icdCol;
    @FXML
    private TableColumn<Icd, Integer> versionCol;
    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;


    @Override
    public String getTitleWindow() {
        return ": ICDs";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        tableView.getItems().clear();
        icdCol.setCellValueFactory(new PropertyValueFactory<>("ICD"));
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionCol.setStyle("-fx-alignment: center;");

        ObservableList<Icd> list = FXCollections.observableArrayList();
        try {
            list.addAll(Icd.getSetAll());
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
            Icd icd = tableView.getItems().get(idSelectedRow);
            icd.setComment(commentField.getText());
            try {
                icd.update();
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

    public void onAdd() {
        try {
            generateNewWindow("IcdAdd.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDelete() {
        Icd icd = tableView.getItems().get(idSelectedRow);
        try {
            icd.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
