package GUI;

import Logic.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {


        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * JavaFX alkalmazást elindító függvény.
     *
     * @param window Ablak.
     * @throws Exception Kivétel.
     */
    @Override
    public void start(Stage window) throws Exception {
        final double MIN_WIDTH = 400;
        final double MIN_HEIGHT = 300;
        window.setResizable(false);
        Settings settings = new Settings();
        double width = settings.getWidth();
        double height = settings.getHeight();
        double windowPosX = settings.getWindowPosX();
        double windowPosY = settings.getWindowPosY();
        window.setWidth(width);
        window.setHeight(height);
        window.setX(windowPosX);
        window.setY(windowPosY);
        MainMenu mainMenu = new MainMenu(window);


        Scene mainMenuScene = new Scene(mainMenu, width, height);
        window.setTitle("Snake");

        window.setOnCloseRequest(e -> {
            Settings toBeSaved = new Settings(); //Azok a beállítások amiket elmentünk
            toBeSaved.getWindowOptions(window); //Megszerezzük az ablak adatait
            toBeSaved.putEverything(); //Elmentjük a beállításokat
        });

        window.setMinWidth(MIN_WIDTH);
        window.setMinHeight(MIN_HEIGHT);
        window.setScene(mainMenuScene);
        window.show();
    }
}
