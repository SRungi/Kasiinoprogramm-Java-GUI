package com.example.kasiinoprogrammgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class HelloApplication extends Application {

    private Kasutaja mangur;
    public Stage stage;
    private Label krediidid;

    Image pilt = new Image(getClass().getResource("/pilt1.jpg").toExternalForm());

    BackgroundImage bgImage = new BackgroundImage(
            pilt,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, false, true)
    );

    Background bg = new Background(bgImage);

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label küsimus = new Label("Sisesta oma nimi:");
        küsimus.setFont(Font.font("Calibri", FontWeight.BOLD, 50));
        küsimus.setTextFill(Color.RED);

        TextField nimeVäli = new TextField(); // kuhu sisestatakse nimi
        nimeVäli.setMaxWidth(200);

        Button nupp = new Button("Kinnita");


        nupp.setDefaultButton(true); // nuppuga saab Enteriga kinnitada


        nupp.setOnAction(e -> { // kui nuppu vajutatakse luuakse uus mängija
            String nimi = nimeVäli.getText();
            mangur = new Kasutaja(nimi, 0, 0);
            avaPeamenüü(stage);
        });

        VBox root = new VBox(15);
        root.setBackground(bg);
        root.getChildren().addAll(küsimus, nimeVäli, nupp);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 350);

        stage.setTitle("Kasiino");
        stage.setScene(scene);
        stage.show();
    }

    public void avaPeamenüü(Stage stage) {


        BorderPane root = new BorderPane();
        HBox manguri_kuvang = new HBox(20);

        manguri_kuvang.setBorder(new Border(new BorderStroke( // Kasti , mille sees on mängija nimi, krediidid ja ikoonipilt, on tehtud kulla värvi
                Color.GOLD,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2)
        )));

        manguri_kuvang.setBackground(new Background(new BackgroundFill( // taustapilt
                Color.BLACK,
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        Image image = new Image(getClass().getResource("/pilt2.jpg").toExternalForm()); // inimese ikoonipilt, mis iseloomustab avatari
        ImageView avatar = new ImageView(image);
        avatar.setFitHeight(50);
        avatar.setFitWidth(50);

        Label mangurinimi = new Label(mangur.getNimi());
        mangurinimi.setTextFill(Color.WHITE);

        krediidid = new Label("EUR - " + mangur.getKrediidid());
        krediidid.setTextFill(Color.GOLD);
        krediidid.setFont(Font.font("Calibri", FontWeight.BOLD, 22));

        manguri_kuvang.getChildren().addAll( // Kast kus on ikoonipilt, krediidid, mangurinimi
                avatar,
                mangurinimi,
                krediidid
        );

        manguri_kuvang.setAlignment(Pos.TOP_LEFT);

        root.setTop(manguri_kuvang);
        root.setBackground(bg);

        //nupud
        Button juurdemakse = new Button("Lisa Krediite +");
        Button väljamakse = new Button("Väljamakse");
        Button täringumäng = new Button("Täringumäng");
        Button Mustjaak = new Button("BlackJack");
        Button Tiles = new Button("Tiles");
        Button Slot = new Button("Slot");
        Button rulett = new Button("Roulette");
        Button välju = new Button("Välju");

        String nupp_stiil =
                "-fx-background-color: #1a1a2e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 30;" +
                        "-fx-cursor: hand;";

        Button[] buttons = {
                juurdemakse, väljamakse, täringumäng,
                Mustjaak, Tiles, Slot, rulett, välju
        };

        for (Button b : buttons) { // igale nupule rakendatakse samat stiili
            b.setStyle(nupp_stiil);
        }

        VBox nupud = new VBox(10);
        nupud.setAlignment(Pos.CENTER);
        nupud.getChildren().addAll(buttons); //nupud lisatakse VBoxi

        root.setCenter(nupud);

        juurdemakse.setDefaultButton(true); // juurdemakset saab teha nii, et vajutada enter ja siis tuleb juurdemakse menüü ette

        juurdemakse.setOnAction(e -> {
            krediidid_juurdemakse(mangur);
            krediidid.setText("EUR - " + mangur.getKrediidid());
        });

        välju.setOnAction(event -> stage.close());

        väljamakse.setOnAction(event -> väljamakse(mangur));

        Slot.setOnAction(event -> new SlotGUI(this, mangur).naita());
        rulett.setOnAction(event -> new RulettGUI(this, mangur).naita());
        Tiles.setOnAction(event -> new MineGUI(this, mangur).naita());
        täringumäng.setOnAction(event -> new TäringumängGUI(this, mangur).naita());
        Mustjaak.setOnAction(event -> new MustjaakGUI(this, mangur).naita());

        Scene peamenüü = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());

        stage.setScene(peamenüü);
    }

    private void väljamakse(Kasutaja mangur) {
        TextInputDialog withdraw = new TextInputDialog();

        withdraw.setTitle("Väljamakse");
        withdraw.setHeaderText("Sisesta väljamakse summa!");
        withdraw.setContentText("Summa: ");

        Optional<String> tulemus = withdraw.showAndWait(); // ootab kuni kasutaja teeb midagi

        if (tulemus.isPresent()) { // kui kasutaja sisestas midagi
            try {
                double summa = Double.parseDouble(tulemus.get());

                if (summa > mangur.getKrediidid()) { // ei saa raha võtta välja rohkem kui seda kontol on
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Sa ei saa rohkem võtta kui sul on!");
                    error.showAndWait();

                } else if (summa < 0) { // ei saa negatiivseid ühikuid välja võtta
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Negatiivne summa pole lubatud!");
                    error.showAndWait();

                } else { // kui kõik on õigesti tehtud
                    mangur.setKrediidid(mangur.getKrediidid() - summa);
                    krediidid.setText("EUR - " + mangur.getKrediidid());

                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setContentText("Väljamakse tehtud!");
                    ok.showAndWait();
                }

            } catch (NumberFormatException e) { // inimene peab sisestama numbri
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Sisesta korrektne number");
                error.showAndWait();
            }
        }
    }

    private void krediidid_juurdemakse(Kasutaja mangur) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Juurdemakse");
        dialog.setHeaderText("Sisesta summa (Limiit 10000)");
        dialog.setContentText("Summa:");

        Optional<String> tulemus = dialog.showAndWait();

        if (tulemus.isPresent()) {
            try {
                double summa = Double.parseDouble(tulemus.get());

                if (summa > 10000) { // juurde ei saa maksta rohkem kui 10000
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Limiit on 10000");
                    error.showAndWait();

                } else if (summa < 0) { // negatiivseid arve ei saa juurdemaksta
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Negatiivne summa pole lubatud!");
                    error.showAndWait();

                } else { // kui kõik sujus
                    mangur.juurdemakse(summa);
                    krediidid.setText("EUR - " + mangur.getKrediidid());

                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setContentText(summa + " lisatud kontole");
                    ok.showAndWait();
                }

            } catch (NumberFormatException e) { // peab olema number
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Sisesta korrektne number");
                error.showAndWait();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}