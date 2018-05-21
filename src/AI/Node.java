package AI;

import Logic.Snake;

import static java.lang.Math.abs;

/**
 * Gráfalgoritmus által használ csúcs.
 */
public class Node implements Comparable {
    private int x, y; //Koordináták
    private Boolean obstruction; //Akadály-e
    private int gCost; //Kezdőponttól a távolság
    private int hCost; //Végponttól a távolság
    private Node cameFrom; //A csúcsot tartalmazó útvonalon a csúcs előtti csúcs

    /**
     * Incializáló konstruktor koordináták alapján.
     *
     * @param x X koordináta.
     * @param y Y koordináta.
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        obstruction = false;
        gCost = Integer.MAX_VALUE / 2;
        hCost = Integer.MAX_VALUE / 2;
        cameFrom = null;
    }

    /**
     * Megmondja egy másik csúcstól a távolságot.
     *
     * @param other Másik csúcs.
     * @return Távolság.
     */
    int distanceFromNode(Node other) {
        return abs(other.x - x) + abs(other.y - y); //Keresztbe nem lehet menni
    }

    /**
     * FCost alapján Comparable interface-ből delegált függvény.
     *
     * @param o Másik csúcs.
     * @return 0 ha egyenlő a kettő csúcsnak az fCost-ja, negatívat ha a paraméternek nagyobb, pozitívat, ha a példányak nagyobb.
     */
    @Override
    public int compareTo(Object o) {
        Node other = (Node) o;
        return this.getfCost() - other.getfCost();
    }

    /**
     * Megmondja melyik irányban van egy másik pont.
     *
     * @param other Másik pont.
     * @return Irány.
     */
    Snake.Direction toOtherNode(Node other) {
        if (x == other.x) {
            if (y > other.y)
                return Snake.Direction.UP;
            else
                return Snake.Direction.DOWN;
        } else if (y == other.y) {
            if (x > other.x)
                return Snake.Direction.LEFT;
            else
                return Snake.Direction.RIGHT;
        } else
            return null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Boolean getObstruction() {
        return obstruction;
    }

    public int getgCost() {
        return gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public int getfCost() {
        return gCost + hCost;
    }

    public Node getCameFrom() {
        return cameFrom;
    }

    void setObstruction(Boolean obstruction) {
        this.obstruction = obstruction;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    void sethCost(int hCost) {
        this.hCost = hCost;
    }

    void setCameFrom(Node cameFrom) {
        this.cameFrom = cameFrom;
    }

    @Override
    public String toString() {
        return "Node{" + "x=" + x + ", y=" + y + '}';
    }
}
