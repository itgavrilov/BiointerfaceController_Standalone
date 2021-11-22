package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.services.ExaminationService;
import ru.gsa.biointerface.services.PatientService;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientOpenController extends AbstractWindow implements WindowWithProperty<Patient> {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private Patient patient;
    private Examination examination;
    @FXML
    private Text idText;
    @FXML
    private Text secondNameText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text patronymicText;
    @FXML
    private Text birthdayText;
    @FXML
    private Text icdText;
    @FXML
    private TextArea commentField;
    @FXML
    private TableView<Examination> tableView;
    @FXML
    private TableColumn<Examination, String> startTimeCol;
    @FXML
    private TableColumn<Examination, Integer> deviceIdCol;
    @FXML
    private Button deleteButton;

    public WindowWithProperty<Patient> setProperty(Patient patient) {
        if (patient == null)
            throw new NullPointerException("PatientRecord is null");

        this.patient = patient;

        return this;
    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null) {
            throw new NullPointerException("" +
                    "resourceSource or transitionGUI is null. " +
                    "First call setResourceAndTransition()" +
                    "");
        }
        if (patient == null) {
            throw new NullPointerException("" +
                    "servicePatientRecord is null. " +
                    "First call setParameter()" +
                    "");
        }

        idText.setText(String.valueOf(patient.getId()));
        secondNameText.setText(patient.getSecondName());
        firstNameText.setText(patient.getFirstName());
        patronymicText.setText(patient.getPatronymic());
        birthdayText.setText(dateFormatter.format(patient.getBirthday().getTime()));

        if (patient.getIcd() != null) {
            Icd icd = patient.getIcd();
            icdText.setText(icd.getName() + " (ICD-" + icd.getVersion() + ")");
        } else {
            icdText.setText("-");
        }

        tableView.getItems().clear();
        ObservableList<Examination> examinations = FXCollections.observableArrayList();
        try {
            examinations.addAll(ExaminationService.getInstance().findAllByPatientRecord(patient));
        } catch (Exception e) {
            new AlertError("Error load list examinations: " + e.getMessage());
        }
        tableView.setItems(examinations);
        startTimeCol.setCellValueFactory(param -> new SimpleObjectProperty<>(
                dateTimeFormatter.format(param.getValue().getStarttime())));
        deviceIdCol.setCellValueFactory(param -> new SimpleObjectProperty<>(
                param.getValue().getDevice().getId()));
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                onMouseClickedTableView(mouseEvent);
            }
        });

        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return ": patients";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void commentFieldChange() {
        if (Objects.equals(patient.getComment(), commentField.getText())) {
            String comment = patient.getComment();
            patient.setComment(commentField.getText());
            try {
                PatientService.getInstance().save(patient);
            } catch (Exception e) {
                commentField.setText(comment);
                patient.setComment(comment);
                new AlertError("Error change comment for patient: " + e.getMessage());
            }
        }
    }

    public void onAddButtonPush() {
        try {
            //noinspection unchecked
            ((WindowWithProperty<Patient>) generateNewWindow("fxml/Metering.fxml"))
                    .setProperty(patient)
                    .showWindow();
        } catch (Exception e) {
            new AlertError("Error load form for metering: " + e.getMessage());
        }
    }

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (examination != tableView.getFocusModel().getFocusedItem()) {
            examination = tableView.getFocusModel().getFocusedItem();
            commentField.setText(examination.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

        if (mouseEvent.getClickCount() == 2) {
            try {
                //noinspection unchecked
                ((WindowWithProperty<Examination>) generateNewWindow("fxml/Examination.fxml"))
                        .setProperty(examination)
                        .showWindow();
            } catch (Exception e) {
                new AlertError("Error load examination: " + e.getMessage());
            }
        }
    }

    public void onDeleteButtonPush() {
        try {
            ExaminationService.getInstance().delete(examination);
            commentField.setText("");
            tableView.getItems().remove(examination);
            examination = null;
        } catch (Exception e) {
            new AlertError("Error delete examination: " + e.getMessage());
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Patients.fxml")
                    .showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient records: " + e.getMessage());
        }
    }
}
