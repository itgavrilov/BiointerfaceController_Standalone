package servo;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static java.lang.Math.abs;

public final class Servo extends AnchorPane {
    private boolean isReady = false;
    private Separator separator;
    private Label label;
    private Label amperage;
    private Slider sliderPositionIndicator;

    public Servo(int i){
        separator = buildSeparator();
        setLeftAnchor(separator, 0.0);
        setBottomAnchor(separator, 0.0);
        setRightAnchor(separator, 0.0);

        label = buildLabel(i);
        setTopAnchor(label, 10.0);
        setLeftAnchor(label, 30.0);
        setRightAnchor(label, 30.0);

        amperage = builAmperage();
        setTopAnchor(amperage, 60.0);
        setLeftAnchor(amperage, 20.0);
        setBottomAnchor(amperage, 50.0);

        sliderPositionIndicator = buildSlider();
        setTopAnchor(sliderPositionIndicator, 40.0);
        setRightAnchor(sliderPositionIndicator, 20.0);
        setBottomAnchor(sliderPositionIndicator, 10.0);

        setPrefWidth(150);

        building();
    }


    private void building(){
        getChildren().addAll(separator, label, amperage, sliderPositionIndicator);
        isReady = true;
    }

    private Separator buildSeparator() {
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        separator.setMinWidth(1);
        separator.setMaxWidth(1);
        separator.setPrefWidth(1);

        return separator;
    }

    private Label builAmperage(){
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(new Font("System", 24));
        label.setText("A:0");

        return label;
    }
    private Label buildLabel(int i){
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(new Font("System", 18));
        label.setText("servo "+i);

        return label;
    }
    private Slider buildSlider() {
        Slider slider = new Slider();
        slider.setValue(0);
        slider.setBlockIncrement(1);
        slider.setLayoutX(20);
        slider.setLayoutY(20);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setMin(0);
        slider.setMax(4095);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setDisable(true);
        return slider;
    }

    public void setData(int position, int amperage){
        isReady = false;
        Platform.runLater(() -> {
            sliderPositionIndicator.setValue(abs(position));
            this.amperage.setText("A:"+ abs(amperage));
            isReady = true;
        });
    }
    public boolean isReady() {
        return isReady;
    }
}
