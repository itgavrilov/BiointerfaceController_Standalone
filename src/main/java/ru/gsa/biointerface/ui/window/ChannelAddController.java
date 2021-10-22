package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.services.ServiceChannel;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.ui.UIException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelAddController extends AbstractWindow {
    private ServiceChannel serviceChannel;
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

        try {
            serviceChannel = ServiceChannel.getInstance();
            transitionGUI.show();
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
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
        Channel channel = serviceChannel.create(
                channelField.getText(),
                commentField.getText()
        );

        try {
            serviceChannel.save(channel);
            onBackButtonPush();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Channels.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }
}
