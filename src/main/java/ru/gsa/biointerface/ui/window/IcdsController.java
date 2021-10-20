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

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdsController extends AbstractWindow {
    private Icd icdSelected;
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
            list.addAll(Icd.getAll());
        } catch (DomainException e) {
            throw new UIException("Error getting a list of ICDs");
        }
        tableView.setItems(list);
        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (icdSelected != tableView.getFocusModel().getFocusedItem()) {
            icdSelected = tableView.getFocusModel().getFocusedItem();
            commentField.setText(icdSelected.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        try {
            icdSelected.setComment(commentField.getText());
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

    public void onAddButtonPush() {
        try {
            generateNewWindow("fxml/IcdAdd.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            icdSelected.delete();
            tableView.getItems().remove(icdSelected);
            commentField.setText("");
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }
}
