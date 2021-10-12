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
    int idSelectedRow = -1;

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
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");

        tableView.getItems().clear();
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<Channel> list = FXCollections.observableArrayList();
        try {
            list.addAll(Channel.getAll());
        } catch (DomainException e) {
            throw new UIException("Error getting a list of channels", e);
        }
        tableView.setItems(list);
        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (idSelectedRow != tableView.getFocusModel().getFocusedCell().getRow()) {
            idSelectedRow = tableView.getFocusModel().getFocusedCell().getRow();
            commentField.setText(
                    tableView.getItems().get(idSelectedRow).getComment()
            );
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        if (!commentField.getText().equals(tableView.getItems().get(idSelectedRow).getComment())) {
            Channel channel = tableView.getItems().get(idSelectedRow);
            channel.setComment(commentField.getText());
            try {
                channel.update();
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("/fxml/PatientRecords.fxml").showWindow();
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
        Channel channel = tableView.getItems().get(idSelectedRow);
        try {
            channel.delete();
        } catch (DomainException e) {
            e.printStackTrace();
        }
        commentField.setText("");
        tableView.getItems().remove(idSelectedRow);
        idSelectedRow = -1;
    }
}
