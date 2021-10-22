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
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Graph;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.services.ServiceExamination;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.ui.UIException;
import ru.gsa.biointerface.ui.window.AbstractWindow;
import ru.gsa.biointerface.ui.window.WindowWithProperty;
import ru.gsa.biointerface.ui.window.graph.CheckBoxOfGraph;
import ru.gsa.biointerface.ui.window.graph.CompositeNode;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class ExaminationController extends AbstractWindow implements WindowWithProperty<Examination> {
    private ServiceExamination serviceExamination;
    private Examination examination;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final List<CompositeNode<AnchorPane, GraphController>> channelGUIs = new LinkedList<>();
    private final List<CheckBoxOfGraph> checkBoxesOfChannel = new LinkedList<>();
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
            throw new NullPointerException("serviceExamination is null");

        this.examination = examination;

        return this;
    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (examination == null)
            throw new UIException("serviceExamination is null. First call setParameter()");


        try {
            serviceExamination = ServiceExamination.getInstance();
            examination = serviceExamination.loadFromDatabaseWithGraphsById(examination.getId());
            idDeviceText.setText(String.valueOf(examination.getDevice().getId()));
            dateTimeText.setText(examination.getStartTimeInLocalDateTime().format(dateTimeFormatter));
            PatientRecord patientRecord = examination.getPatientRecord();
            patientRecordIdText.setText(String.valueOf(patientRecord.getId()));
            secondNameText.setText(patientRecord.getSecondName());
            firstNameText.setText(patientRecord.getFirstName());
            middleNameText.setText(patientRecord.getMiddleName());
            birthdayText.setText(patientRecord.getBirthdayInLocalDate().format(dateFormatter));

            if (patientRecord.getIcd() != null) {
                Icd icd = patientRecord.getIcd();
                icdText.setText(icd.getName() + " (ICD-" + icd.getVersion() + ")");
            } else {
                icdText.setText("-");
            }

            timeScrollBar.setMin(0);
            timeScrollBar.setValue(0);
            timeScrollBar.setBlockIncrement(1);
            buildingChannelsGUIs();
            transitionGUI.show();
        } catch (ServiceException e) {
            throw new UIException("Error connection to database", e);
        }
    }

    public void buildingChannelsGUIs() {
        List<Graph> graphs = examination.getGraphs();
        graphCapacity = allSliderZoom.getValue();

        for (Graph graph : graphs) {
            CompositeNode<AnchorPane, GraphController> node =
                    new CompositeNode<>(new FXMLLoader(resourceSource.getResource("fxml/Graph.fxml")));
            GraphController graphController = node.getController();
            graphController.setGraph(graph);
            graphController.setStart(0);
            graphController.setCapacity((int) graphCapacity);

            if (graphSize > graphController.getLengthGraphic() || graphSize == 0) {
                graphSize = graphController.getLengthGraphic();
            }

            channelGUIs.add(node);
            CheckBoxOfGraph checkBox = new CheckBoxOfGraph(graph.getNumberOfChannel());
            checkBox.setText(graphController.getName());
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
            //noinspection unchecked
            ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/PatientRecordOpen.fxml"))
                    .setProperty(examination.getPatientRecord())
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() {
        String comment = examination.getComment();
        if (Objects.equals(comment, commentField.getText())) {
            try {
                examination.setComment(commentField.getText());
                serviceExamination.update(examination);
            } catch (ServiceException e) {
                examination.setComment(comment);
                e.printStackTrace();
            }
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
        return ": serviceExamination";
    }
}

