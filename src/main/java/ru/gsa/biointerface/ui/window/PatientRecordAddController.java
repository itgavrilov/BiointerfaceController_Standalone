package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Icds;
import ru.gsa.biointerface.domain.PatientRecords;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.ui.UIException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientRecordAddController extends AbstractWindow {
    @FXML
    private TextField externalIDField;
    @FXML
    private TextField secondNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField middleNameField;
    @FXML
    private DatePicker birthdayField;
    @FXML
    private ComboBox<Icd> icdComboBox;
    @FXML
    private TextArea commentField;
    @FXML
    private Button registerAndOpenButton;

    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        icdComboBox.setConverter(Icd.converter);

        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return ": new patient record";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void idFieldChange() {
        String str = externalIDField.getText()
                .trim()
                .replaceAll("\s.*", "")
                .replaceAll("[^0-9]", "");

        if (str.length() > 35)
            str = str.substring(0, 35);

        if (!externalIDField.getText().equals(str)) {
            externalIDField.setText(str);
            externalIDField.positionCaret(str.length());
        }

        if (str.length() > 0) {
            secondNameField.setDisable(false);
            externalIDField.setStyle(null);
        } else {
            externalIDField.setStyle("-fx-background-color: red;");
            secondNameField.setDisable(true);
            firstNameField.setDisable(true);
            middleNameField.setDisable(true);
            birthdayField.setDisable(true);
            icdComboBox.setDisable(true);
            icdComboBox.getItems().clear();
            commentField.setDisable(true);
            registerAndOpenButton.setDisable(true);
        }

    }

    public void secondNameFieldChange() {
        String str = firstUpperCase(
                secondNameField.getText()
                        .trim()
                        .replaceAll("\s.*", "")
                        .replaceAll("[^a-zA-Zа-яА-Я]", "")
        );

        if (str.length() > 35)
            str = str.substring(0, 35);

        if (!secondNameField.getText().equals(str)) {
            secondNameField.setText(str);
            secondNameField.positionCaret(str.length());
        }

        if (str.length() > 0) {
            firstNameField.setDisable(false);
            middleNameField.setDisable(false);
            secondNameField.setStyle(null);
        } else {
            secondNameField.setStyle("-fx-background-color: red;");
            firstNameField.setDisable(true);
            middleNameField.setDisable(true);
            birthdayField.setDisable(true);
            icdComboBox.setDisable(true);
            icdComboBox.getItems().clear();
            commentField.setDisable(true);
            registerAndOpenButton.setDisable(true);
        }
    }

    public void firstNameFieldChange() {
        String str = firstUpperCase(
                firstNameField.getText()
                        .trim()
                        .replaceAll("\s.*", "")
                        .replaceAll("[^a-zA-Zа-яА-Я]", "")
        );
        if (str.length() > 35)
            str = str.substring(0, 35);

        if (!firstNameField.getText().equals(str)) {
            firstNameField.setText(str);
            firstNameField.positionCaret(str.length());
        }

        if (str.length() > 0) {
            birthdayField.setDisable(false);
            firstNameField.setStyle(null);
        } else {
            firstNameField.setStyle("-fx-background-color: red;");
            birthdayField.setDisable(true);
            icdComboBox.setDisable(true);
            icdComboBox.getItems().clear();
            commentField.setDisable(true);
            registerAndOpenButton.setDisable(true);
        }
    }

    public void middleNameFieldChange() {
        String str = firstUpperCase(
                middleNameField.getText()
                        .trim()
                        .replaceAll("\s.*", "")
                        .replaceAll("[^a-zA-Zа-яА-Я]", "")
        );
        if (str.length() > 35)
            str = str.substring(0, 35);

        if (!middleNameField.getText().equals(str)) {
            middleNameField.setText(str);
            middleNameField.positionCaret(str.length());
        }
    }

    public void birthdayFieldChange() {
        LocalDate tmp = birthdayField.getValue();

        if (tmp != null) {
            icdComboBox.setDisable(false);
            icdComboBox.getItems().add(null);
            commentField.setDisable(false);
            registerAndOpenButton.setDisable(false);
            birthdayField.setStyle(null);
            setIcdComboBox();
        } else {
            birthdayField.setStyle("-fx-background-color: red;");
            icdComboBox.setDisable(true);
            icdComboBox.getItems().clear();
            commentField.setDisable(true);
            registerAndOpenButton.setDisable(true);
        }
    }

    public void birthdayTextFieldChange() {
        String str = birthdayField.getEditor().getText().trim()
                .replaceAll("[^0-9.\\-:_]", "")
                .replaceAll("[-:_]+", ".");

        if (!birthdayField.getEditor().getText().equals(str)) {
            birthdayField.getEditor().setText(str);
            birthdayField.getEditor().positionCaret(str.length());
        }
        try {
            birthdayField.setValue(LocalDate.parse(str, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            icdComboBox.setDisable(false);
            setIcdComboBox();
            commentField.setDisable(false);
            registerAndOpenButton.setDisable(false);
            birthdayField.setStyle(null);
        } catch (Exception e) {
            birthdayField.setStyle("-fx-background-color: red;");
            icdComboBox.setDisable(true);
            icdComboBox.getItems().clear();
            commentField.setDisable(true);
            registerAndOpenButton.setDisable(true);
        }
    }

    private void setIcdComboBox() {
        ObservableList<Icd> list = FXCollections.observableArrayList();
        try {
            list.addAll(Icds.getSetAll());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        icdComboBox.getItems().addAll(list);
    }

    public void commentFieldChange() {
        String str = commentField.getText().replaceAll("\"'", "");
        commentField.setText(str);
        commentField.positionCaret(str.length());
    }

    public void onAdd() {
        PatientRecord patientRecord = new PatientRecord(
                Integer.parseInt(externalIDField.getText()),
                secondNameField.getText(),
                firstNameField.getText(),
                middleNameField.getText(),
                birthdayField.getValue(),
                icdComboBox.getValue(),
                commentField.getText()
        );
        try {
            PatientRecords.insert(patientRecord);
        } catch (DomainException e) {
            e.printStackTrace();
        }

        onBack();
    }

    public void onBack() {
        try {
            generateNewWindow("PatientRecords.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public String firstUpperCase(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
