package ru.gsa.biointerfaceController_standalone.uiLayer.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerfaceController_standalone.businessLayer.Icd;
import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;
import ru.gsa.biointerfaceController_standalone.daoLayer.dao.IcdDAO;
import ru.gsa.biointerfaceController_standalone.uiLayer.UIException;

import java.util.Set;

public class Icds extends AbstractWindow {
    Set<Icd> icds;
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
        if(resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        tableView.getItems().clear();
        icdCol.setCellValueFactory(new PropertyValueFactory<>("ICD"));
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionCol.setStyle("-fx-alignment: center;");
        tableView.setItems(getList());

        transitionGUI.show();
    }

    private ObservableList<Icd> getList() {
        ObservableList<Icd> list = FXCollections.observableArrayList();
        try {
            icds = IcdDAO.getInstance().getAll();
            list.addAll(icds);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new NullPointerException("dao is null");
        }

        return list;
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
                IcdDAO.getInstance().update(icd);
            } catch (DAOException e) {
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
            IcdDAO.getInstance().delete(icd);
        } catch (DAOException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
