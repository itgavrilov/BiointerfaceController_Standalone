package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Icd;
import ru.gsa.biointerface.ui.UIException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdAddController extends AbstractWindow {
    @FXML
    private TextField icdField;
    @FXML
    private TextField versionField;
    @FXML
    private TextArea commentField;
    @FXML
    private Button addButton;

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

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
        String str = icdField.getText().trim().replaceAll("\s.*", "").replaceAll("[^a-zA-Zа-яА-Я0-9.:]", "");
        if (str.length() > 16)
            str = str.substring(0, 16);

        icdField.setText(str);
        icdField.positionCaret(str.length());

        if (str.length() > 0) {
            icdField.setStyle(null);
            versionField.setDisable(false);
        } else {
            icdField.setStyle("-fx-background-color: red;");
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

    public void onAdd() {
        Icd icd = new Icd(-1,
                icdField.getText(),
                Integer.parseInt(versionField.getText()),
                commentField.getText()
        );

        try {
            icd.insert();
        } catch (DomainException e) {
            e.printStackTrace();
        }

        onBack();
    }

    public void onBack() {
        try {
            generateNewWindow("Icds.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }
}
