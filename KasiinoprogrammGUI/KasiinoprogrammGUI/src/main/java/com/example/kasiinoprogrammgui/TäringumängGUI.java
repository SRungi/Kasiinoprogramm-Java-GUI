package com.example.kasiinoprogrammgui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TäringumängGUI {

    private HelloApplication app;
    private Kasutaja mangur;
    private Stage stage;

    public TäringumängGUI(HelloApplication app, Kasutaja mangur) {
        this.app = app;
        this.mangur = mangur;
        this.stage = app.stage;
    }

    public void naita() {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0a0a0f;");

        Label pealkiri = new Label("TÄRINGUMÄNG");
        pealkiri.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: gold;");

        Label seletus = new Label("Arva: kas number tuleb alla 50 või üle/võrdne 50?");
        seletus.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa;");

        // Arvu kuva
        Label arvLabel = new Label("?");
        arvLabel.setStyle(
                "-fx-font-size: 80px; -fx-font-weight: bold; " +
                        "-fx-text-fill: gold; -fx-min-width: 150; " +
                        "-fx-alignment: center;"
        );

        // Tulemus tekst
        Label tulemus = new Label("");
        tulemus.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        tulemus.setMinHeight(35);

        // Panus
        Label panusText = new Label("Panus:");
        panusText.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        TextField panusVali = new TextField("10");
        panusVali.setMaxWidth(100);
        panusVali.setStyle(
                "-fx-background-color: #1a1a2e; -fx-text-fill: white;" +
                        "-fx-border-color: gold; -fx-border-radius: 6;" +
                        "-fx-background-radius: 6; -fx-padding: 6 10;"
        );
        HBox panusRida = new HBox(10, panusText, panusVali);
        panusRida.setAlignment(Pos.CENTER);

        Label krediididLabel = new Label("Krediidid: " + String.format("%.2f", mangur.getKrediidid()));
        krediididLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gold;");

        // Nupud
        String nuppStiil =
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 12 30; -fx-cursor: hand;";

        Button alleNupp = new Button("⬇ ALLA 50");
        Button uleNupp = new Button("⬆ ÜLE/VÕRDNE 50");
        alleNupp.setStyle("-fx-background-color: #2980b9;" + nuppStiil);
        uleNupp.setStyle("-fx-background-color: #8e44ad;" + nuppStiil);
        alleNupp.setPrefWidth(200);
        uleNupp.setPrefWidth(200);

        HBox nupuRida = new HBox(20, alleNupp, uleNupp);
        nupuRida.setAlignment(Pos.CENTER);

        Button tagasi = new Button("Tagasi");
        tagasi.setStyle(
                "-fx-background-color: #333344; -fx-text-fill: white;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 8 20; -fx-cursor: hand;"
        );
        tagasi.setOnAction(e -> app.avaPeamenüü(stage));

        //Loogika kahe valiku jaoks
        alleNupp.setOnAction(e -> mangi(panusVali, arvLabel, tulemus, krediididLabel, "alla"));
        uleNupp.setOnAction(e ->  mangi(panusVali, arvLabel, tulemus, krediididLabel, "ule"));

        root.getChildren().addAll(
                pealkiri, seletus, arvLabel,
                tulemus, panusRida, krediididLabel,
                nupuRida, tagasi
        );

        //Selle jaoks, kui mäng avatakse säilitab ekraani suuruse:)
        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);

    }

    private void mangi(TextField panusVali, Label arvLabel,
                       Label tulemus, Label krediididLabel, String valik) {
        // Panuse kontroll
        double panus;
        try {
            panus = Double.parseDouble(panusVali.getText());
        } catch (NumberFormatException ex) {
            tulemus.setText("Sisesta sobiv number!");
            tulemus.setStyle("-fx-text-fill: #ff4444;");
            return;
        }
        if (panus <= 0) {
            tulemus.setText("Panus peab olema suurem kui 0!");
            tulemus.setStyle("-fx-text-fill: #ff4444;");
            return;
        }
        if (panus > mangur.getKrediidid()) {
            tulemus.setText("Pole piisavalt krediiti!");
            tulemus.setStyle("-fx-text-fill: #ff4444;");
            return;
        }

        // Genereeri arv 0-99
        int arv = (int)(Math.random() * 100);
        arvLabel.setText(String.valueOf(arv));
        mangur.setKrediidid(mangur.getKrediidid() - panus);

        // Võidu kontroll
        boolean voit = (valik.equals("alla") && arv < 50) ||
                (valik.equals("ule")  && arv >= 50);

        if (voit) {
            double voitSumma = panus * 2;
            mangur.setKrediidid(mangur.getKrediidid() + voitSumma);
            tulemus.setText("VÕITSID! " + String.format("%.2f", voitSumma));
            tulemus.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        } else {
            tulemus.setText("Kaotasid!");
            tulemus.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        }
        krediididLabel.setText("Krediidid: " + String.format("%.2f", mangur.getKrediidid()));
    }
}