package ru.gsa.biointerface.ui.window.ExaminationNew;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public class CompositeNode<N extends Node, C> {
    private final N node;
    private final C controller;

    public CompositeNode(FXMLLoader loader) {
        try {
            this.node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException("node is null");
        }
        controller = loader.getController();
    }

    public N getNode() {
        return node;
    }

    public C getController() {
        return controller;
    }
}
