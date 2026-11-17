package com.example.kasiinoprogrammgui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MustjaakGUI {

    private HelloApplication app;
    private Kasutaja mangija;

    private List<Integer> pakk = new ArrayList<>(); // kaardipakk

    private List<Integer> mangijaKaardid = new ArrayList<>(); // mängija käsi
    private List<Integer> dealerKaardid = new ArrayList<>(); // diileri käsi

    private double panus = 0; // mängija panus

    private Label krediididText;
    private Label mangijaText;
    private Label dealerText;
    private Label infoText;

    private Button hit;
    private Button stand;

    public MustjaakGUI(HelloApplication app, Kasutaja mangija) {
        this.app = app;
        this.mangija = mangija;
    }

    public void naita() {

        Stage stage = app.stage;

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0f1923;");
        //krediidid
        krediididText = new Label("EUR - " + mangija.getKrediidid());
        krediididText.setTextFill(Color.GOLD);
        krediididText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        //Panus
        TextField panusField = new TextField();
        panusField.setPromptText("Sisesta panus");
        panusField.setMaxWidth(200);
        // näitab mängija käsi
        mangijaText = new Label();
        mangijaText.setTextFill(Color.WHITE);
        //näitab diileri käsi
        dealerText = new Label();
        dealerText.setTextFill(Color.WHITE);
        //Info
        infoText = new Label();
        infoText.setTextFill(Color.LIME);

        Button alusta = new Button("ALUSTA");
        alusta.setStyle("-fx-background-color: gold; -fx-font-size: 18px;");

        hit = new Button("HIT");
        stand = new Button("STAND");
        // alguses ei saa kasutada hiti ja standi
        hit.setDisable(true);
        stand.setDisable(true);

        HBox tegevused = new HBox(15, hit, stand);
        tegevused.setAlignment(Pos.CENTER);
        // tagasi nupp
        Button tagasi = new Button("TAGASI");
        tagasi.setOnAction(e -> app.avaPeamenüü(stage));

        alusta.setOnAction(e -> {

            try { // panusekontroll
                panus = Double.parseDouble(panusField.getText());
            } catch (Exception ex) {
                infoText.setTextFill(Color.RED);
                infoText.setText("Vale number");
                return;
            }

            if (panus <= 0) { // ei saa negatiivseid ega nulli
                infoText.setTextFill(Color.RED);
                infoText.setText("Vale panus");
                return;
            }
             // ei saa rohkem kui kontol on
            if (panus > mangija.getKrediidid()) {
                infoText.setTextFill(Color.RED);
                infoText.setText("Pole piisavalt raha");
                return;
            }
            //krediidi jälgija uuendus
            mangija.setKrediidid(mangija.getKrediidid() - panus);
            krediididText.setText("EUR - " + mangija.getKrediidid());

            startGame();

            hit.setDisable(false);
            stand.setDisable(false);

            infoText.setTextFill(Color.WHITE);
            infoText.setText("Mäng algas!");

            uuendaTekst();

            // kui saab kohe esimese handiga blackjacki
            if (summa(mangijaKaardid) == 21) {

                infoText.setTextFill(Color.LIME);
                infoText.setText("BLACKJACK! Võitsid!");

                voit();
            }
        });
        // kui vajutatakse hit
        hit.setOnAction(e -> {

            mangijaKaardid.add(tombaKaart());
            uuendaTekst();

            if (summa(mangijaKaardid) > 21) {

                infoText.setTextFill(Color.RED);
                infoText.setText("Läksid lõhki!");

                lopp();
            }

            if (summa(mangijaKaardid) == 21) {

                infoText.setTextFill(Color.LIME);
                infoText.setText("21! Võitsid!");

                voit();
            }
        });

        stand.setOnAction(e -> {

            while (summa(dealerKaardid) < 17) { // diiler peab võtma kaardi kui tal on alla 17
                dealerKaardid.add(tombaKaart());
            }

            uuendaTekst(); // uuendab teksti

            //liidetakse kaardid kokku
            int m = summa(mangijaKaardid);
            int d = summa(dealerKaardid);

            if (d > 21) { // kui diiler on üle 21, siis kasutaja võidab

                infoText.setTextFill(Color.LIME);
                infoText.setText("Dealer läks lõhki!");

                voit();

            } else if (m > d) { // kui kasutajal rohkem kui diilerir siis kasutaja võidab

                infoText.setTextFill(Color.LIME);
                infoText.setText("Võitsid!");

                voit();

            } else if (d > m) { // kui diilerir rohkem kui kasutajal, siis diiler võitis

                infoText.setTextFill(Color.RED);
                infoText.setText("Kaotasid!");

                lopp();

            } else { // kui sama palju siis jääb viik

                infoText.setTextFill(Color.GOLD);
                infoText.setText("Viik!");

                mangija.setKrediidid(
                        mangija.getKrediidid() + panus
                );

                krediididText.setText(
                        "EUR - " + mangija.getKrediidid()
                );

                lopp();
            }
        });

        root.getChildren().addAll(
                krediididText,
                panusField,
                alusta,
                mangijaText,
                dealerText,
                tegevused,
                infoText,
                tagasi
        );

        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);
    }



    private void startGame() {

        pakk.clear();
        mangijaKaardid.clear();
        dealerKaardid.clear();

        for (int i = 2; i <= 10; i++) {
            for (int j = 0; j < 4; j++) {
                pakk.add(i);
            }
        }

        for (int i = 0; i < 16; i++) {
            pakk.add(10);
        }

        for (int i = 0; i < 4; i++) {
            pakk.add(11);
        }

        Collections.shuffle(pakk);

        mangijaKaardid.add(tombaKaart());
        mangijaKaardid.add(tombaKaart());

        dealerKaardid.add(tombaKaart());
        dealerKaardid.add(tombaKaart());
    }

    private int tombaKaart() { // kaardi võtmise jaoks
        int kaart = pakk.get(0);
        pakk.remove(0);
        return kaart;
    }

    private int summa(List<Integer> kaardid) { // arvutab kaardid kokku

        int s = 0;
        int aces = 0;

        for (int k : kaardid) {
            s += k;
            if (k == 11) aces++; // hoiab meeles mitu ässa handis on
        }

        while (s > 21 && aces > 0) { // kui summa on üle 21, siis äss = 1
            s -= 10;
            aces--;
        }

        return s;
    }

    private void uuendaTekst() { // teksti uuendamise jaoks

        mangijaText.setText(
                "Sinu: " + mangijaKaardid +
                        " | " + summa(mangijaKaardid)
        );

        dealerText.setText(
                "Dealer: " + dealerKaardid +
                        " | " + summa(dealerKaardid)
        );
    }

    private void voit() { // võidu abimeetod, panus korrutatakse 2ga, krediidinöidik muutub

        mangija.setKrediidid(
                mangija.getKrediidid() + panus * 2
        );

        krediididText.setText(
                "EUR - " + mangija.getKrediidid()
        );

        lopp();
    }

    private void lopp() { // mäng saab läbi
        hit.setDisable(true);
        stand.setDisable(true);
    }
}