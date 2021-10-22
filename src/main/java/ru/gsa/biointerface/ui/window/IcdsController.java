package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.services.ServiceIcd;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.services.ServicePatientRecord;
import ru.gsa.biointerface.ui.UIException;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdsController extends AbstractWindow {
    private ServiceIcd serviceIcd;
    private Icd icd;
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

        try {
            serviceIcd = ServiceIcd.getInstance();
            ObservableList<Icd> icds = FXCollections.observableArrayList();
            icds.addAll(serviceIcd.getAll());
            tableView.setItems(icds);
            icdCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
            versionCol.setStyle("-fx-alignment: center;");
            transitionGUI.show();
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
    }

    public void onMouseClickedTableView() {
        if (icd != tableView.getFocusModel().getFocusedItem()) {
            icd = tableView.getFocusModel().getFocusedItem();
            commentField.setText(icd.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        String comment = icd.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                icd.setComment(commentField.getText());
                serviceIcd.update(icd);
            } catch (ServiceException e) {
                icd.setComment(comment);
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

    public void onAddButtonPush() {
        try {
            generateNewWindow("fxml/IcdAdd.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            serviceIcd.delete(icd);
            tableView.getItems().remove(icd);
            commentField.setText("");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
