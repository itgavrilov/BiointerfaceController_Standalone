package ru.gsa.biointerfaceController_standalone.uiLayer.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ru.gsa.biointerfaceController_standalone.businessLayer.Device;
import ru.gsa.biointerfaceController_standalone.businessLayer.Examination;
import ru.gsa.biointerfaceController_standalone.businessLayer.PatientRecord;
import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;
import ru.gsa.biointerfaceController_standalone.daoLayer.dao.ExaminationDAO;
import ru.gsa.biointerfaceController_standalone.daoLayer.dao.PatientRecordDAO;
import ru.gsa.biointerfaceController_standalone.uiLayer.UIException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatientRecordOpen extends AbstractWindow implements WindowControllerWithProperty<PatientRecord>{
    private PatientRecord patientRecord;
    int idSelectedRow = -1;
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

    public WindowControllerWithProperty<PatientRecord> setProperty(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        this.patientRecord = patientRecord;

        return this;
    }

    @Override
    public void showWindow() throws UIException {
        if(resourceSource == null || transitionGUI == null)
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
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    onMouseClickedTableView(mouseEvent);
                }
            }
        });
        dateTimeCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Examination, String> param) {
                Examination examination = param.getValue();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime dateTime = examination.getDateTime();
                return new SimpleObjectProperty<>(dateTime.format(dateFormatter));
            }
        });
        deviceIdCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Examination, Integer> param) {
                Examination examination = param.getValue();
                Device device = examination.getDevice();
                return new SimpleObjectProperty<>(device.getId());
            }
        });

        tableView.setItems(getList());

        transitionGUI.show();
    }

    private ObservableList<Examination> getList() {
        ObservableList<Examination> list = FXCollections.observableArrayList();
        try {
            list.addAll(ExaminationDAO.getInstance().getByPatientRecordId(patientRecord.getId()));
        } catch (DAOException e) {
            e.printStackTrace();
            throw new NullPointerException("dao is null");
        }

        return list;
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
                PatientRecordDAO.getInstance()
                        .update(patientRecord);
            } catch (DAOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBack() {
        try {
            generateNewWindow("PatientRecords.fxml")
                    .showWindow();;
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onAdd() {
        Examination examination = new Examination(-1, LocalDateTime.now(), patientRecord, null, null);

        try {
            ((WindowControllerWithProperty<Examination>) generateNewWindow("BiointerfaceData.fxml"))
                    .setProperty(examination)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onMouseClickedTableView(MouseEvent mouseEvent) {
        if (idSelectedRow != tableView.getFocusModel().getFocusedCell().getRow()) {
            idSelectedRow = tableView.getFocusModel().getFocusedCell().getRow();
            commentField.setText(
                    tableView.getItems().get(idSelectedRow).getComment()
            );
            //deleteButton.setDisable(false);
            commentField.setDisable(false);
        }

//        if (mouseEvent.getClickCount() == 2) {
//            FXMLLoader loader = new FXMLLoader(rootObject.getClass().getResource("PatientRecordOpen.fxml"));
//            try {
//                transitionGUI.transition(loader);
//                PatientRecordOpen controller = loader.getController();
//                controller.setPatientRecord(tableView.getItems().get(idSelectedRow));
//                controller.uploadContent(rootObject, transitionGUI);
//            } catch (UIException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
