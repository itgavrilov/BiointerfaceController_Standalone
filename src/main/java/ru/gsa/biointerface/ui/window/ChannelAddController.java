package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.ui.UIException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelAddController extends AbstractWindow {
    @FXML
    private TextField channelField;
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
        return ": add channel";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    public void icdChange() {
        String str = channelField.getText().trim().replaceAll("\s.*", "").replaceAll("[^a-zA-Zа-яА-Я0-9.:]", "");
        if (str.length() > 16)
            str = str.substring(0, 16);

        channelField.setText(str);
        channelField.positionCaret(str.length());

        if (str.length() > 0) {
            channelField.setStyle(null);
            commentField.setDisable(false);
            addButton.setDisable(false);
        } else {
            channelField.setStyle("-fx-background-color: red;");
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
        Channel channel = new Channel(-1,
                channelField.getText(),
                commentField.getText()
        );

        try {
            channel.insert();
        } catch (DomainException e) {
            e.printStackTrace();
        }

        onBackButtonPush();
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Channels.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }
}
