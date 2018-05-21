package GUI;

import Database.ScoreTable;
import Logic.GameLoop;
import Logic.Settings;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MainMenu extends StackPane {
    private Stage window; //Az ablak amin rajta vannak a gombok

    /**
     * @param window Az játékot kijelző ablak.
     */
    MainMenu(Stage window) {
        this.window = window;
        Button startButton = new Button("Start");
        Button optionsButton = new Button("Options");
        Button exitButton = new Button("Exit");
        Button scoresButton = new Button("High Scores");
        this.getChildren().addAll(startButton, optionsButton, scoresButton, exitButton);

        startButton.setOnAction(event -> {
            GameGUI gui = new GameGUI(window, this.getWidth(), this.getHeight(), window.getScene());
            Scene gameScene = new Scene(gui, this.getWidth(), this.getHeight());
            GameLoop loop = new GameLoop(gui);
            gui.setLoop(loop);
            gameScene.setOnKeyPressed(new GameLoop.KeyHandler(loop));
            window.setScene(gameScene);
            loop.start();

        });

        optionsButton.setOnAction(event -> {
            Options opt = new Options(window, window.getScene());
            Scene optionsScene = new Scene(opt, this.getWidth(), this.getHeight());
            window.setScene(optionsScene);
        });

        scoresButton.setOnAction(event -> {
            ScoreTable scores = new ScoreTable(window, this.getScene());
            Scene scoreScene = new Scene(scores, this.getWidth(), this.getHeight());
            scores.connectToDatabase();
            try {
                if (scores.getConn() != null) {
                    scores.createTableGuiFromServer();
                    scores.getConn().close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            window.setScene(scoreScene);
        });

        exitButton.setOnAction(event -> {
            Settings settings = new Settings();
            settings.getWindowOptions(window);
            settings.putEverything();
            window.close();
        });

        //Gombok közötti gap-ek beállítása
        startButton.translateYProperty().bind(this.heightProperty().divide(-8));
        scoresButton.translateYProperty().bind(this.heightProperty().divide(8));
        exitButton.translateYProperty().bind(this.heightProperty().divide(4));


        //Gombok magassagának beállítása
        startButton.prefHeightProperty().bind(this.heightProperty().divide(20));
        optionsButton.prefHeightProperty().bind(this.heightProperty().divide(20));
        scoresButton.prefHeightProperty().bind(this.heightProperty().divide(20));
        exitButton.prefHeightProperty().bind(this.heightProperty().divide(20));

        //Gombok szélességének beállítása
        startButton.prefWidthProperty().bind(this.widthProperty().divide(3));
        optionsButton.prefWidthProperty().bind(this.widthProperty().divide(3));
        scoresButton.prefWidthProperty().bind(this.widthProperty().divide(3));
        exitButton.prefWidthProperty().bind(this.widthProperty().divide(3));
    }

    public Stage getWindow() {
        return window;
    }


}

