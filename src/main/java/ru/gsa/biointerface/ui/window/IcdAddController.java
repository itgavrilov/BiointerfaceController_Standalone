package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.services.IcdService;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdAddController extends AbstractWindow {
    private final IcdService icdService;
    @FXML
    private TextField nameField;
    @FXML
    private TextField versionField;
    @FXML
    private TextArea commentField;
    @FXML
    private Button addButton;

    public IcdAddController() throws NoConnectionException {
        icdService = IcdService.getInstance();
    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException(
                    "resourceSource or transitionGUI is null. First call setResourceAndTransition()"
            );

        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return ": add ICD";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void icdChange() {
        String str = nameField.getText().trim().replaceAll("\s.*", "").replaceAll("[^a-zA-Zа-яА-Я0-9.:]", "");
        if (str.length() > 16)
            str = str.substring(0, 16);

        nameField.setText(str);
        nameField.positionCaret(str.length());

        if (str.length() > 0) {
            nameField.setStyle(null);
            versionField.setDisable(false);
        } else {
            nameField.setStyle("-fx-background-color: red;");
            versionField.setDisable(true);
            commentField.setDisable(true);
            addButton.setDisable(true);
        }
    }

    public void versionChange() {
        String str = versionField.getText().trim().replaceAll("\s.*", "").replaceAll("[^0-9]", "");
        if (str.length() > 2)
            str = str.substring(0, 2);

        versionField.setText(str);
        versionField.positionCaret(str.length());
        if (str.length() > 0) {
            versionField.setStyle(null);
            commentField.setDisable(false);
            addButton.setDisable(false);
        } else {
            versionField.setStyle("-fx-background-color: red;");
            commentField.setDisable(true);
            addButton.setDisable(true);
        }
    }

    public void commentChange() {
        String str = commentField.getText().replaceAll("\"'", "");
        commentField.setText(str);
        commentField.positionCaret(str.length());
    }

    public void onAddButtonPush() {
        try {
            Icd icd = icdService.create(
                    nameField.getText(),
                    Integer.parseInt(versionField.getText()),
                    commentField.getText()
            );
            icdService.save(icd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onBackButtonPush();
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Icds.fxml").showWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
