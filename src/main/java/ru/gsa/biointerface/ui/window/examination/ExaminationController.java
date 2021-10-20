package ru.gsa.biointerface.ui.window.examination;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.GraphDAO;
import ru.gsa.biointerface.ui.UIException;
import ru.gsa.biointerface.ui.window.AbstractWindow;
import ru.gsa.biointerface.ui.window.WindowWithProperty;
import ru.gsa.biointerface.ui.window.graph.CheckBoxOfGraph;
import ru.gsa.biointerface.ui.window.graph.CompositeNode;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class ExaminationController extends AbstractWindow implements WindowWithProperty<Examination> {
    private final List<CompositeNode<AnchorPane, GraphController>> channelGUIs = new LinkedList<>();
    private final List<CheckBoxOfGraph> checkBoxesOfChannel = new LinkedList<>();
    private Examination examination;
    private double graphSize = 0;
    private double graphCapacity = 0;
    private double graphStart = 0;

    @FXML
    private AnchorPane anchorPaneControl;
    @FXML
    private Text patientRecordIdText;
    @FXML
    private Text secondNameText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text middleNameText;
    @FXML
    private Text birthdayText;
    @FXML
    private Text icdText;
    @FXML
    private Slider allSliderZoom;
    @FXML
    private Text idDeviceText;
    @FXML
    private VBox checkBoxOfChannelVBox;
    @FXML
    private Text dateTimeText;
    @FXML
    private TextArea commentField;
    @FXML
    private VBox channelVBox;
    @FXML
    private ScrollBar timeScrollBar;

    public WindowWithProperty<Examination> setProperty(Examination examination) {
        if (examination == null)
            throw new NullPointerException("examination is null");

        this.examination = examination;

        return this;
    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (examination == null)
            throw new UIException("examination is null. First call setParameter()");

        patientRecordIdText.setText(String.valueOf(examination.getPatientRecord().getId()));
        secondNameText.setText(examination.getPatientRecord().getSecondName());
        firstNameText.setText(examination.getPatientRecord().getFirstName());
        middleNameText.setText(examination.getPatientRecord().getMiddleName());
        if (examination.getPatientRecord().getIcd() != null)
            icdText.setText(examination.getPatientRecord().getIcd().toString());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthdayText.setText(examination.getPatientRecord().getBirthday().format(dateFormatter));

        idDeviceText.setText(String.valueOf(examination.getDevice().getId()));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        dateTimeText.setText(examination.getDateTime().format(dateTimeFormatter));
        timeScrollBar.setMin(0);
        timeScrollBar.setValue(0);
        timeScrollBar.setBlockIncrement(1);

        buildingChannelsGUIs();

        transitionGUI.show();
    }

    public void buildingChannelsGUIs() {
        List<GraphEntity> graphEntities;
        channelGUIs.clear();
        checkBoxesOfChannel.clear();

        try {
            graphEntities = GraphDAO.getInstance().getAllByExamination(examination.getEntity());

            graphCapacity = allSliderZoom.getValue();

            for (GraphEntity graphEntity : graphEntities) {
                CompositeNode<AnchorPane, GraphController> node =
                        new CompositeNode<>(new FXMLLoader(resourceSource.getResource("fxml/Graph.fxml")));
                GraphController graph = node.getController();

                graph.setGraphEntity(graphEntity);
                graph.setStart(0);
                graph.setCapacity((int) graphCapacity);

                if (graphSize > graph.getLengthGraphic() || graphSize == 0) {
                    graphSize = graph.getLengthGraphic();
                }
                channelGUIs.add(node);

                CheckBoxOfGraph checkBox = new CheckBoxOfGraph(graphEntity.getNumberOfChannel());
                checkBox.setText(graph.getName());
                checkBox.setOnAction(event -> {
                    node.getNode().setVisible(checkBox.isSelected());
                    drawChannelsGUI();
                });
                checkBoxesOfChannel.add(checkBox);
            }

            allSliderZoom.setMax(graphSize);
            timeScrollBar.setMax(graphSize - graphCapacity);
            timeScrollBar.setVisibleAmount(timeScrollBar.getMax() * graphCapacity / graphSize);

            allSliderZoom.valueProperty().addListener((ov, old_val, new_val) -> {
                graphCapacity = new_val.intValue();
                graphStart = timeScrollBar.getValue();

                if (graphStart > graphSize - graphCapacity) {
                    graphStart = graphSize - graphCapacity;
                    timeScrollBar.setValue(graphStart);
                }

                timeScrollBar.setMax(graphSize - graphCapacity);
                timeScrollBar.setVisibleAmount(timeScrollBar.getMax() * graphCapacity / graphSize);

                channelGUIs.forEach(o -> {
                    o.getController().setStart((int) graphStart);
                    o.getController().setCapacity((int) graphCapacity);
                });
            });

            timeScrollBar.valueProperty().addListener((ov, old_val, new_val) -> {
                graphStart = new_val.intValue();
                channelGUIs.forEach(o -> o.getController().setStart((int) graphStart));
            });

            drawChannelsGUI();
        } catch (PersistenceException | UIException e) {
            e.printStackTrace();
        }
    }

    public void drawChannelsGUI() {
        channelVBox.getChildren().clear();
        channelGUIs.forEach(n -> {
            if (n.getNode().isVisible())
                channelVBox.getChildren().add(n.getNode());
        });

        checkBoxOfChannelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().addAll(checkBoxesOfChannel);

        resizeWindow(anchorPaneRoot.getHeight(), anchorPaneRoot.getWidth());
    }

    public void onBack() {
        try {
            ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/PatientRecordOpen.fxml"))
                    .setProperty(examination.getPatientRecord())
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() {
        try {
            examination.setComment(commentField.getText());
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resizeWindow(double height, double width) {
        long count = channelGUIs.stream().
                map(CompositeNode::getNode).
                filter(Node::isVisible).count();

        double heightChannelGUIs = (height - timeScrollBar.getHeight()) / count;

        channelVBox.setPrefHeight(heightChannelGUIs);
        channelVBox.setPrefWidth(width - anchorPaneControl.getWidth());

        for (CompositeNode<AnchorPane, GraphController> o : channelGUIs) {
            o.getController().resizeWindow(heightChannelGUIs, width - anchorPaneControl.getWidth() + 13);
        }

    }

    @Override
    public String getTitleWindow() {
        return ": examination";
    }
}

