package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationsController extends AbstractWindow {
    Examination examinationSelected;

    @FXML
    private TableView<Examination> tableView;
    @FXML
    private TableColumn<Examination, String> dateTimeCol;
    @FXML
    private TableColumn<Examination, String> patientCol;
    @FXML
    private TableColumn<Examination, Integer> deviceIdCol;

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

        tableView.getItems().clear();

        dateTimeCol.setCellValueFactory(param -> {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime dateTime = param.getValue().getDateTime();
            return new SimpleObjectProperty<>(dateTime.format(dateFormatter));
        });
        patientCol.setCellValueFactory(param -> {
            PatientRecord patientRecord = param.getValue().getPatientRecord();
            String Initials = patientRecord.getSecondName() + " " +
                    patientRecord.getFirstName().charAt(0) + ".";
            if (!"".equals(patientRecord.getMiddleName()))
                Initials += patientRecord.getMiddleName().charAt(0) + ".";

            return new SimpleObjectProperty<>(Initials);
        });
        deviceIdCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDevice().getId()));

        ObservableList<Examination> list = FXCollections.observableArrayList();
        try {
            list.addAll(Examination.getAll());
        } catch (DomainException e) {
            throw new UIException("Error getting a list of examinations");
        }
        tableView.setItems(list);

        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (examinationSelected != tableView.getFocusModel().getFocusedItem()) {
            examinationSelected = tableView.getFocusModel().getFocusedItem();
            commentField.setText(examinationSelected.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        try {
            examinationSelected.setComment(commentField.getText());
        } catch (DomainException e) {
            e.printStackTrace();
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
            examinationSelected.delete();
            tableView.getItems().remove(examinationSelected);
            commentField.setText("");
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }
}
