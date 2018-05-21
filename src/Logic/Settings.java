package Logic;

import javafx.stage.Stage;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class Settings {

    private Preferences prefs;
    private double width, height;
    private double windowPosX, windowPosY;
    private int numberOfEnemies;
    private Difficulty difficulty;

    private static boolean firstRun = true;

    /**
     * Ha a program futása során először fut le, fájlból beolvassa a beállításokat.
     */
    public Settings() {
        if (firstRun) { //Ha megnyitjuk a programot, fájlból olvassa be a beállításokat
            try {
                File f = new File("settings.xml");
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    Preferences.importPreferences(fis); //Beállításokat a settings.xml fájlból olvassuk ki
                    fis.close();
                }
            } catch (IOException | InvalidPreferencesFormatException e) {
                e.printStackTrace();
            } finally {
                firstRun = false;
            }
        }
        prefs = Preferences.userRoot();
        width = prefs.getDouble("width", 800);
        height = prefs.getDouble("height", 480);
        windowPosX = prefs.getDouble("windowPosX", 0);
        windowPosY = prefs.getDouble("windowPosY", 0);
        numberOfEnemies = prefs.getInt("numberOfEnemies", 0);
        difficulty = Difficulty.valueOf(prefs.get("difficulty", "EASY"));
    }

    public Preferences getPrefs() {
        return prefs;
    }

    /**
     * Beállítja az osztály változóít az ablak alapján.
     *
     * @param window Ablak amiről lemásolja a beállításokat.
     */
    public void getWindowOptions(Stage window) {
        width = window.getWidth();
        height = window.getHeight();
        windowPosX = window.getX();
        windowPosY = window.getY();
    }

    /**
     * Beállítja a játék logikai beállításait
     *
     * @param loop Játék magja.
     */
    void setGameOptions(GameLoop loop) {
        loop.setDifficulty(difficulty);
        loop.setNumberOfEnemies(numberOfEnemies);
    }

    /**
     * Beállítások kiírása fájlba.
     */
    public void putEverything() { //Minden beállítást beírunk a fájlba
        try {
            prefs.putDouble("width", width);
            prefs.putDouble("height", height);

            if (windowPosX < 0)
                windowPosX = 0;
            if (windowPosY < 0)
                windowPosY = 0;

            prefs.putDouble("windowPosX", windowPosX);
            prefs.putDouble("windowPosY", windowPosY);
            prefs.putInt("numberOfEnemies", numberOfEnemies);
            prefs.put("difficulty", difficulty.toString());
            prefs.exportNode(new FileOutputStream("settings.xml")); //Fájlba írjuk a beállításokat
        } catch (IOException | BackingStoreException e) {
            e.printStackTrace();
        }
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getWindowPosX() {
        return windowPosX;
    }

    public double getWindowPosY() {
        return windowPosY;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public static boolean isFirstRun() {
        return firstRun;
    }

    public void setWidth(double width) {
        this.width = width;
        prefs.putDouble("width", width);
    }

    public void setHeight(double height) {
        this.height = height;
        prefs.putDouble("height", height);
    }

    public void setWindowPosX(double windowPosX) {
        this.windowPosX = windowPosX;
        prefs.putDouble("windowPosX", windowPosX);
    }

    public void setWindowPosY(double windowPosY) {
        this.windowPosY = windowPosY;
        prefs.putDouble("windowPosY", windowPosY);
    }

    public void setNumberOfEnemies(int numberOfEnemies) {
        this.numberOfEnemies = numberOfEnemies;
        prefs.putInt("numberOfEnemies", numberOfEnemies);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        prefs.put("difficulty", difficulty.toString());
    }
}
