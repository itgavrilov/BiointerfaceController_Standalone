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
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.services.ServiceIcd;
import ru.gsa.biointerface.services.ServicePatientRecord;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordsController extends AbstractWindow {
    private ServicePatientRecord servicePatientRecord;
    private ServiceIcd serviceIcd;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final StringConverter<Icd> converter = new StringConverter<>() {
        @Override
        public String toString(Icd icd) {
            String str = "";
            if (icd != null)
                str = icd.getName() + " (ICD-" + icd.getVersion() + ")";
            return str;
        }

        @Override
        public Icd fromString(String string) {
            return null;
        }
    };
    private PatientRecord patientRecord;
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

        try {
            servicePatientRecord = ServicePatientRecord.getInstance();
            serviceIcd = ServiceIcd.getInstance();
            ObservableList<PatientRecord> patientRecords = FXCollections.observableArrayList();
            patientRecords.addAll(servicePatientRecord.getAll());
            tableView.setItems(patientRecords);
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
                LocalDate birthday = patientRecord.getBirthdayInLocalDate();
                return new SimpleObjectProperty<>(birthday.format(dateFormatter));
            });
            birthdayCol.setStyle("-fx-alignment: center;");
            setIcdComboBox();
            transitionGUI.show();
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
    }

    private void setIcdComboBox() {
        ObservableList<Icd> icds = FXCollections.observableArrayList();
        icds.add(null);
        try {
            icds.addAll(serviceIcd.getAll());
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        icdCol.setCellFactory(ComboBoxTableCell.forTableColumn(converter, icds));
        icdCol.setCellValueFactory(param -> {
            PatientRecord patientRecord = param.getValue();
            Icd icd = patientRecord.getIcd();
            return new SimpleObjectProperty<>(icd);
        });
        icdCol.setOnEditCommit((TableColumn.CellEditEvent<PatientRecord, Icd> event) -> {
            PatientRecord patientRecord = event
                    .getTableView()
                    .getSelectionModel()
                    .getSelectedItem();
            Icd icd = patientRecord.getIcd();
            Icd newIcd = event.getNewValue();

            if (!Objects.equals(newIcd, icd)) {
                try {
                    patientRecord.setIcd(newIcd);
                    servicePatientRecord.update(patientRecord);
                } catch (ServiceException e) {
                    patientRecord.setIcd(icd);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String getTitleWindow() {
        return "";
    }

    @Override
    public void resizeWindow(double height, double width) {}

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (patientRecord != tableView.getFocusModel().getFocusedItem()) {
            patientRecord = tableView.getFocusModel().getFocusedItem();
            commentField.setText(patientRecord.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

        if (mouseEvent.getClickCount() == 2) {
            try {
                //noinspection unchecked
                ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/PatientRecordOpen.fxml"))
                        .setProperty(patientRecord)
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
        String comment = patientRecord.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                patientRecord.setComment(commentField.getText());
                servicePatientRecord.update(patientRecord);
            } catch (ServiceException e) {
                patientRecord.setComment(comment);
                e.printStackTrace();
            }
        }
    }

    public void onDeleteButtonPush() {
        try {
            servicePatientRecord.delete(patientRecord);
            tableView.getItems().remove(patientRecord);
            commentField.setText("");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
