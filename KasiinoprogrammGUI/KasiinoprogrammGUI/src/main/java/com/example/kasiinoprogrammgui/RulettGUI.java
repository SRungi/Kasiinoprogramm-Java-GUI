package com.example.kasiinoprogrammgui;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RulettGUI {

    private HelloApplication app;
    private Kasutaja mangija;

    public RulettGUI(HelloApplication app, Kasutaja mangija) {

        this.app = app;
        this.mangija = mangija;

    }

    public void naita() {


        Stage stage = app.stage;

        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color: #0f1923;");

        VBox main = new VBox(20);
        main.setAlignment(Pos.CENTER);
        //krediidid
        Label krediidid = new Label(
                "EUR - " + mangija.getKrediidid()
        );

        krediidid.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );
        // panuse sisestus
        TextField panusField = new TextField();
        panusField.setPromptText("Sisesta panus");
        panusField.setMaxWidth(200);

        Label tulemusText = new Label(); // võit või kaotus
        // ruletti rida
        HBox rida = new HBox(5);

        for (int i = 0; i < 60; i++) { // tehakse 60 ruutu

            StackPane kast = new StackPane();

            Rectangle rect = new Rectangle(80, 80);

            String varv;

            // iga 15. roheline
            if (i % 15 == 0) {

                varv = "GREEN";

            } else {
                // must ja punane on vaheldumisi
                if (i % 2 == 0) {

                    varv = "RED";

                } else {

                    varv = "BLACK";
                }
            }
            // kasti värvimine
            if (varv.equals("RED")) {

                rect.setFill(Color.RED);

            } else if (varv.equals("BLACK")) {

                rect.setFill(Color.BLACK);

            } else {

                rect.setFill(Color.GREEN);
            }
            // kasti mõõted
            rect.setArcWidth(15);
            rect.setArcHeight(15);

            Label text = new Label(varv); // kastile tekst, see mis värvi ta on kas RED, Black, Green

            text.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
            );

            kast.getChildren().addAll(rect, text); // lisatakse kasti

            rida.getChildren().add(kast); // rida saab selle kasti nüüd
        }

        Pane clipPane = new Pane(); // aitab näha selle rea keskosa, mitte tervet 60 kasti
        clipPane.setPrefSize(700, 120);

        rida.setLayoutY(20);

        clipPane.getChildren().add(rida);
        // marker, mis näitab mis kasti peale langeti
        Rectangle marker = new Rectangle(4, 120);
        marker.setFill(Color.GOLD);
        StackPane rouletteArea = new StackPane();
        StackPane.setAlignment(marker, Pos.CENTER);
        rouletteArea.getChildren().addAll(
                clipPane,
                marker
        );
        // valikud, kas punane, must, roheline
        ToggleGroup group = new ToggleGroup();

        RadioButton red = new RadioButton("Punane");
        RadioButton black = new RadioButton("Must");
        RadioButton green = new RadioButton("Roheline");

        red.setTextFill(Color.WHITE);
        black.setTextFill(Color.WHITE);
        green.setTextFill(Color.WHITE);

        red.setToggleGroup(group);
        black.setToggleGroup(group);
        green.setToggleGroup(group);

        red.setSelected(true);

        HBox valikud = new HBox(15);

        valikud.setAlignment(Pos.CENTER);

        valikud.getChildren().addAll(
                red,
                black,
                green
        );
        // spin nupp
        Button spin = new Button("SPIN");

        spin.setStyle(
                "-fx-background-color: gold;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        // tagasi nupp
        Button tagasi = new Button ("Tagasi");

        tagasi.setOnAction(e -> {

            app.avaPeamenüü(stage);

        });

        spin.setOnAction(e -> {

            double panus;

            try { // panusekontroll

                panus =
                        Double.parseDouble(panusField.getText());

            } catch (Exception ex) {

                tulemusText.setTextFill(Color.RED);

                tulemusText.setText(
                        "Sisesta korrektne number"
                );

                return;
            }

            if (panus <= 0) { // ei saa 0 ega negatiivseid

                tulemusText.setTextFill(Color.RED);

                tulemusText.setText("Vale panus");

                return;
            }

            if (panus > mangija.getKrediidid()) { // kui panus suurem kui kontol raha

                tulemusText.setTextFill(Color.RED);

                tulemusText.setText(
                        "Pole piisavalt krediite"
                );

                return;
            }

            // uuendatakse krediidi näidikut
            mangija.setKrediidid(
                    mangija.getKrediidid() - panus
            );
            krediidid.setText(
                    "EUR - " + mangija.getKrediidid()
            );

            // kasti rida pannakse algusesse
            rida.setTranslateX(0);

            // rea spinnimine
            TranslateTransition transition =
                    new TranslateTransition();
            transition.setNode(rida);
            transition.setDuration(Duration.seconds(5));
            transition.setByX(-2500); // jookseb vasakult paremale
            transition.setInterpolator(
                    Interpolator.EASE_OUT
            );
            transition.play();

            // kui spinn lõppeb
            transition.setOnFinished(event -> {

                double center = 450; // keskpunkt

                // kontroll, mis kast jäi keskele alla
                for (Node node : rida.getChildren()) {
                    // võtab ruudu asukoha ja mõõtmed ekraanil
                    Bounds bounds =
                            node.localToScene(
                                    node.getBoundsInLocal()
                            );

                    double x = bounds.getMinX();
                    double width = bounds.getWidth();

                    //kui kast on keskel
                    if (x <= center &&
                            x + width >= center) {

                        StackPane kast =
                                (StackPane) node;

                        Label label = // annab selle, mis värv kast oli
                                (Label) kast.getChildren().get(1);

                        String tulemus =
                                label.getText(); // mida kasutaja valis

                        String valitudVarv;

                        if (red.isSelected()) {

                            valitudVarv = "RED";

                        } else if (black.isSelected()) {

                            valitudVarv = "BLACK";

                        } else {

                            valitudVarv = "GREEN";
                        }

                        if (tulemus.equals(valitudVarv)) {

                            double voidusumma;

                            if (tulemus.equals("GREEN")) {

                                voidusumma = panus * 14;

                            } else {

                                voidusumma = panus * 2;
                            }

                            mangija.setKrediidid(
                                    mangija.getKrediidid()
                                            + voidusumma
                            );

                            tulemusText.setTextFill(Color.LIME);

                            tulemusText.setText(
                                    "VÕITSID! Tuli "
                                            + tulemus
                                            + " +" + voidusumma
                            );

                        } else {

                            tulemusText.setTextFill(Color.RED);

                            tulemusText.setText(
                                    "Kaotasid! Tuli "
                                            + tulemus
                            );
                        }

                        krediidid.setText(
                                "EUR - "
                                        + mangija.getKrediidid()
                        );

                        break;
                    }
                }
            });
        });

        main.getChildren().addAll(
                krediidid,
                valikud,
                rouletteArea,
                panusField,
                spin,
                tulemusText,
                tagasi
        );

        root.setCenter(main);

        Scene scene =
                new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());

        stage.setScene(scene);
    }
}