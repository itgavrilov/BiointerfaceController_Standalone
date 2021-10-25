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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.services.ChannelNameService;
import ru.gsa.biointerface.services.IcdService;
import ru.gsa.biointerface.services.PatientRecordService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordsController extends AbstractWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecordsController.class);
    private final PatientRecordService patientRecordService;
    private final IcdService icdService;
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

    public PatientRecordsController() throws NoConnectionException {
        patientRecordService = PatientRecordService.getInstance();
        icdService = IcdService.getInstance();
    }

    @Override
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        ObservableList<PatientRecord> patientRecords = FXCollections.observableArrayList();
        patientRecords.addAll(patientRecordService.getAll());
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
    }

    private void setIcdComboBox() {
        ObservableList<Icd> icds = FXCollections.observableArrayList();
        try {
            List<Icd> icdList = icdService.getAll();
            icds.add(null);
            icds.addAll(icdList);
        } catch (Exception e) {
            new AlertError("Error load icds: " + e.getMessage());
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
                LOGGER.info("Set new icd in patientRecord(number={})", patientRecord.getId());
                try {
                    patientRecord.setIcd(newIcd);
                    patientRecordService.update(patientRecord);
                } catch (Exception e){
                    patientRecord.setIcd(icd);
                    LOGGER.error("Error set new icd in patientRecord(number={})", patientRecord.getId());
                    new AlertError("Error set new icd: " + e.getMessage());
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
            } catch (Exception e) {
                new AlertError("Error load patient record: " + e.getMessage());
            }
        }
    }

    public void createNewPatientRecord() {
        try {
            generateNewWindow("fxml/PatientRecordAdd.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load form for add patient record: " + e.getMessage());
        }
    }

    public void commentFieldChange() {
        LOGGER.info("Change commentField for Examination");
        if (Objects.equals(patientRecord.getComment(), commentField.getText())) {
            patientRecord.setComment(commentField.getText());
            try {
                patientRecordService.update(patientRecord);
                LOGGER.info("New comment is set in patientRecord(number={})", patientRecord.getId());
            } catch (Exception e){
                commentField.setText(patientRecord.getComment());
                LOGGER.error("Error update patientRecord(number={})", patientRecord.getId(), e);
                new AlertError("Error update patient record: " + e.getMessage());
            }
        }
    }

    public void onDeleteButtonPush() {
        LOGGER.info("Delete button push");
        try {
            patientRecordService.delete(patientRecord);
            tableView.getItems().remove(patientRecord);
            commentField.setText("");
        } catch (Exception e) {
            LOGGER.error("Error delete patientRecord(number={})", patientRecord.getId(), e);
            new AlertError("Error delete patient record: " + e.getMessage());
        }
    }
}
