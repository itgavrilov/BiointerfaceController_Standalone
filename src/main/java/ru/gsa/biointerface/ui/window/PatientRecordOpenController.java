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
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.services.ServiceExamination;
import ru.gsa.biointerface.services.ServicePatientRecord;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordOpenController extends AbstractWindow implements WindowWithProperty<PatientRecord> {
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
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (patientRecord == null)
            throw new UIException("servicePatientRecord is null. First call setParameter()");

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
            examinations.addAll(ServiceExamination.getInstance().getByPatientRecord(patientRecord));
        } catch (ServiceException e) {
            throw new UIException("Error getting a examinations");
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
        String comment = patientRecord.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                patientRecord.setComment(commentField.getText());
                ServicePatientRecord.getInstance().update(patientRecord);
            } catch (ServiceException e) {
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
        } catch (UIException e) {
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
            } catch (UIException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDeleteButtonPush() {
        try {
            ServiceExamination.getInstance().delete(examination);
            commentField.setText("");
            tableView.getItems().remove(examination);
            examination = null;
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml")
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }
}
