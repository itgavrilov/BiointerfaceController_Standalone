package ru.gsa.biointerfaceController_standalone.controllers.channel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public class CompositeNode<N extends Node, C> implements Comparable<CompositeNode<N, C>>{
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

    @Override
    public int compareTo(CompositeNode<N, C> o) {
        ContentComparable content = (ContentComparable) getController();
        ContentComparable contentO = (ContentComparable) o.getController();

        return content.compareTo(contentO);
    }
}
