package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.services.IcdService;
import ru.gsa.biointerface.services.PatientRecordService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordAddController extends AbstractWindow {
    private final PatientRecordService patientRecordService;
    private final StringConverter<Icd> converter = new StringConverter<>() {
        @Override
        public String toString(Icd icd) {
            String str = "";
            if (icd != null)
                str = icd.getName() + " (ICD-" + icd.getVersion() + ")";
            return str;
        }

        @Override
        public Icd fromString(String string) {
            return null;
        }
    };
    private IcdService icdService;
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

    public PatientRecordAddController() throws Exception {
        patientRecordService = PatientRecordService.getInstance();
    }

    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        icdService = IcdService.getInstance();
        icdComboBox.setConverter(converter);
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
                .replaceAll("\s.*", "")
                .replaceAll("[^0-9]", "");

        if (str.length() > 35)
            str = str.substring(0, 35);

        if (str.equals(externalIDField.getText())) {
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
                        .replaceAll("\s.*", "")
                        .replaceAll("[^a-zA-Zа-яА-Я]", "")
        );

        if (str.length() > 35)
            str = str.substring(0, 35);

        if (str.equals(secondNameField.getText())) {
            firstNameField.setDisable(false);
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
                        .replaceAll("\s.*", "")
                        .replaceAll("[^a-zA-Zа-яА-Я]", "")
        );
        if (str.length() > 35)
            str = str.substring(0, 35);

        if (str.equals(firstNameField.getText())) {
            middleNameField.setDisable(false);
            birthdayField.setDisable(false);
            firstNameField.setStyle(null);
        } else {
            firstNameField.setStyle("-fx-background-color: red;");
            middleNameField.setDisable(true);
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
                        .replaceAll("\s.*", "")
                        .replaceAll("[^a-zA-Zа-яА-Я]", "")
        );
        if (str.length() > 35)
            str = str.substring(0, 35);

        if (str.equals(middleNameField.getText())) {
            birthdayField.setDisable(false);
            icdComboBox.setDisable(false);
            middleNameField.setStyle(null);
            commentField.setDisable(false);
            registerAndOpenButton.setDisable(false);
        } else {
            middleNameField.setStyle("-fx-background-color: red;");
            birthdayField.setDisable(true);
            icdComboBox.setDisable(true);
            icdComboBox.getItems().clear();
            commentField.setDisable(true);
            registerAndOpenButton.setDisable(true);
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
        String str = birthdayField.getEditor().getText()
                .replaceAll("[^0-9.\\-:_]", "")
                .replaceAll("[-:_]+", ".");

        if (str.length() == 2 || str.length() == 5)
            str = str + ".";

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
        ObservableList<Icd> icds = FXCollections.observableArrayList();
        try {
            List<Icd> icdList = icdService.getAll();
            icds.add(null);
            icds.addAll(icdList);
        } catch (Exception e) {
            new AlertError("Error load list ICDs: " + e.getMessage());
        }
        icdComboBox.getItems().clear();
        icdComboBox.getItems().addAll(icds);
    }

    public void commentFieldChange() {
        String str = commentField.getText().replaceAll("\"'", "");
        commentField.setText(str);
        commentField.positionCaret(str.length());
    }

    public void onAddButtonPush() {
        try {
            PatientRecord patientRecord = new PatientRecord(
                    Integer.parseInt(externalIDField.getText().trim()),
                    secondNameField.getText().trim(),
                    firstNameField.getText().trim(),
                    middleNameField.getText().trim(),
                    localDateToDate(birthdayField.getValue()),
                    icdComboBox.getValue(),
                    commentField.getText().trim()
            );
            patientRecordService.save(patientRecord);
        } catch (Exception e) {
            new AlertError("Error create new patient record: " + e.getMessage());
        }
        onBackButtonPush();
    }

    private static Calendar localDateToDate(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        //noinspection MagicConstant
        calendar.set(
                localDate.getYear(),
                localDate.getMonthValue() - 1,
                localDate.getDayOfMonth()
        );

        return calendar;
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient records: " + e.getMessage());
        }
    }

    public String firstUpperCase(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
