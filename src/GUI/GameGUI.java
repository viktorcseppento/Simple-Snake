package GUI;

import Logic.Fruit;
import Logic.GameLoop;
import Logic.Snake;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Játék grafikájával foglalkozó osztály
 */
public class GameGUI extends StackPane {

    private Stage window;
    private Scene mainMenuScene; //Hogy vissza tudjunk lépni a menübe
    private Canvas canvas;
    private GraphicsContext gc;
    private GameLoop loop;
    private Text pausedText;

    /**
     * Létrehoz egy új GameGUI-t.
     *
     * @param window        Játékot kijelző ablak.
     * @param width         Ablak szélessége.
     * @param height        Ablak magassága.
     * @param mainMenuScene Főmenü jelenete.
     */
    GameGUI(Stage window, double width, double height, Scene mainMenuScene) {
        super();
        this.setStyle("-fx-background-color: rgb(128, 0, 128);"); //A háttér lila legyen
        this.window = window;
        this.mainMenuScene = mainMenuScene;
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

    }

    /**
     * Kirajzolja a kígyókat, és a gyümölcsöt.
     */
    public void drawEverything() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());


        for(Fruit f :loop.getFruits()) //Gyümölcsök rajzolása
            f.draw(gc, loop.getCellSize());

        for (Snake s : loop.getEnemies()) //Ellenségek rajzolása
            s.draw(gc, loop.getCellSize());

        loop.getSnake().draw(gc, loop.getCellSize()); //Játékos rajzolása

    }

    /**
     * Játék leszüneteltésekor meghívott függvény.
     */
    public void pausedScene() { //Ha le van pause-olva a játék kiírja
        this.setStyle("-fx-background-color: rgb(86, 0, 86);");
        pausedText = new Text("Paused");
        pausedText.setFont(new Font("Book Antiqua", 100));

        this.getChildren().addAll(pausedText);
    }

    /**
     * Játék folytatásakor meghívott függvény.
     */
    public void gameScene() { //Ha le volt pause-olva, visszaállítja a játékot
        this.getChildren().remove(pausedText);
        this.setStyle("-fx-background-color: rgb(128, 0, 128);");
    }

    /**
     * Játék végekor meghívott függvény.
     *
     * @param score Játékos által elért pontszám.
     */
    public void gameOverScene(int score) { //Ha vége a játéknak
        new GameOver(window, mainMenuScene, score);
    }

    public Stage getWindow() {
        return window;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setWindow(Stage window) {
        this.window = window;
    }

    public GameLoop getLoop() {
        return loop;
    }

    public void setLoop(GameLoop loop) {
        this.loop = loop;
    }
}
