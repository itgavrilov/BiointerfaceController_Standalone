package ru.gsa.biointerface.ui.window;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.services.ExaminationService;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ExaminationsController extends AbstractWindow {
    private final ExaminationService examinationService;
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private Examination examination;
    @FXML
    private TableView<Examination> tableView;
    @FXML
    private TableColumn<Examination, String> startTimeCol;
    @FXML
    private TableColumn<Examination, String> patientCol;
    @FXML
    private TableColumn<Examination, Integer> deviceIdCol;

    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;

    public ExaminationsController() throws Exception {
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
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        ObservableList<Examination> examinations = FXCollections.observableArrayList();
        examinations.addAll(examinationService.findAll());
        tableView.setItems(examinations);
        startTimeCol.setCellValueFactory(param -> new SimpleObjectProperty<>(
                dateTimeFormatter.format(param.getValue().getStarttime())
        ));
        patientCol.setCellValueFactory(param -> {
            Patient patient = param.getValue().getPatient();
            String initials = patient.getSecondName() + " " +
                    patient.getFirstName().charAt(0) + ".";
            if (!"".equals(patient.getPatronymic()))
                initials += patient.getPatronymic().charAt(0) + ".";

            return new SimpleObjectProperty<>(initials);
        });
        deviceIdCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDevice().getId()));
        transitionGUI.show();
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

    public void commentFieldChange() {
        if (Objects.equals(examination.getComment(), commentField.getText())) {
            String comment = examination.getComment();
            examination.setComment(commentField.getText());
            try {
                examinationService.save(examination);
            } catch (Exception e) {
                examination.setComment(comment);
                commentField.setText(comment);
                new AlertError("Error change comment for examination: " + e.getMessage());
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Patients.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient records: " + e.getMessage());
        }
    }

    public void onDeleteButtonPush() {
        try {
            examinationService.delete(examination);
            tableView.getItems().remove(examination);
            commentField.setText("");
        } catch (Exception e) {
            new AlertError("Error delete examination: " + e.getMessage());
        }
    }
}
