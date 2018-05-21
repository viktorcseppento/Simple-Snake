package GUI;

import Logic.Difficulty;
import Logic.Settings;
import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Játék beálíltásainak módosítására használandó osztály.
 */
public class Options extends StackPane {

    private Stage window;
    private Scene mainMenuScene; //Hogy vissza tudjunk lépni a menübe
    private Settings settings;

    private ChoiceBox<String> setResolution;

    private ArrayList<RadioButton> setEnemies;

    private ArrayList<RadioButton> setDifficulty;

    /**
     * @param window        Játékot kijelző ablak.
     * @param mainMenuScene Főmenü jelenete.
     */
    public Options(Stage window, Scene mainMenuScene) {
        this.mainMenuScene = mainMenuScene;
        this.window = window;
        settings = new Settings();

        Text resolution = new Text("Resolution");
        resolution.setFont(new Font(20));
        resolution.translateYProperty().bind(window.heightProperty().divide(-2.5));

        setResolution = createResOptions();

        Text numberOfEnemies = new Text("Number Of Enemies");
        numberOfEnemies.setFont(new Font(20));
        numberOfEnemies.translateYProperty().bind(window.heightProperty().divide(-8));

        setEnemies = createEnemyOptions();
        ToggleGroup enemyToggle = new ToggleGroup();
        enemyToggle.getToggles().addAll(setEnemies);

        Text difficulty = new Text("Difficulty");
        difficulty.setFont(new Font(20));
        difficulty.translateYProperty().bind(window.heightProperty().divide(8));

        setDifficulty = createDifficultyOptions();
        ToggleGroup difficultyToggle = new ToggleGroup();
        difficultyToggle.getToggles().addAll(setDifficulty);

        Button applyButton = new Button("Apply");
        applyButton.translateYProperty().bind(window.heightProperty().divide(2.8));

        applyButton.setOnAction(e -> applyChanges());

        Button backButton = new Button("Back");
        backButton.translateYProperty().bind(window.heightProperty().divide(2.8));
        backButton.translateXProperty().bind(window.widthProperty().divide(-10));

        backButton.setOnAction(e -> window.setScene(mainMenuScene));

        Button okButton = new Button("Ok");
        okButton.translateYProperty().bind(window.heightProperty().divide(2.8));
        okButton.translateXProperty().bind(window.widthProperty().divide(10));

        okButton.setOnAction(e -> {
            applyChanges();
            window.setScene(mainMenuScene);
        });

        this.getChildren().addAll(resolution, setResolution, numberOfEnemies, difficulty, applyButton, backButton, okButton);
        this.getChildren().addAll(setEnemies);
        this.getChildren().addAll(setDifficulty);
    }

    /**
     * Felbontás beállítására szolgáló függvény.
     *
     * @return Felbontások listája JavaFx ChoiceBoxban.
     */
    private ChoiceBox<String> createResOptions() { //Felbontás beállítása
        ChoiceBox<String> setResolution = new ChoiceBox<>(FXCollections.observableArrayList("800x480", "1024x768", "1280x1024", "1366x768", "1920x1080", "2560x1600"));
        String resString = String.valueOf((int) window.getWidth()) + "x" + String.valueOf((int) window.getHeight());
        setResolution.setValue(resString);
        setResolution.translateYProperty().bind(window.heightProperty().divide(-3.3));
        return setResolution;
    }

    /**
     * Ellenségek számának beállítására szolgáló függvény.
     *
     * @return Ellenségek lehetséges számainak beállítógombjai.
     */
    private ArrayList<RadioButton> createEnemyOptions() { //Ellenségek számának menüpontja
        ArrayList<RadioButton> setEnemies = new ArrayList<>(4);
        RadioButton zero = new RadioButton("0");
        RadioButton one = new RadioButton("1");
        RadioButton two = new RadioButton("2");
        RadioButton three = new RadioButton("3");
        setEnemies.add(zero);
        setEnemies.add(one);
        setEnemies.add(two);
        setEnemies.add(three);

        for (RadioButton rb : setEnemies) {
            rb.translateYProperty().bind(window.heightProperty().divide(-50));
            if (settings.getNumberOfEnemies() == Integer.parseInt(rb.getText()))
                rb.fire();
        }
        zero.translateXProperty().bind(window.widthProperty().divide(-10));
        one.translateXProperty().bind(window.widthProperty().divide(-30));
        two.translateXProperty().bind(window.widthProperty().divide(30));
        three.translateXProperty().bind(window.widthProperty().divide(10));

        return setEnemies;
    }

    /**
     * Játék nehézségi fokának beállítására szolgáló függvény.
     *
     * @return Nehézségi szint lehetséges értékeinek beállítógombjai.
     */
    private ArrayList<RadioButton> createDifficultyOptions() { //Ellenségek számának menüpontja
        ArrayList<RadioButton> setDifficulty = new ArrayList<>(4);
        RadioButton easy = new RadioButton("easy");
        RadioButton medium = new RadioButton("medium");
        RadioButton hard = new RadioButton("hard");
        RadioButton expert = new RadioButton("expert");
        setDifficulty.add(easy);
        setDifficulty.add(medium);
        setDifficulty.add(hard);
        setDifficulty.add(expert);

        for (RadioButton rb : setDifficulty) {
            rb.translateYProperty().bind(window.heightProperty().divide(4));
            if (settings.getDifficulty().toString().equals(rb.getText().toUpperCase()))
                rb.fire();
        }
        easy.translateXProperty().bind(window.widthProperty().divide(-9));
        medium.translateXProperty().bind(window.widthProperty().divide(-25));
        hard.translateXProperty().bind(window.widthProperty().divide(25));
        expert.translateXProperty().bind(window.widthProperty().divide(9));

        return setDifficulty;
    }

    /**
     * Módosítások elmentése.
     */
    private void applyChanges() {
        Rectangle2D monitorScreen = Screen.getPrimary().getVisualBounds(); //Monitor dimenzióit lekérjük
        double width, height, posX, posY;

        String res = setResolution.getValue();
        width = Double.parseDouble(res.split("x")[0]); //Pl 1920x1080-t szétszedjük 1920-ra és 1080-ra
        height = Double.parseDouble(res.split("x")[1]);
        posX = (monitorScreen.getWidth() - width) / 2;
        posY = (monitorScreen.getHeight() - height) / 2;
        if (posX < 0)
            posX = 0;
        if (posY < 0)
            posY = 0;

        if (!(width == settings.getWidth() && height == settings.getHeight())) { //Ha megváltoztattuk a felbontást, akkor állítjuk át csak
            //Beírjuk a preferenciákba
            settings.setWidth(width);
            settings.setHeight(height);
            settings.setWindowPosX(posX);
            settings.setWindowPosY(posY);

            //Megváltoztatjuk az ablal méreteit, pozícióját
            window.setWidth(settings.getWidth());
            window.setHeight(settings.getHeight());
            window.setX(posX);
            window.setY(posY);
        }
        for (RadioButton rb : setEnemies) { //Ellenségek számának beállítása, végigmegy a gombokon és amelyik aktív annak a szövegének a számbeli értékét adja hozzá
            if (rb.isSelected())
                settings.setNumberOfEnemies(Integer.parseInt(rb.getText()));
        }

        for (RadioButton rb : setDifficulty) {
            if (rb.isSelected())
                settings.setDifficulty(Difficulty.valueOf(rb.getText().toUpperCase()));
        }

    }

    public Stage getWindow() {
        return window;
    }

    public Scene getMainMenuScene() {
        return mainMenuScene;
    }
}
