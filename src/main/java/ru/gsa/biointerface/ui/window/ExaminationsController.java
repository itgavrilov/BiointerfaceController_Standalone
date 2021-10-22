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
import ru.gsa.biointerface.services.ServiceExamination;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationsController extends AbstractWindow {
    private ServiceExamination serviceExamination;
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


    @Override
    public String getTitleWindow() {
        return ": Examinations";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        try {
            serviceExamination = ServiceExamination.getInstance();
            ObservableList<Examination> examinations = FXCollections.observableArrayList();
            examinations.addAll(serviceExamination.getAll());
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
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
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
        String comment = examination.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                examination.setComment(commentField.getText());
                serviceExamination.update(examination);
            } catch (ServiceException e) {
                examination.setComment(comment);
                e.printStackTrace();
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            serviceExamination.delete(examination);
            tableView.getItems().remove(examination);
            commentField.setText("");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
