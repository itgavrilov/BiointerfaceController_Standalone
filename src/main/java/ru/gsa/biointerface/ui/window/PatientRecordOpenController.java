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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.services.ExaminationService;
import ru.gsa.biointerface.services.PatientRecordService;
import ru.gsa.biointerface.ui.window.metering.MeteringController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordOpenController extends AbstractWindow implements WindowWithProperty<PatientRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecordOpenController.class);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private PatientRecord patientRecord;
    private Examination examination;
    @FXML
    private Text idText;
    @FXML
    private Text secondNameText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text middleNameText;
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
    private TableColumn<Examination, Long> deviceIdCol;
    @FXML
    private Button deleteButton;

    public WindowWithProperty<PatientRecord> setProperty(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");

        this.patientRecord = patientRecord;

        return this;
    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (patientRecord == null)
            throw new NullPointerException("servicePatientRecord is null. First call setParameter()");

        idText.setText(String.valueOf(patientRecord.getId()));
        secondNameText.setText(patientRecord.getSecondName());
        firstNameText.setText(patientRecord.getFirstName());
        middleNameText.setText(patientRecord.getMiddleName());
        birthdayText.setText(patientRecord.getBirthdayInLocalDate().format(dateFormatter));

        if (patientRecord.getIcd() != null) {
            Icd icd = patientRecord.getIcd();
            icdText.setText(icd.getName() + " (ICD-" + icd.getVersion() + ")");
        } else {
            icdText.setText("-");
        }

        tableView.getItems().clear();
        ObservableList<Examination> examinations = FXCollections.observableArrayList();
        try {
            examinations.addAll(ExaminationService.getInstance().getByPatientRecord(patientRecord));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.setItems(examinations);
        startTimeCol.setCellValueFactory(param -> {
            LocalDateTime dateTime = param.getValue().getStartTimeInLocalDateTime();
            return new SimpleObjectProperty<>(dateTime.format(dateTimeFormatter));
        });
        deviceIdCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDevice().getId()));
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                onMouseClickedTableView(mouseEvent);
            }
        });

        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return ": patient record";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void commentFieldChange() {
        if (Objects.equals(patientRecord.getComment(), commentField.getText())) {
            String comment = patientRecord.getComment();
            patientRecord.setComment(commentField.getText());
            try {
                PatientRecordService.getInstance().update(patientRecord);
            } catch (Exception e) {
                commentField.setText(comment);
                patientRecord.setComment(comment);
                e.printStackTrace();
            }
        }
    }

    public void onAddButtonPush() {
        try {
            //noinspection unchecked
            ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/Metering.fxml"))
                    .setProperty(patientRecord)
                    .showWindow();
        } catch (Exception e) {
            e.printStackTrace();
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
                e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml")
                    .showWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
