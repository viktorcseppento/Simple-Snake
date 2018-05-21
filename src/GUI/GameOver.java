package GUI;

import Database.ScoreTable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameOver extends StackPane {
    private Scene mainMenuScene;
    private TextField nameField;

    public GameOver(Stage window, Scene mainMenu, int score) {
        this.mainMenuScene = mainMenu;


        Text gameOverText = new Text("Game Over");
        gameOverText.setFont(new Font("Book Antiqua", 150));
        gameOverText.translateYProperty().bind(this.heightProperty().divide(-4));

        Text scoreText = new Text("Score: " + String.valueOf(score));
        scoreText.setFont(new Font("Book Antiqua", 40));
        scoreText.translateYProperty().bind(this.heightProperty().divide(-14));

        Label name = new Label("Name:");
        name.translateYProperty().bind(this.heightProperty().divide(30));

        nameField = new TextField();
        nameField.translateYProperty().bind(this.heightProperty().divide(12));
        nameField.setMaxWidth(200);
        nameField.setFocusTraversable(false);


        Button submit = new Button("Submit Score");
        submit.translateYProperty().bind(this.heightProperty().divide(6.4));
        submit.prefWidthProperty().bind(this.widthProperty().divide(5));
        submit.prefHeightProperty().bind(this.heightProperty().divide(20));
        submit.setOnAction(e -> {
            if (nameField.getText().length() <= 50 && nameField.getText().length() > 0)
                ScoreTable.addNewEntry(nameField.getText(), score);
            window.setScene(mainMenuScene);
        });

        Button toMainMenu = new Button("Main Menu");
        toMainMenu.translateYProperty().bind(this.heightProperty().divide(3.5));
        toMainMenu.prefWidthProperty().bind(this.widthProperty().divide(5));
        toMainMenu.prefHeightProperty().bind(this.heightProperty().divide(20));
        toMainMenu.setOnAction(e -> window.setScene(mainMenuScene));

        this.getChildren().addAll(gameOverText, scoreText, name, nameField, submit, toMainMenu);
        window.setScene(new Scene(this, window.getWidth(), window.getHeight()));
    }
}


