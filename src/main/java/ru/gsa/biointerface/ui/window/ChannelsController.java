package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.ui.UIException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelsController extends AbstractWindow {
    private Channel channelSelected;
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

        tableView.getItems().clear();
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<Channel> list = FXCollections.observableArrayList();
        try {
            list.addAll(Channel.getAll());
        } catch (DomainException e) {
            throw new UIException("Error getting a list of channels");
        }
        tableView.setItems(list);
        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (channelSelected != tableView.getFocusModel().getFocusedItem()) {
            channelSelected = tableView.getFocusModel().getFocusedItem();
            commentField.setText(channelSelected.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        try {
            channelSelected.setComment(commentField.getText());
        } catch (DomainException e) {
            e.printStackTrace();
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
            channelSelected.delete();
            tableView.getItems().remove(channelSelected);
            commentField.setText("");
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }
}
