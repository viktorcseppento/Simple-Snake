package Logic;

import GUI.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;


public class Snake implements Drawable, Movable {

    public enum Direction {
        UP, RIGHT, DOWN, LEFT;
    }

    private ArrayList<Point> points;
    private Point head;
    private int velocity;
    private Direction dir;
    private Direction requestedDir;
    private Color snakeColor;
    private boolean alive;
    private LinkedHashMap<Point, Integer> trail; //Azon pontok, ahol kanyarodik a kígyó, a szám pedig, hogy a kígyónak hány pontja ment át rajta már a fejen kívül
    private LinkedHashMap<Point, Integer> requestedNewPoints; //Ha nőni akar a kígyó, ez ebben lesznek azon pontok amik még nincsenek a kígyóban és a szám pedig, hogy mikor kell hozzáadni a kígyóhoz
    private int coveredPixels;

    /**
     * Kígyó konstruktora.
     *
     * @param startingPoint    Véletleszerűen generált kezdőpont.
     * @param startingVelocity Nehézségi szinttől függő sebesség.
     * @param startingColor    Kezdőszín.
     * @param cellsize         Testsejtek átmérője.
     */
    Snake(Point startingPoint, int startingVelocity, Color startingColor, int cellsize) {
        points = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            points.add(new Point(startingPoint.x, startingPoint.y + i * cellsize));
        }

        head = points.get(0);
        velocity = startingVelocity;
        requestedDir = dir = Direction.UP;
        snakeColor = startingColor;
        alive = true;
        trail = new LinkedHashMap<>();
        requestedNewPoints = new LinkedHashMap<>();
        coveredPixels = 0;
    }

    /**
     * Kígyó mozgatása.
     */
    public void move() {

        int cellSize = 16; //Átmérő beállítása

        for (int i = 0; i < velocity; i++) { //Annyiszor lépked 1-1 pixelt minden képkockában amennyi a sebessége
            coveredPixels++;
            if (onBorder(cellSize)) { //Csak megfelelő pozícióban tud kanyarodni
                if (!dir.equals(requestedDir))
                    trail.put(new Point(head), 0); //Ha megváltozott a kígyó iránya, akkor egy új pontot veszünk fel
                setDir(requestedDir); //Megváltoztatjuk az irányát
            }
            if (dir.equals(Direction.UP)) { //Mozgatjuk a fejet
                head.translate(0, -1);
            } else if (dir.equals(Direction.RIGHT)) {
                head.translate(1, 0);
            } else if (dir.equals(Direction.DOWN)) {
                head.translate(0, 1);
            } else if (dir.equals(Direction.LEFT)) {
                head.translate(-1, 0);
            }


            for (int j = 1; j < points.size(); j++) { //Mozgatjuk a kígyó testének az összes pontját
                Iterator trailIterator = trail.keySet().iterator();

                if (trail.keySet().isEmpty()) //Ha egy pont sincs a a halmazban, akkor csak megy az előtte lévő pont irányába
                    points.get(j).translateOnePixelToTargetPos(points.get(j - 1));

                while (trailIterator.hasNext()) {
                    Point p = (Point) trailIterator.next();
                    if (trail.get(p) < j) { //Ha a vizsgált pont a kígyón még nem ment át a kanyaron
                        if (points.get(j).equals(p)) { //Ha a kanyarban van akkor megnöveljük a kanyarpont értékét eggyel
                            trail.put(p, trail.get(p) + 1);

                            points.get(j).translateOnePixelToTargetPos(points.get(j - 1)); //És mozgatjuk az előtte lévő pont irányába
                            break;
                        }
                        points.get(j).translateOnePixelToTargetPos(p); //Mozgatjuk a következő kanyarpont irányába
                        break;
                    }
                    if (!trailIterator.hasNext()) //Ha nem break-elt még ki akkor is elmozdítjuk az előtte lévőnek az irányába
                        points.get(j).translateOnePixelToTargetPos(points.get(j - 1));
                }
            }

            //Ha volt kérés új pontokra, végigiterálunk rajtuk
            Iterator iter = requestedNewPoints.keySet().iterator();
            if (iter.hasNext()) {
                Point firstPoint = (Point) iter.next();
                if (requestedNewPoints.get(firstPoint) == coveredPixels) { //Ha megegyezik a kígyó által megtett távolság és a pont értéke, akkor hozzáadjuk
                    points.add(new Point(firstPoint));
                    requestedNewPoints.remove(firstPoint);
                }
            }
        }

        Point toBeRemoved = null;
        for (Point p : trail.keySet()) //Végigmegyünk a kanyarokon
            if (trail.get(p) == points.size() - 1) //Ha valamelyik kanyaron átment már az összes, akkor kitöröljük
                toBeRemoved = p;
        trail.remove(toBeRemoved);
    }

    /**
     * Kígyó helyes helyen van-e, hogy forduljon.
     *
     * @param cellSize Körök mérete.
     * @return Boolean érték.
     */
    private boolean onBorder(int cellSize) {
        return ((double) head.y / cellSize == Math.floor(head.y / cellSize) && ((double) head.x / cellSize == Math.floor(head.x / cellSize))); //Megnézi hogy határon van-e
    }

    /**
     * Ha a kígyó felvesz egy gyümölcsöt, kér egy új pontot a logikai egységétől.
     */
    void requestNewPoint() {
        int cellSize = 16;
        requestedNewPoints.put(new Point(points.get(points.size() - 1)), coveredPixels + cellSize); //Az utolsó pont helyére tesszük, majd ha az elmegy onnan
    }

    /**
     * Kígyó kirajzolása.
     *
     * @param gc       JavaFX GraphicsContext-je
     * @param cellSize Körök mérete.
     */
    public void draw(GraphicsContext gc, int cellSize) {
        gc.setFill(snakeColor);

        for (Point p : points) {
            gc.fillOval(p.getX(), p.getY(), cellSize, cellSize);
        }

    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Point getHead() {
        return head;
    }

    public int getVelocity() {
        return velocity;
    }

    public Direction getDir() {
        return dir;
    }

    public Color getSnakeColor() {
        return snakeColor;
    }

    public boolean isAlive() {
        return alive;
    }

    public Direction getRequestedDir() {
        return requestedDir;
    }

    public int getCoveredPixels() {
        return coveredPixels;
    }

    public LinkedHashMap<Point, Integer> getTrail() {
        return trail;
    }

    public LinkedHashMap<Point, Integer> getRequestedNewPoints() {
        return requestedNewPoints;
    }

    private void setDir(Direction dir) {
        this.dir = dir;
    }

    /**
     * Legközelebbi irány settere.
     *
     * @param dir Irány.
     */
    void setRequestedDir(Direction dir) {
        switch (dir) {
            case UP:
                if (this.dir != Direction.DOWN)
                    this.requestedDir = Direction.UP;
                break;
            case DOWN:
                if (this.dir != Direction.UP)
                    this.requestedDir = Direction.DOWN;
                break;
            case LEFT:
                if (this.dir != Direction.RIGHT)
                    this.requestedDir = Direction.LEFT;
                break;
            case RIGHT:
                if (this.dir != Direction.LEFT)
                    this.requestedDir = Direction.RIGHT;
                break;
        }
    }
}


