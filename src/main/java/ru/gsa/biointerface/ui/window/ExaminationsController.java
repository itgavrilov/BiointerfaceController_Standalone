package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Examinations;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExaminationsController extends AbstractWindow {
    int idSelectedRow = -1;

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
            list.addAll(Examinations.getSetAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        tableView.setItems(list);

        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (idSelectedRow != tableView.getFocusModel().getFocusedCell().getRow()) {
            idSelectedRow = tableView.getFocusModel().getFocusedCell().getRow();
            commentField.setText(
                    tableView.getItems().get(idSelectedRow).getComment()
            );
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        if (!commentField.getText().equals(tableView.getItems().get(idSelectedRow).getComment())) {
            Examination examination = tableView.getItems().get(idSelectedRow);
            examination.setComment(commentField.getText());
            try {
                Examinations.update(examination);
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBack() {
        try {
            generateNewWindow("PatientRecords.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDelete() {
        Examination examination = tableView.getItems().get(idSelectedRow);
        try {
            Examinations.delete(examination);
        } catch (DomainException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
