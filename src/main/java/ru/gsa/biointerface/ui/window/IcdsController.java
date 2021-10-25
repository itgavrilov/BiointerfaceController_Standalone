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
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.services.IcdService;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdsController extends AbstractWindow {
    private final IcdService icdService;
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

    public IcdsController() throws NoConnectionException {
        icdService = IcdService.getInstance();
    }

    @Override
    public String getTitleWindow() {
        return ": ICDs";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        ObservableList<Icd> icds = FXCollections.observableArrayList();
        try {
            icds.addAll(icdService.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.setItems(icds);
        icdCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionCol.setStyle("-fx-alignment: center;");
        transitionGUI.show();
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
        if (Objects.equals(icd.getComment(), commentField.getText())) {
            String comment = icd.getComment();
            icd.setComment(commentField.getText());
            try {
                icdService.update(icd);
            } catch (Exception e) {
                commentField.setText(comment);
                icd.setComment(comment);
                e.printStackTrace();
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml").showWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAddButtonPush() {
        try {
            generateNewWindow("fxml/IcdAdd.fxml").showWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            icdService.delete(icd);
            tableView.getItems().remove(icd);
            commentField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
