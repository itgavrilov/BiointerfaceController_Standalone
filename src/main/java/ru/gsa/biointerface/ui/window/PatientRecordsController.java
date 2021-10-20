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
import javafx.util.StringConverter;
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
    private final StringConverter<Icd> converter = new StringConverter<>() {
        @Override
        public String toString(Icd icd) {
            String str = "";
            if (icd != null)
                str = icd.getICD() + " (ICD-" + icd.getVersion() + ")";
            return str;
        }

        @Override
        public Icd fromString(String string) {
            return null;
        }
    };
    private PatientRecord patientRecordSelected;
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


        ObservableList<Icd> listIcd = FXCollections.observableArrayList();
        listIcd.add(null);
        try {
            listIcd.addAll(Icd.getAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }

        icdCol.setCellFactory(ComboBoxTableCell.forTableColumn(converter, listIcd));

        icdCol.setOnEditCommit((TableColumn.CellEditEvent<PatientRecord, Icd> event) -> {
            TablePosition<PatientRecord, Icd> pos = event.getTablePosition();

            Icd newIcd = event.getNewValue();

            int row = pos.getRow();
            PatientRecord patientRecord = event.getTableView().getItems().get(row);

            if (newIcd != null && (!newIcd.equals(patientRecord.getIcd()))
                    || patientRecord.getIcd() != null && (!patientRecord.getIcd().equals(newIcd))) {
                try {
                    patientRecord.setIcd(newIcd);
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
        });

        ObservableList<PatientRecord> listPatientRecord = FXCollections.observableArrayList();
        try {
            listPatientRecord.addAll(PatientRecord.getSetAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        tableView.setItems(listPatientRecord);

        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return "";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (patientRecordSelected != tableView.getFocusModel().getFocusedItem()) {
            patientRecordSelected = tableView.getFocusModel().getFocusedItem();
            commentField.setText(patientRecordSelected.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

        if (mouseEvent.getClickCount() == 2) {
            try {
                ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/PatientRecordOpen.fxml"))
                        .setProperty(patientRecordSelected)
                        .showWindow();
            } catch (UIException e) {
                e.printStackTrace();
            }
        }
    }

    public void createNewPatientRecord() {
        try {
            generateNewWindow("fxml/PatientRecordAdd.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() {
        try {
            patientRecordSelected.setComment(commentField.getText());
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            patientRecordSelected.delete();
            tableView.getItems().remove(patientRecordSelected);
            commentField.setText("");
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }
}
