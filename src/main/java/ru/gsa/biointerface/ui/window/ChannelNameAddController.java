package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.services.ChannelNameService;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameAddController extends AbstractWindow {
    private final ChannelNameService channelNameService;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea commentField;
    @FXML
    private Button addButton;

    public ChannelNameAddController() throws Exception {
        channelNameService = ChannelNameService.getInstance();
    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        transitionGUI.show();
    }

    @Override
    public String getTitleWindow() {
        return ": add channel";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void nameChange() {
        String str = nameField.getText()
                .replaceAll(" {2}.*", "")
                .replaceAll("[^a-zA-Zа-яА-Я0-9.:\s]", "");

        if (str.length() > 35)
            str = str.substring(0, 35);

        if (!nameField.getText().equals(str)) {
            nameField.setText(str);
            nameField.positionCaret(str.length());
        }

        if (str.equals(nameField.getText())) {
            nameField.setStyle(null);
            commentField.setDisable(false);
            addButton.setDisable(false);
        } else {
            nameField.setStyle("-fx-background-color: red;");
            commentField.setDisable(true);
            addButton.setDisable(true);
        }
    }

    public void commentChange() {
        String str = commentField.getText().replaceAll("\"'", "");
        //commentField.setText(str);
        //commentField.positionCaret(str.length());
    }

    public void onAddButtonPush() {
        try {
            ChannelName channelName = new ChannelName(
                    nameField.getText().trim(),
                    commentField.getText().trim()
            );
            channelNameService.save(channelName);
        } catch (Exception e) {
            new AlertError("Error create new channel name: " + e.getMessage());
        }

        onBackButtonPush();
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/ChannelNames.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load channel names: " + e.getMessage());
        }
    }
}
