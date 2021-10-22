package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.services.ServiceChannel;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.services.ServiceIcd;
import ru.gsa.biointerface.ui.UIException;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelsController extends AbstractWindow {
    private ServiceChannel serviceChannel;
    private Channel channel;
    @FXML
    private TableView<Channel> tableView;
    @FXML
    private TableColumn<Channel, String> nameCol;
    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;


    @Override
    public String getTitleWindow() {
        return ": channels";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("ResourceSource or transitionGUI is null. First call setResourceAndTransition()");

        try {
            serviceChannel = ServiceChannel.getInstance();
            ObservableList<Channel> icds = FXCollections.observableArrayList();
            icds.addAll(serviceChannel.getAll());
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            transitionGUI.show();
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
    }

    public void onMouseClickedTableView() {
        if (channel != tableView.getFocusModel().getFocusedItem()) {
            channel = tableView.getFocusModel().getFocusedItem();
            commentField.setText(channel.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        String comment = channel.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                channel.setComment(commentField.getText());
                serviceChannel.update(channel);
            } catch (ServiceException e) {
                channel.setComment(comment);
                e.printStackTrace();
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onAddButtonPush() {
        try {
            generateNewWindow("fxml/ChannelAdd.fxml").showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteButtonPush() {
        try {
            serviceChannel.delete(channel);
            tableView.getItems().remove(channel);
            commentField.setText("");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
