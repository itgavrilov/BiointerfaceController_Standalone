package ru.gsa.biointerfaceController_standalone.uiLayer.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import ru.gsa.biointerfaceController_standalone.businessLayer.Examination;
import ru.gsa.biointerfaceController_standalone.businessLayer.Icd;
import ru.gsa.biointerfaceController_standalone.businessLayer.PatientRecord;
import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;
import ru.gsa.biointerfaceController_standalone.daoLayer.dao.IcdDAO;
import ru.gsa.biointerfaceController_standalone.daoLayer.dao.PatientRecordDAO;
import ru.gsa.biointerfaceController_standalone.uiLayer.UIException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientRecords extends AbstractWindow {
    int idSelectedRow = -1;

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

    @Override
    public void showWindow() throws UIException {
        if(resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        tableView.getItems().clear();
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    onMouseClickedTableView(mouseEvent);
                }
            }
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: center-right;");
        secondNameCol.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        birthdayCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PatientRecord, String> param) {
                PatientRecord patientRecord = param.getValue();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate birthday = patientRecord.getBirthday();
                return new SimpleObjectProperty<>(birthday.format(dateFormatter));
            }
        });
        birthdayCol.setStyle("-fx-alignment: center;");

        icdCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<Icd> call(TableColumn.CellDataFeatures<PatientRecord, Icd> param) {
                PatientRecord patientRecord = param.getValue();
                Icd icd = patientRecord.getIcd();
                return new SimpleObjectProperty<>(icd);
            }
        });
        icdCol.setCellFactory(ComboBoxTableCell.forTableColumn(getIcdList()));

        icdCol.setOnEditCommit((TableColumn.CellEditEvent<PatientRecord, Icd> event) -> {
            TablePosition<PatientRecord, Icd> pos = event.getTablePosition();

            Icd newIcd = event.getNewValue();

            int row = pos.getRow();
            PatientRecord patientRecord = event.getTableView().getItems().get(row);

            if (newIcd != null && (!newIcd.equals(patientRecord.getIcd()))
                    || patientRecord.getIcd() != null && (!patientRecord.getIcd().equals(newIcd))) {
                patientRecord.setIcd(newIcd);
                try {
                    PatientRecordDAO.getInstance()
                            .update(patientRecord);
                } catch (DAOException e) {
                    e.printStackTrace();
                }
            }
        });

        tableView.setItems(getList());

        transitionGUI.show();
    }

    private ObservableList<PatientRecord> getList() {
        ObservableList<PatientRecord> list = FXCollections.observableArrayList();
        try {
            list.addAll(PatientRecordDAO.getInstance().getAll());
        } catch (DAOException e) {
            e.printStackTrace();
            throw new NullPointerException("dao is null");
        }

        return list;
    }

    private ObservableList<Icd> getIcdList() {
        ObservableList<Icd> list = FXCollections.observableArrayList();

        try {
            list.add(null);
            list.addAll(IcdDAO.getInstance().getAll());
        } catch (DAOException e) {
            e.printStackTrace();
            throw new NullPointerException("dao is null");
        }

        return list;
    }

    @Override
    public String getTitleWindow() {
        return ": start";
    }

    @Override
    public void resizeWindow(double height, double width) {

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
                ((WindowControllerWithProperty<PatientRecord>) generateNewWindow("PatientRecordOpen.fxml"))
                        .setProperty(tableView.getItems().get(idSelectedRow))
                        .showWindow();
            } catch (UIException e) {
                e.printStackTrace();
            }
        }
    }

    public void createNewPatientRecord() {
        try {
            generateNewWindow("PatientRecordAdd.fxml").showWindow();;
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() {
        if (!commentField.getText().equals(tableView.getItems().get(idSelectedRow).getComment())) {
            PatientRecord patientRecord = tableView.getItems().get(idSelectedRow);
            patientRecord.setComment(commentField.getText());
            try {
                PatientRecordDAO.getInstance()
                        .update(patientRecord);
            } catch (DAOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDelete() {
        PatientRecord patientRecord = tableView.getItems().get(idSelectedRow);
        try {
            PatientRecordDAO.getInstance()
                    .delete(patientRecord);
        } catch (DAOException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
