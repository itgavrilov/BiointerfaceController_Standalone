package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientsController extends AbstractWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientsController.class);
    private final PatientService patientService;
    private final IcdService icdService;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
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
    private Patient patient;
    @FXML
    private TableView<Patient> tableView;
    @FXML
    private TableColumn<Patient, Integer> idCol;
    @FXML
    private TableColumn<Patient, String> secondNameCol;
    @FXML
    private TableColumn<Patient, String> firstNameCol;
    @FXML
    private TableColumn<Patient, String> middleNameCol;
    @FXML
    private TableColumn<Patient, String> birthdayCol;
    @FXML
    private TableColumn<Patient, Icd> icdCol;
    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;

    public PatientsController() throws Exception {
        patientService = PatientService.getInstance();
        icdService = IcdService.getInstance();
    }

    @Override
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null) {
            throw new NullPointerException("" +
                    "resourceSource or transitionGUI is null. " +
                    "First call setResourceAndTransition()" +
                    "");
        }

        ObservableList<Patient> patients = FXCollections.observableArrayList();
        patients.addAll(patientService.findAll());
        tableView.setItems(patients);
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                onMouseClickedTableView(mouseEvent);
            }
        });
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        secondNameCol.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        birthdayCol.setCellValueFactory(param -> {
            Patient patient = param.getValue();
            return new SimpleObjectProperty<>(dateFormatter.format(patient.getBirthday().getTime()));
        });
        setIcdComboBox();
        transitionGUI.show();
    }

    private void setIcdComboBox() {
        ObservableList<Icd> icds = FXCollections.observableArrayList();
        try {
            List<Icd> icdList = icdService.findAll();
            icds.add(null);
            icds.addAll(icdList);
        } catch (Exception e) {
            new AlertError("Error load icds: " + e.getMessage());
        }
        icdCol.setCellFactory(ComboBoxTableCell.forTableColumn(converter, icds));
        icdCol.setCellValueFactory(param -> {
            Patient patient = param.getValue();
            Icd icd = patient.getIcd();
            return new SimpleObjectProperty<>(icd);
        });
        icdCol.setOnEditCommit((TableColumn.CellEditEvent<Patient, Icd> event) -> {
            Patient patient = event
                    .getTableView()
                    .getSelectionModel()
                    .getSelectedItem();
            Icd icd = patient.getIcd();
            Icd newIcd = event.getNewValue();

            if (!Objects.equals(newIcd, icd)) {
                LOGGER.info("Set new icd in patient(id={})", patient.getId());
                try {
                    patient.setIcd(newIcd);
                    patientService.save(patient);
                } catch (Exception e) {
                    patient.setIcd(icd);
                    LOGGER.error("Error set new icd in patient(id={})", patient.getId());
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
    public void resizeWindow(double height, double width) {
    }

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (patient != tableView.getFocusModel().getFocusedItem()) {
            patient = tableView.getFocusModel().getFocusedItem();
            commentField.setText(patient.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

        if (mouseEvent.getClickCount() == 2) {
            try {
                //noinspection unchecked
                ((WindowWithProperty<Patient>) generateNewWindow("fxml/PatientOpen.fxml"))
                        .setProperty(patient)
                        .showWindow();
            } catch (Exception e) {
                new AlertError("Error load patient: " + e.getMessage());
            }
        }
    }

    public void createNewPatientRecord() {
        try {
            generateNewWindow("fxml/PatientAdd.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load form for add patient: " + e.getMessage());
        }
    }

    public void commentFieldChange() {
        LOGGER.info("Change commentField for Examination");
        if (Objects.equals(patient.getComment(), commentField.getText())) {
            patient.setComment(commentField.getText());
            try {
                patientService.save(patient);
                LOGGER.info("New comment is set in patient(id={})", patient.getId());
            } catch (Exception e) {
                commentField.setText(patient.getComment());
                LOGGER.error("Error update patient(id={})", patient.getId(), e);
                new AlertError("Error update patient: " + e.getMessage());
            }
        }
    }

    public void onDeleteButtonPush() {
        LOGGER.info("Delete button push");
        try {
            patientService.delete(patient);
            tableView.getItems().remove(patient);
            commentField.setText("");
        } catch (Exception e) {
            LOGGER.error("Error delete patient(number={})", patient.getId(), e);
            new AlertError("Error delete patient record: " + e.getMessage());
        }
    }
}
