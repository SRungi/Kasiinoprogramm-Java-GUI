package com.example.kasiinoprogrammgui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MineGUI {

    private HelloApplication app;
    private Kasutaja mangija;

    public MineGUI(HelloApplication app, Kasutaja mangija) {
        this.app = app; // et saaks peamenüüsse tagasi
        this.mangija = mangija; // kannaks mängija andmeid edasi
    }

    public void naita() {

        Stage stage = app.stage; // võtab akna

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0f1923;");

        //krediidid
        Label krediidid = new Label("EUR - " + mangija.getKrediidid());
        krediidid.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        TextField panusField = new TextField();
        panusField.setPromptText("Sisesta panus");
        panusField.setMaxWidth(200);

        Label voidusummaText = new Label("Võidusumma: 0");
        voidusummaText.setTextFill(Color.LIME);

        Label info = new Label();
        info.setTextFill(Color.WHITE);

        // 4x4 mängulaud
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Button[][] nupud = new Button[4][4]; // nuppude jaoks
        boolean[][] pommid = new boolean[4][4]; // pommide jaoks

        final double[] panus = {0};
        final double[] voidusumma = {0};
        final boolean[] kaotas = {false};
        final int[] safeCount = {0};

        // Mängu algus
        Button alusta = new Button("ALUSTA");

        alusta.setOnAction(e -> {
            // panuse kontroll
            try {
                panus[0] = Double.parseDouble(panusField.getText());
            } catch (Exception ex) {
                info.setTextFill(Color.RED);
                info.setText("Sisesta korrektne number");
                return;
            }
            // panus peab olema väiksem võrdne konto krediiditest, samuti ei tohi olla negatiivne arv
            if (panus[0] <= 0 || panus[0] > mangija.getKrediidid()) {
                info.setTextFill(Color.RED);
                info.setText("Vale panus");
                return;
            }
            // Mängijalt võetakse panus ära ja krediidid jälgija uuendatakse
            mangija.setKrediidid(mangija.getKrediidid() - panus[0]);
            krediidid.setText("EUR - " + mangija.getKrediidid());


            voidusumma[0] = panus[0]; // algsumma, mida hakatakse iga õige ruudu puhul suurendama
            kaotas[0] = false; // kui on false, siis mängija pole veel kaotanud
            safeCount[0] = 0; // klikkide arv

            voidusummaText.setText("Võidusumma: " + voidusumma[0]);

            info.setTextFill(Color.WHITE);
            info.setText("Mäng algas!");

            // reset pommid
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    pommid[i][j] = false;
                }
            }

            // 4 pommi tehakse mängu väljale
            int placed = 0;
            while (placed < 4) {
                int r = (int) (Math.random() * 4);
                int c = (int) (Math.random() * 4);

                if (!pommid[r][c]) { // kui selles kohas pole veel pommi, saab pommi lisada
                    pommid[r][c] = true;
                    placed++;
                }
            }

            // reset nupud
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    nupud[i][j].setDisable(false);
                    nupud[i][j].setText("?");
                    nupud[i][j].setStyle("-fx-font-size: 24px;");
                }
            }
        });

        // Luuakse nupud
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                Button nupp = new Button("?");
                nupp.setPrefSize(100, 100);

                int r = i;
                int c = j;

                nupp.setOnAction(e -> {

                    if (panus[0] == 0) {
                        info.setTextFill(Color.RED);
                        info.setText("Pane enne panus!");
                        return;
                    }

                    if (kaotas[0]) return; // kui on juba kaotatud

                    if (pommid[r][c]) { // kui on pomm

                        kaotas[0] = true; // mängija kaotas

                        nupp.setText("X");
                        nupp.setStyle("-fx-background-color: red; -fx-text-fill: white;");

                        info.setTextFill(Color.RED);
                        info.setText("LÄKSID ÕHKU!");

                        // näita kõik tile’id
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 4; y++) {

                                if (pommid[x][y]) {
                                    nupud[x][y].setText("X");
                                    nupud[x][y].setStyle("-fx-background-color: red;");
                                } else {
                                    nupud[x][y].setText("O");
                                    nupud[x][y].setStyle("-fx-background-color: lime;");
                                }

                                nupud[x][y].setDisable(true); // teeb nii, et nuppe ei saa enam vajutada
                            }
                        }

                    } else { // Kui valiti safe tile

                        nupp.setText("O");
                        nupp.setStyle("-fx-background-color: lime; -fx-text-fill: black;");
                        nupp.setDisable(true);

                        safeCount[0]++; // loetakse safe tile
                        voidusumma[0] *= 1.15;

                        voidusummaText.setText( // uuendatakse hetkest võidusummat
                                "Võidusumma: " + String.format("%.2f", voidusumma[0])
                        );

                        info.setTextFill(Color.LIME);
                        info.setText("+15% SAFE");

                        // WIN
                        if (safeCount[0] == 12) { // kui kõik safeid on saadud, siis väljastatakse näidatakse neid tile ka kus olid pommid

                            info.setTextFill(Color.GOLD);
                            info.setText("KÕIK SAFE! VÕITSID!");

                            mangija.setKrediidid(
                                    mangija.getKrediidid() + voidusumma[0]
                            );

                            krediidid.setText("EUR - " + mangija.getKrediidid());

                            for (int x = 0; x < 4; x++) {
                                for (int y = 0; y < 4; y++) {
                                    nupud[x][y].setDisable(true);
                                }
                            }
                        }
                    }
                });
                // nupu loomised
                nupud[i][j] = nupp;
                grid.add(nupp, j, i);
            }
        }
        //Väljamakse mängust
        Button rahaValja = new Button("VÕTA RAHA");

        rahaValja.setOnAction(e -> {

            if (kaotas[0]) { // kui on kaotatud
                info.setTextFill(Color.RED);
                info.setText("Kaotasid! Cashout pole võimalik.");
                return;
            }

            if (voidusumma[0] > 0) { // väljamakse peab olema suurem kui 0

                mangija.setKrediidid(
                        mangija.getKrediidid() + voidusumma[0]
                );

                krediidid.setText("EUR - " + mangija.getKrediidid());

                info.setTextFill(Color.LIME);
                info.setText("Cashout: +" + String.format("%.2f", voidusumma[0]));

                voidusumma[0] = 0;
                panus[0] = 0;
                safeCount[0] = 0;

                voidusummaText.setText("Võidusumma: 0");

                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 4; y++) {
                        nupud[x][y].setDisable(true); // nuppe ei saa enam vajutada peale väljamakset
                    }
                }
            }
        });

       // tagasi nupp
        Button tagasi = new Button("TAGASI");
        tagasi.setStyle(
                "-fx-background-color: gray;" +
                        "-fx-text-fill: white;"
        );

        tagasi.setOnAction(e -> {
            app.avaPeamenüü(stage);
        });

        root.getChildren().addAll(
                krediidid,
                panusField,
                alusta,
                voidusummaText,
                grid,
                rahaValja,
                info,
                tagasi
        );

        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);
    }
}