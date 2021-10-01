package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Icd;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordsController extends AbstractWindow {
    int idSelectedRow = -1;

    @FXML
    private TableView<PatientRecord> tableView;
    @FXML
    private TableColumn<PatientRecord, Integer> idCol;
    @FXML
    private TableColumn<PatientRecord, String> secondNameCol;
    @FXML
    private TableColumn<PatientRecord, String> firstNameCol;
    @FXML
    private TableColumn<PatientRecord, String> middleNameCol;
    @FXML
    private TableColumn<PatientRecord, String> birthdayCol;
    @FXML
    private TableColumn<PatientRecord, Icd> icdCol;
    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        tableView.getItems().clear();
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                onMouseClickedTableView(mouseEvent);
            }
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: center-right;");
        secondNameCol.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        birthdayCol.setCellValueFactory(param -> {
            PatientRecord patientRecord = param.getValue();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate birthday = patientRecord.getBirthday();
            return new SimpleObjectProperty<>(birthday.format(dateFormatter));
        });
        birthdayCol.setStyle("-fx-alignment: center;");

        icdCol.setCellValueFactory(param -> {
            PatientRecord patientRecord = param.getValue();
            Icd icd = patientRecord.getIcd();
            return new SimpleObjectProperty<>(icd);
        });
        try {
            ObservableList<Icd> list = FXCollections.observableArrayList();
            list.add(null);
            list.addAll(Icd.getAll());
            icdCol.setCellFactory(ComboBoxTableCell.forTableColumn(list));
        } catch (DomainException e) {
            e.printStackTrace();
        }

        icdCol.setOnEditCommit((TableColumn.CellEditEvent<PatientRecord, Icd> event) -> {
            TablePosition<PatientRecord, Icd> pos = event.getTablePosition();

            Icd newIcd = event.getNewValue();

            int row = pos.getRow();
            PatientRecord patientRecord = event.getTableView().getItems().get(row);

            if (newIcd != null && (!newIcd.equals(patientRecord.getIcd()))
                    || patientRecord.getIcd() != null && (!patientRecord.getIcd().equals(newIcd))) {
                patientRecord.setIcd(newIcd);
                try {
                    patientRecord.update();
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
        });

        ObservableList<PatientRecord> list = FXCollections.observableArrayList();
        try {
            list.addAll(PatientRecord.getSetAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }

        tableView.setItems(list);
        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return ": start";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (idSelectedRow != tableView.getFocusModel().getFocusedCell().getRow()) {
            idSelectedRow = tableView.getFocusModel().getFocusedCell().getRow();
            commentField.setText(
                    tableView.getItems().get(idSelectedRow).getComment()
            );
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

        if (mouseEvent.getClickCount() == 2) {
            try {
                ((WindowWithProperty<PatientRecord>) generateNewWindow("PatientRecordOpen.fxml"))
                        .setProperty(tableView.getItems().get(idSelectedRow))
                        .showWindow();
            } catch (UIException e) {
                e.printStackTrace();
            }
        }
    }

    public void createNewPatientRecord() {
        try {
            generateNewWindow("PatientRecordAdd.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() {
        if (!commentField.getText().equals(tableView.getItems().get(idSelectedRow).getComment())) {
            PatientRecord patientRecord = tableView.getItems().get(idSelectedRow);
            patientRecord.setComment(commentField.getText());
            try {
                patientRecord.update();
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDeleteButtonPush() {
        PatientRecord patientRecord = tableView.getItems().get(idSelectedRow);
        try {
            patientRecord.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
