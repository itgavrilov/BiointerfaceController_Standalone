package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.services.ExaminationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationsController extends AbstractWindow {
    private final ExaminationService examinationService;
    private Examination examination;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    @FXML
    private TableView<Examination> tableView;
    @FXML
    private TableColumn<Examination, String> startTimeCol;
    @FXML
    private TableColumn<Examination, String> patientCol;
    @FXML
    private TableColumn<Examination, Long> deviceIdCol;

    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;

    public ExaminationsController() throws NoConnectionException {
        examinationService = ExaminationService.getInstance();
    }

    @Override
    public String getTitleWindow() {
        return ": Examinations";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        ObservableList<Examination> examinations = FXCollections.observableArrayList();
        try {
            examinations.addAll(examinationService.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.setItems(examinations);
        startTimeCol.setCellValueFactory(param -> {
            LocalDateTime dateTime = param.getValue().getStartTimeInLocalDateTime();
            return new SimpleObjectProperty<>(dateTime.format(dateFormatter));
        });
        patientCol.setCellValueFactory(param -> {
            PatientRecord patientRecord = param.getValue().getPatientRecord();
            String initials = patientRecord.getSecondName() + " " +
                    patientRecord.getFirstName().charAt(0) + ".";
            if (!"".equals(patientRecord.getMiddleName()))
                initials += patientRecord.getMiddleName().charAt(0) + ".";

            return new SimpleObjectProperty<>(initials);
        });
        deviceIdCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDevice().getId()));
        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (examination != tableView.getFocusModel().getFocusedItem()) {
            examination = tableView.getFocusModel().getFocusedItem();
            commentField.setText(examination.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        if (Objects.equals(examination.getComment(), commentField.getText())) {
            String comment = examination.getComment();
            examination.setComment(commentField.getText());
            try {
                examinationService.update(examination);
            } catch (Exception e) {
                examination.setComment(comment);
                commentField.setText(comment);
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

    public void onDeleteButtonPush() {
        try {
            examinationService.delete(examination);
            tableView.getItems().remove(examination);
            commentField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
