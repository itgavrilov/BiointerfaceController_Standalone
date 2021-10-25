package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.exception.NoConnectionException;
import ru.gsa.biointerface.services.ChannelNameService;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameAddController extends AbstractWindow {
    private final ChannelNameService channelNameService;
    @FXML
    private TextField channelField;
    @FXML
    private TextArea commentField;
    @FXML
    private Button addButton;

    public ChannelNameAddController() throws NoConnectionException {
        channelNameService = ChannelNameService.getInstance();
    }

    @Override
    public void showWindow(){
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
        try {
            ChannelName channelName = channelNameService.create(
                    channelField.getText(),
                    commentField.getText()
            );
            channelNameService.save(channelName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onBackButtonPush();
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/Channels.fxml").showWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
