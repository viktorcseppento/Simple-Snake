package AI;

import Logic.GameLoop;
import Logic.Point;
import Logic.Snake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.floor;


public class PathFinder {

    private GameLoop loop;
    private final int cellSize = 16;
    private Set<Node> nodes;


    public PathFinder(GameLoop loop) {
        this.loop = loop;
        nodes = new HashSet<>();
        for (int i = 0; i < loop.getColumns(); i++) {
            for (int j = 0; j < loop.getRows(); j++) {
                nodes.add(new Node(i, j));
            }
        }
    }


    /**
     * A* algoritmus futtatása, irányt ad vissza, merre forduljon a kígyó.
     *
     * @param snake Kígyó amire meghívjuk.
     * @param fruit Gyümölcs (cél) pontja.
     * @return Új irány.
     */
    public Snake.Direction runAlgorithm(Snake snake, Point fruit) {
        Point head = snake.getHead();
        Snake.Direction headDirection = snake.getDir();
        Node start = getNodeOfHead(head, headDirection);
        Node end = getNodeByPixels(fruit.getX(), fruit.getY());
        if (start == end)
            return headDirection;

        if (start.distanceFromNode(end) > 10) {
            return heuristicDirection(start, end, headDirection);
        }
        Set<Node> closedSet = new HashSet<>();
        Set<Node> openSet = new HashSet<>();
        openSet.add(start); //Elején csak a startcsúcs van benne
        start.setgCost(0); //Start csúcs 0 távolságra van magától
        start.sethCost(start.distanceFromNode(end));
        while (!openSet.isEmpty()) {
            Node current = Collections.min(openSet, null);
            if (current == end) {
                Node iterator;
                for (iterator = end; iterator.getCameFrom().getCameFrom() != null; iterator = iterator.getCameFrom()) { //Iterálunk a kezdő utáni csúcsra
                }
                Snake.Direction returnDir = start.toOtherNode(iterator);
                if (returnDir == null)
                    return headDirection;
                else
                    return returnDir;
            }
            openSet.remove(current);
            closedSet.add(current);

            for (Node neighbour : getNeighbours(current)) {
                if (closedSet.contains(neighbour))
                    continue;
                if (!openSet.contains(neighbour))
                    openSet.add(neighbour);

                int gCostNow = current.getgCost() + 1; //Szomszédja
                if (gCostNow >= neighbour.getgCost()) {
                    continue;
                }
                neighbour.setCameFrom(current);
                neighbour.setgCost(gCostNow);
                neighbour.sethCost(neighbour.distanceFromNode(end));
            }
        }

        //Ide csak akkor jut el, ha nincs útvonal, így csak egy irányt adunk a kígyónak, hogy ne ütközzön semmibe
        Set<Node> startNeighbours = getNeighbours(start);
        if (!startNeighbours.isEmpty())
            return start.toOtherNode(startNeighbours.iterator().next());

        return headDirection; //Ez csak akkor van, ha biztosan meghalna a kígyó
    }

    /**
     * Ha nagyon messze van a gyümölcstől ez keresi az irányt, nem térképezi fel az egész gráfot.
     *
     * @param start         Kezdőpont.
     * @param end           Végpont.
     * @param headDirection Eredeti irány.
     * @return Irány.
     */
    private Snake.Direction heuristicDirection(Node start, Node end, Snake.Direction headDirection) {
        Node min = null;
        int minDistance = Integer.MAX_VALUE;
        for (Node n : getNeighbours(start)) {
            int distanceFromNode = n.distanceFromNode(end);
            if (distanceFromNode < minDistance) {

                minDistance = distanceFromNode;
                min = n;
            }
        }
        if(min != null)
            return start.toOtherNode(min);
        else
            return  headDirection;
    }

    /**
     * Legelső csúcs, ahol a kígyó van.
     *
     * @param head          Kígyó fejének a pontja.
     * @param headDirection Kígyó iránya.
     * @return Csúcs.
     */
    private Node getNodeOfHead(Point head, Snake.Direction headDirection) {
        if (headDirection.equals(Snake.Direction.UP) || headDirection.equals(Snake.Direction.LEFT))
            return getNodeByPixels(head.getX(), head.getY());
        else if (headDirection.equals(Snake.Direction.DOWN))
            return getNodeByPixels(head.getX(), head.getY() + cellSize - 1);
        else
            return getNodeByPixels(head.getX() + cellSize - 1, head.getY());
    }

    /**
     * Egy csúcs szomszédcsúcsainak halmazát visszadja.
     *
     * @param node Vizsgált csúcs.
     * @return Csúcsok halmaza.
     */
    private Set<Node> getNeighbours(Node node) {
        Set<Node> returnSet = new HashSet<>();
        if (node.getX() != 0) { //Ha nem balszélen van
            if (!getNodeByCoordinatesFromSet(node.getX() - 1, node.getY(), nodes).getObstruction()) //Ha nem akadály
                returnSet.add(getNodeByCoordinatesFromSet(node.getX() - 1, node.getY(), nodes)); //Bal szomszéd
        }
        if (node.getX() != loop.getColumns() - 1) { //Ha nem jobbszélen van
            if (!getNodeByCoordinatesFromSet(node.getX() + 1, node.getY(), nodes).getObstruction())
                returnSet.add(getNodeByCoordinatesFromSet(node.getX() + 1, node.getY(), nodes)); //Jobb szomszéd
        }
        if (node.getY() != 0) { //Ha nem balszélen van
            if (!getNodeByCoordinatesFromSet(node.getX(), node.getY() - 1, nodes).getObstruction())
                returnSet.add(getNodeByCoordinatesFromSet(node.getX(), node.getY() - 1, nodes)); //Fölső szomszéd
        }
        if (node.getY() != loop.getRows() - 1) { //Ha nem balszélen van
            if (!getNodeByCoordinatesFromSet(node.getX(), node.getY() + 1, nodes).getObstruction())
                returnSet.add(getNodeByCoordinatesFromSet(node.getX(), node.getY() + 1, nodes)); //Alsó szomszéd
        }
        return returnSet;
    }

    /**
     * Frissíti a csúcsok állapotát.
     */
    public void refreshNodes() {
        resetAllNodes();
        setAllFlags(false);
        setObstructionFlagsByLoop(); //Ahol akadály van ott true lesz a flag

    }


    /**
     * Összes csúcs flagjét egy boolean érték-re állítja.
     *
     * @param value Érték.
     */
    private void setAllFlags(Boolean value) {
        for (Node n : nodes)
            n.setObstruction(value);
    }

    /**
     * Visszaállítja a csúcsok állapotait.
     */
    private void resetAllNodes() {
        for (Node n : nodes) {
            n.setgCost(Integer.MAX_VALUE / 2);
            n.sethCost(Integer.MAX_VALUE / 2);
            n.setCameFrom(null);
        }
    }

    /**
     * Pixelkoordináta alapján keres a nodes halmazban.
     *
     * @param x X koordináta.
     * @param y Y koordináta.
     * @return Talált csúccsal tér vissza.
     */
    private Node getNodeByPixels(int x, int y) {
        int nodeX = (int) floor((double) x / cellSize);
        int nodeY = (int) floor((double) y / cellSize);
        for (Node n : nodes) {
            if (n.getX() == nodeX && n.getY() == nodeY)
                return n;
        }
        return null;
    }

    /**
     * Koordináta alapján keres egy adott halmazban.
     *
     * @param x     Csúcs x koordinátája.
     * @param y     Csúcs y koordinátája.
     * @param nodes Halmaz, amiben keresünk.
     * @return Talált csúcs vagy null.
     */
    private Node getNodeByCoordinatesFromSet(int x, int y, Set<Node> nodes) {
        for (Node n : nodes)
            if (n.getX() == x && n.getY() == y)
                return n;
        return null;
    }

    /**
     * Kígyók testeinek alapján beállítjuk a csúcsok flagjeit.
     */
    private void setObstructionFlagsByLoop() {
        ArrayList<Snake> snakes = new ArrayList<>(loop.getNumberOfEnemies() + 1); //Egy listába tesszük az összes kígyót
        snakes.addAll(loop.getEnemies());
        snakes.add(loop.getSnake());
        for (Snake s : snakes) {
            for (Point p : s.getPoints()) {
                setObstructionFlagBySnakeBody(p);
            }
        }


    }

    /**
     * Kígyó egy test-pontja alapján beállítjuk a hozzá tartozó pontok flagjeit.
     *
     * @param body Kígyó teste.
     */
    private void setObstructionFlagBySnakeBody(Point body) {

        if (inRange(body))
            setObstructionFlagByPoint(body);

        if (inRange(new Point(body.getX() + cellSize - 1, body.getY())))
            setObstructionFlagByPixels(body.getX() + cellSize - 1, body.getY());

        if (inRange(new Point(body.getX(), body.getY() + cellSize - 1)))
            setObstructionFlagByPixels(body.getX(), body.getY() + cellSize - 1);

        if (inRange(new Point(body.getX() + cellSize - 1, body.getY() + cellSize - 1)))
            setObstructionFlagByPixels(body.getX() + cellSize - 1, body.getY() + cellSize - 1);
    }

    /**
     * Pont a játékteren belül van-e
     *
     * @param point Vizsgált pont.
     * @return Érték.
     */
    private boolean inRange(Point point) {
        return (point.getX() >= 0 && point.getX() <= (loop.getColumns() - 1) * cellSize && point.getY() >= 0 && point.getY() <= (loop.getRows() - 1) * cellSize);
    }

    /**
     * Egy pont kordinátájához hozzátartozó csúcs flagjének beállítása.
     *
     * @param point Pont.
     */
    private void setObstructionFlagByPoint(Point point) {
        int column = (int) floor((double) point.getX() / cellSize);
        int row = (int) floor((double) point.getY() / cellSize);
        Node n = getNodeByCoordinatesFromSet(column, row, nodes);
        if (n != null)
            n.setObstruction(true);
    }

    /**
     * Egy koordinátához tartozó csúcs flagjének beállítása.
     *
     * @param x X koordináta.
     * @param y Y koordináta.
     */
    private void setObstructionFlagByPixels(int x, int y) {
        int column = (int) floor((double) x / cellSize);
        int row = (int) floor((double) y / cellSize);
        Node n = getNodeByCoordinatesFromSet(column, row, nodes);
        if (n != null)
            n.setObstruction(true);
    }

}
