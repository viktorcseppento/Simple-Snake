package Database;

import Logic.Settings;

public class Entry {
    private String name;
    private String difficulty;
    private String resolution;
    private int numberOfEnemies;
    private int score;

    /**
     * Bejegyzést hoz létre a táblába.
     *
     * @param name  Játékos neve.
     * @param score Elért pontszám.
     */
    public Entry(String name, int score) {
        this.name = name;
        this.score = score;
        Settings settings = new Settings();
        double windowWidth, windowHeight;

        windowWidth = settings.getWidth();
        windowHeight = settings.getHeight();
        numberOfEnemies = settings.getNumberOfEnemies();
        resolution = String.valueOf((int) windowWidth) + "x" + String.valueOf((int) windowHeight); //String lesz a felbontásból
        difficulty = settings.getDifficulty().toString();
    }

    /**
     * Új bejegyzést hoz létre.
     *
     * @param name            Név.
     * @param difficulty      Nehézségi szint.
     * @param numberOfEnemies Ellenségek száma.
     * @param resolution      Felbontás.
     * @param score           Elért pontszám.
     */

    public Entry(String name, String difficulty, String resolution, int numberOfEnemies, int score) {
        this.name = name;
        this.difficulty = difficulty;
        this.resolution = resolution;
        this.numberOfEnemies = numberOfEnemies;
        this.score = score;
    }


    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getResolution() {
        return resolution;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    public int getScore() {
        return score;
    }
}
