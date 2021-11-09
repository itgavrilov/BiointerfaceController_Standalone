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

    public IcdsController() throws Exception {
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
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null) {
            throw new NullPointerException("" +
                    "resourceSource or transitionGUI is null. " +
                    "First call setResourceAndTransition()" +
                    "");
        }

        ObservableList<Icd> icds = FXCollections.observableArrayList();
        icds.addAll(icdService.findAll());
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
                icdService.save(icd);
            } catch (Exception e) {
                commentField.setText(comment);
                icd.setComment(comment);
                new AlertError("Error change comment for ICD: " + e.getMessage());
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

    public void onAddButtonPush() {
        try {
            generateNewWindow("fxml/IcdAdd.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load form for add ICD: " + e.getMessage());
        }
    }

    public void onDeleteButtonPush() {
        try {
            icdService.delete(icd);
            tableView.getItems().remove(icd);
            commentField.setText("");
        } catch (Exception e) {
            new AlertError("Error delete ICD: " + e.getMessage());
        }
    }
}
