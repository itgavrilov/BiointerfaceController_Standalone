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
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordOpenController extends AbstractWindow implements WindowWithProperty<PatientRecord> {
    int idSelectedRow = -1;
    private PatientRecord patientRecord;
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
    private TableColumn<Examination, String> dateTimeCol;
    @FXML
    private TableColumn<Examination, Integer> deviceIdCol;
    @FXML
    private Button deleteButton;

    public WindowWithProperty<PatientRecord> setProperty(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        this.patientRecord = patientRecord;

        return this;
    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (patientRecord == null)
            throw new UIException("patientRecord is null. First call setParameter()");

        idText.setText(String.valueOf(patientRecord.getId()));
        secondNameText.setText(patientRecord.getSecondName());
        firstNameText.setText(patientRecord.getFirstName());
        middleNameText.setText(patientRecord.getMiddleName());
        if (patientRecord.getIcd() != null)
            icdText.setText(patientRecord.getIcd().toString());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthdayText.setText(patientRecord.getBirthday().format(dateFormatter));

        tableView.getItems().clear();
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                onMouseClickedTableView(mouseEvent);
            }
        });
        dateTimeCol.setCellValueFactory(param -> {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime dateTime = param.getValue().getDateTime();
            return new SimpleObjectProperty<>(dateTime.format(dateTimeFormatter));
        });
        deviceIdCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDevice().getId()));

        ObservableList<Examination> list = FXCollections.observableArrayList();
        try {
            list.addAll(Examination.getByPatientRecordId(patientRecord));
        } catch (DomainException e) {
            throw new UIException("Error getting a list of examinations", e);
        }
        tableView.setItems(list);

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
        if (!commentField.getText().equals(patientRecord.getComment())) {
            patientRecord.setComment(commentField.getText());
            try {
                patientRecord.update();
            } catch (DomainException e) {
                e.printStackTrace();
            }
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

    public void onAddButtonPush() {
        try {
            ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/Metering.fxml"))
                    .setProperty(patientRecord)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        Examination examination = tableView.getItems().get(idSelectedRow);
        try {
            examination.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (idSelectedRow != tableView.getFocusModel().getFocusedCell().getRow()) {
            idSelectedRow = tableView.getFocusModel().getFocusedCell().getRow();
            commentField.setText(
                    tableView.getItems().get(idSelectedRow).getComment()
            );
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

        if (mouseEvent.getClickCount() == 2) {
            try {
                ((WindowWithProperty<Examination>) generateNewWindow("fxml/Examination.fxml"))
                        .setProperty(tableView.getItems().get(idSelectedRow))
                        .showWindow();
            } catch (UIException e) {
                e.printStackTrace();
            }
        }
    }
}
