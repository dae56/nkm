package ru.dae56.nkm;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Toast {
    public static void makeText(VBox root, String message, String type) {
        Label toastLabel = new Label(message);
        toastLabel.getStyleClass().add("toast-" + type.toLowerCase());

        // Стили для позиционирования
        toastLabel.setStyle(
                "-fx-alignment: center; " +
                        "-fx-pref-width: 300px; " +
                        "-fx-pref-height: 50px;"
        );

        root.getChildren().add(toastLabel);

        // Анимация
        toastLabel.setOpacity(0);
        toastLabel.setTranslateY(-50);

        Timeline showAnimation = new Timeline(
                new KeyFrame(Duration.millis(300), e -> {
                    toastLabel.setTranslateY(0);
                    toastLabel.setOpacity(1);
                })
        );

        Timeline hideAnimation = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    Timeline fadeOut = new Timeline(
                            new KeyFrame(Duration.millis(300), ev -> {
                                toastLabel.setTranslateY(-50);
                                toastLabel.setOpacity(0);
                            })
                    );
                    fadeOut.setOnFinished(event -> root.getChildren().remove(toastLabel));
                    fadeOut.play();
                })
        );

        showAnimation.play();
        hideAnimation.play();
    }
}
