package Logic;

import AI.PathFinder;
import GUI.GameGUI;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.*;

import static java.lang.StrictMath.floor;

public class GameLoop extends AnimationTimer {

    private final long interval; //2 frissítés közt mennyi idő legyen nanomásodpercben
    private long lastRefresh; //Nanoszekundumban, hogy mikor volt utoljára frissítés
    private GameGUI gui;
    private boolean running;
    private Snake snake;
    private int numberOfEnemies;
    private ArrayList<Snake> enemies;
    private ArrayList<Fruit> fruits;
    private Difficulty difficulty;
    private final int cellSize = 16;
    private int columns, rows; //Hányszor hanyas lesz a játékterep
    private PathFinder pathFinder;

    /**
     * Játék logikai egységének konstruktora.
     *
     * @param gui Játék GUI osztálya
     */
    public GameLoop(GameGUI gui) {
        final int FPS = 600; //1 másodperc alatt hányszor legyen képkockafrissítés
        Settings settings = new Settings();
        settings.setGameOptions(this);
        interval = 1000000000L / FPS;
        lastRefresh = 0;
        this.gui = gui;
        running = true;
        columns = (int) floor(gui.getCanvas().getWidth() / cellSize);
        rows = (int) floor(gui.getCanvas().getHeight() / cellSize);
        pathFinder = new PathFinder(this);

        int startingVelocity;
        switch (difficulty) {
            case EASY:
                startingVelocity = 6;
                break;
            case MEDIUM:
                startingVelocity = 8;
                break;
            case HARD:
                startingVelocity = 12;
                break;
            case EXPERT:
                startingVelocity = 16;
                break;
            default:
                startingVelocity = 4;
        }

        enemies = new ArrayList<>(numberOfEnemies);
        for (int i = 0; i < numberOfEnemies; i++) { //Ellenség kígyók
            Point startingPoint = randomStartingPoint(i);
            enemies.add(new Snake(startingPoint, startingVelocity, getEnemyColor(i), cellSize));
        }
        snake = new Snake(randomStartingPoint(enemies.size()), startingVelocity, Color.LAWNGREEN, cellSize); //Mi kígyónk

        fruits = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            fruits.add(new Fruit(this, Color.RED)); //Letesszük a gyümölcsöket
        }
    }

    /**
     * Minden képfrissítésnél meg van hívva.
     *
     * @param now Hány nanoszekundum telt el a program indítása óta.
     */
    public void handle(long now) {

        boolean doRefresh; //Igaz ha frissíteni kell a képernyőt

        doRefresh = (now - lastRefresh > interval) && running; //Ha most és az utolsó frissítés közt eltelt elegendő idő és nincs lepausolva, akkor kell frissíteni

        if (doRefresh) {
            moveEveryThing();
            gui.drawEverything();

            for (Snake e : enemies)
                checkPickedFruit(e);
            checkPickedFruit(snake);

            checkCollision();

            lastRefresh = now; //Az utolsó frissítés most történt meg
        }

    }

    /**
     * Megnézi, hogy kígyó vett-e fel gyümölcsöt, ha igen tesz le újat és növeli a kígyót.
     *
     * @param s Kígyó.
     */
    private void checkPickedFruit(Snake s) {
        Fruit toBeRemoved = null;
        for (Fruit f : fruits) {
            if (s.getHead().intersect(f, cellSize)) {
                s.requestNewPoint();
                toBeRemoved = f;
            }
        }
        if (toBeRemoved != null) {
            fruits.remove(toBeRemoved);
            fruits.add(new Fruit(this, Color.RED));
        }
    }

    /**
     * Megnézi az ütközéseket, és ha voltak elintézi őket.
     */
    private void checkCollision() {
        HashMap<Snake, Boolean> deaths = new HashMap<>(); //Ha valamelyik meghalt, true-t tesz be
        for (Snake e : enemies)
            deaths.put(e, didCollide(e));

        deaths.put(snake, didCollide(snake));

        for (Iterator iterator = enemies.iterator(); iterator.hasNext(); ) { //Végigmegy a HashMap-en és kitörli a játékból a meghalt kígyókat.
            Snake e = (Snake) iterator.next();
            if (deaths.get(e)) {
                deaths.remove(e);
                iterator.remove();
            }
        }


        if (deaths.get(snake)) { //Ha a játékos halt meg akkor le is állítja a játékot.
            this.running = false;
            this.stop();
            gui.gameOverScene(snake.getPoints().size() - 3);
        }

    }

    /**
     * Véletlenszerű kezdőpontot ad.
     *
     * @param numberOfGeneratedEnemies Már legenerált ellenségek száma.
     * @return Kész pont.
     */
    private Point randomStartingPoint(int numberOfGeneratedEnemies) { //Megkapja, hogy mennyi ellenség lett már legenerálva
        Random rand = new Random();
        Point point;
        int randomColumn, row; //A sor az fix
        boolean validPoint;
        do { //Addig generál új pontokat míg nem talál olyat, ami elég messze van a többi kígyótól
            validPoint = true;
            randomColumn = rand.nextInt(columns - 2) + 1; //Elég messze legyen a faltól
            row = rows - 3;
            point = new Point(randomColumn * cellSize, row * cellSize);
            for (int i = 0; i < numberOfGeneratedEnemies; i++) {
                if (enemies.get(i).getHead().distanceFromOtherPoint(point) < 8 * cellSize) //Egymástól legalább 8 cellára legyenek
                    validPoint = false;
            }
        } while (!validPoint);

        return point;
    }

    /**
     * Megnézi egy kígyóra, hogy ütközött-e.
     *
     * @param s Kígyó.
     * @return Igaz vagy hamis érték.
     */
    private boolean didCollide(Snake s) {
        ArrayList<Snake> snakes = new ArrayList<>(numberOfEnemies + 1);
        snakes.addAll(enemies);
        snakes.add(snake);
        for (Snake other : snakes) {
            if (s == other) { //Magára  megnézzük
                for (int i = 2; i < s.getPoints().size(); i++) { //A fej nem ütközhet saját magával és az utána lévővel úgy sem
                    if (s.getHead().intersect(s.getPoints().get(i), cellSize))
                        return true;
                }
            } else { //Többire is
                for (Point p : other.getPoints()) {
                    if (s.getHead().intersect(p, cellSize))
                        return true;
                }
            }
        }

        return s.getHead().x > (columns - 1) * cellSize || s.getHead().x < 0 || s.getHead().y > (rows - 1) * cellSize || s.getHead().y < 0; //Kimegy-e a pályáról

    }

    /**
     * Egy ellenség színét adja meg.
     *
     * @param whichEnemy Hanyadik ellenség.
     * @return Szín.
     */
    private Color getEnemyColor(int whichEnemy) { //Ellenség indexét kapja meg
        Color returnColor;
        switch (whichEnemy) {
            case 0:
                returnColor = Color.YELLOW;
                break;
            case 1:
                returnColor = Color.SKYBLUE;
                break;
            default:
                returnColor = Color.BLUE;
                break;

        }
        return returnColor;
    }

    /**
     * Összes kígyót mozgatja, AI-k következő lépést meghatározza.
     */
    private void moveEveryThing() {
        pathFinder.refreshNodes();
        Collections.shuffle(enemies);
        for (Snake s : enemies) {

            s.setRequestedDir(pathFinder.runAlgorithm(s, s.getHead().closestPoint(fruits)));
            s.move();
        }
        snake.move();
    }


    public GameGUI getGui() {
        return gui;
    }

    public boolean isRunning() {
        return running;
    }

    public Snake getSnake() {
        return snake;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    public ArrayList<Fruit> getFruits() {
        return fruits;
    }

    public long getInterval() {
        return interval;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public void setNumberOfEnemies(int numberOfEnemies) {
        this.numberOfEnemies = numberOfEnemies;
    }

    public ArrayList<Snake> getEnemies() {
        return enemies;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }


    public int getCellSize() {
        return cellSize;
    }


    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Játék közbeni billenytűlenyomások kezelője.
     */
    public static class KeyHandler implements EventHandler<KeyEvent> { //Vezérlés

        GameLoop loop;
        Snake snake;

        public KeyHandler(GameLoop loop) {
            this.loop = loop;
            this.snake = loop.getSnake();
        }

        public void handle(KeyEvent e) {
            switch (e.getCode()) {
                case UP:
                    if (loop.running) //Csak ha nincs lepausolva
                        snake.setRequestedDir(Snake.Direction.UP);
                    break;
                case RIGHT:
                    if (loop.running)
                        snake.setRequestedDir(Snake.Direction.RIGHT);
                    break;
                case DOWN:
                    if (loop.running)
                        snake.setRequestedDir(Snake.Direction.DOWN);
                    break;
                case LEFT:
                    if (loop.running)
                        snake.setRequestedDir(Snake.Direction.LEFT);
                    break;
                case ESCAPE:
                    if (!loop.running) {
                        loop.running = true;
                        loop.getGui().gameScene();
                    } else {
                        loop.running = false;
                        loop.getGui().pausedScene();
                    }
            }
        }
    }

}

