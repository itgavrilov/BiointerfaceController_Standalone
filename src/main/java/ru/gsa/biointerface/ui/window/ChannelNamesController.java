package ru.gsa.biointerface.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.services.ChannelNameService;

import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNamesController extends AbstractWindow {
    private final ChannelNameService channelNameService;
    private ChannelName channelName;
    @FXML
    private TableView<ChannelName> tableView;
    @FXML
    private TableColumn<ChannelName, String> nameCol;
    @FXML
    private TextArea commentField;
    @FXML
    private Button deleteButton;

    public ChannelNamesController() throws Exception {
        channelNameService = ChannelNameService.getInstance();
    }

    @Override
    public String getTitleWindow() {
        return ": channels";
    }

    @Override
    public void resizeWindow(double height, double width) {

    }

    @Override
    public void showWindow() throws Exception {
        if (resourceSource == null || transitionGUI == null)
            throw new NullPointerException("ResourceSource or transitionGUI is null. First call setResourceAndTransition()");

        ObservableList<ChannelName> channelNames = FXCollections.observableArrayList();
        channelNames.addAll(channelNameService.findAll());
        tableView.setItems(channelNames);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        transitionGUI.show();
    }

    public void onMouseClickedTableView() {
        if (channelName != tableView.getFocusModel().getFocusedItem()) {
            channelName = tableView.getFocusModel().getFocusedItem();
            commentField.setText(channelName.getComment());
            deleteButton.setDisable(false);
            commentField.setDisable(false);
        }
    }

    public void commentFieldChange() {
        if (Objects.equals(channelName.getComment(), commentField.getText())) {
            String comment = channelName.getComment();
            channelName.setComment(commentField.getText());
            try {
                channelNameService.save(channelName);
            } catch (Exception e) {
                channelName.setComment(comment);
                commentField.setText(comment);
                new AlertError("Error change comment for channel name: " + e.getMessage());
            }
        }
    }

    public void onBackButtonPush() {
        try {
            generateNewWindow("fxml/PatientRecords.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient records: " + e.getMessage());
        }
    }

    public void onAddButtonPush() {
        try {
            generateNewWindow("fxml/ChannelNameAdd.fxml").showWindow();
        } catch (Exception e) {
            new AlertError("Error load form for add channel name: " + e.getMessage());
        }
    }

    public void onDeleteButtonPush() {
        try {
            channelNameService.delete(channelName);
            tableView.getItems().remove(channelName);
            commentField.setText("");
        } catch (Exception e) {
            new AlertError("Error delete channel name: " + e.getMessage());
        }
    }
}
