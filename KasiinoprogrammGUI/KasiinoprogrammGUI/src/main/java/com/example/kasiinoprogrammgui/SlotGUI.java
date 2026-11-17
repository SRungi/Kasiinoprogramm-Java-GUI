package com.example.kasiinoprogrammgui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SlotGUI {
    private HelloApplication app;
    private Kasutaja mangur;
    private Stage stage;

    //Algsed lahrtrid
    private Label lahter1 = new Label("🍒");
    private Label lahter2 = new Label("🍒");
    private Label lahter3 = new Label("🍒");

    //Võimalikud sümbolid
    private String[] symbolid = {"🍒", "🍒", "🍒", "🍋", "🍋", "💵", "⭐", "💎"};

    private boolean mangKaib = false;

    public SlotGUI(HelloApplication app, Kasutaja mangur) {
        this.app = app;
        this.mangur = mangur;
        this.stage = app.stage;

    }

    public void naita() {
        VBox root = new VBox(20);
        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0a0a0f;");

        Label pealkiri = new Label("🎰 SLOT 🎰");
        pealkiri.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: gold;");

        HBox kastid = new HBox(15);
        kastid.setAlignment(Pos.CENTER);

        lahter1.setStyle(lahtriStiil());
        lahter2.setStyle(lahtriStiil());
        lahter3.setStyle(lahtriStiil());



        kastid.getChildren().addAll(lahter1, lahter2, lahter3);


        Label tulemus = new Label("");
        tulemus.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label panusText = new Label("Panus:");
        panusText.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        TextField panusVali = new TextField("10");
        panusVali.setMaxWidth(100);
        panusVali.setStyle(
                "-fx-background-color: #1a1a2e; -fx-text-fill: #ffffff;" +
                        "-fx-border-color: gold; -fx-border-radius: 6;" +
                        "-fx-background-radius: 6; -fx-padding: 6 10;"
        );

        HBox panusRida = new HBox(10, panusText, panusVali);
        panusRida.setAlignment(Pos.CENTER);

        //Krediidid
        Label krediididLabel = new Label("Krediidid: " + mangur.getKrediidid());
        krediididLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gold;");


        //Keerutame
        Button keerutaNupp = new Button("🎰 KEERUTA");
        keerutaNupp.setStyle( "-fx-background-color: #6a0dad; -fx-text-fill: white;" +
                        "-fx-font-size: 16px; -fx-font-weight: bold;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 12 30; -fx-cursor: hand;"
        );

        //Tagasi nupp
        Button tagasi = new Button("← Tagasi");
        tagasi.setStyle("-fx-background-color: #333344; -fx-text-fill: white;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 8 20; -fx-cursor: hand;"
        );
        tagasi.setOnAction(e -> app.avaPeamenüü(stage));

        //Animatsioon mille tegemiseks kasutasime tehisintellekti abi
        keerutaNupp.setOnAction(e -> {

            // Kui animatsioon juba käib, ei tee midagi
            if (mangKaib) return;

            // Panuse kontroll
            double panus;
            try {
                panus = Double.parseDouble(panusVali.getText());
            } catch (NumberFormatException ex) {
                tulemus.setText("Sisesta sobiv number!");
                tulemus.setStyle("-fx-font-size:16px; -fx-text-fill:#ff4444;");
                return;
            }
            if (panus <= 0 || panus > mangur.getKrediidid()) {
                tulemus.setText(panus <= 0 ? "Panus > 0!" : "Pole piisavalt krediiti!");
                tulemus.setStyle("-fx-font-size:16px; -fx-text-fill:#ff4444;");
                return;
            }

            // Võtame panuse maha
            mangur.setKrediidid(mangur.getKrediidid() - panus);
            krediididLabel.setText("Krediidid: " + String.format("%.2f", mangur.getKrediidid())); //%.2f on kaks komakohta formaat. 
            tulemus.setText("");
            mangKaib = true;
            keerutaNupp.setDisable(true);

            // Lõppsümbolid valitakse enne animatsiooni, et animatsioon "peatub" õigel sümbolil
            String loppsymbol1 = symbolid[(int)(Math.random() * symbolid.length)];
            String loppsymbol2 = symbolid[(int)(Math.random() * symbolid.length)];
            String loppsymbol3 = symbolid[(int)(Math.random() * symbolid.length)];


            //Kasutasime timeline ja keyframe osi, et luua animatsioon kus siis timeline ütleb ära mitme millisekundi tagant käivitab animatsioon järgmine keyframe animatsiooni sammu.

            Timeline animatsioon = new Timeline();

            // Mitu korda sümbolid vahetuvad enne peatumist
            int sammud = 20;

            for (int i = 0; i < sammud; i++) {

                final int samm = i; // peab olema final et lambdas kasutada

                KeyFrame kf = new KeyFrame(
                        Duration.millis(50 * i), // iga samm tuleb 50ms hiljem
                        event -> {

                            // Viimasel 3. sammul paneme lõppsümbolid paika
                            if (samm == sammud - 3) lahter1.setText(loppsymbol1);
                            if (samm == sammud - 2) lahter2.setText(loppsymbol2);
                            if (samm == sammud - 1) lahter3.setText(loppsymbol3);

                            // Muul ajal laseme lihtsalt suvalisi sümboleid.
                            if (samm < sammud - 3) lahter1.setText(juhusSymbol());
                            if (samm < sammud - 2) lahter2.setText(juhusSymbol());
                            if (samm < sammud - 1) lahter3.setText(juhusSymbol());
                        }
                );
                animatsioon.getKeyFrames().add(kf);
            }

            // Animatsiooni lõpp
            animatsioon.setOnFinished(event -> {

                mangKaib = false;
                keerutaNupp.setDisable(false);

                // Võidu kontroll
                if (loppsymbol1.equals(loppsymbol2) && loppsymbol2.equals(loppsymbol3)) {
                    double kordaja = switch (loppsymbol1) {
                        case "💎" -> 20;
                        case "⭐" -> 7;
                        case "💵" -> 3;
                        default   -> 2; // 🍒 ja 🍋
                    };
                    double voit = panus * kordaja;
                    mangur.setKrediidid(mangur.getKrediidid() + voit);
                    tulemus.setText("VÕITSID! " + String.format("%.2f", voit));
                    tulemus.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#2ecc71;");
                } else {
                    tulemus.setText("Kaotasid...");
                    tulemus.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#e74c3c;");
                }
                krediididLabel.setText("Krediidid: " + String.format("%.2f", mangur.getKrediidid())); //%.2f votab kaks komakohta
            });

            animatsioon.play(); // käivita animatsioon
        });

        root.getChildren().addAll(
                pealkiri, kastid, tulemus, panusRida,
                krediididLabel, keerutaNupp, tagasi
        );

        stage.setScene(scene);
    }

    // Abimeetodid
    private String juhusSymbol() {
        return symbolid[(int)(Math.random() * symbolid.length)];
    }

    private String lahtriStiil() {
        return "-fx-font-size: 60px; -fx-min-width: 110; -fx-min-height: 110;" +
                "-fx-alignment: center; -fx-background-color: #1a0a2e;" +
                "-fx-border-color: gold; -fx-border-width: 2;" +
                "-fx-border-radius: 10; -fx-background-radius: 10;";
    }
}