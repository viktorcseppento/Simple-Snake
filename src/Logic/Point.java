package Logic;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Point {

    protected int x;
    protected int y;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void setPoint(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Eltolja a pontot.
     *
     * @param dx X koordinájának eltolása.
     * @param dy Y koordinátájának eltolása.
     */
    void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Távolság egy másik ponttól.
     *
     * @param other Másik pont.
     * @return Double távolság
     */
    double distanceFromOtherPoint(Point other) {
        int xDifference = abs(x - other.x);
        int yDifference = abs(y - other.y);
        return sqrt(xDifference * xDifference + yDifference * yDifference); //Pitagorasz-tétel
    }

    /**
     * Két pont metszi-e egymást.
     *
     * @param other    Másik pont.
     * @param diameter Pontok átmérője.
     * @return Boolean érték, hogy ütköznek-e.
     */
    boolean intersect(Point other, int diameter) {
        return distanceFromOtherPoint(other) < diameter;
    }

    /**
     * Eltolja a pontot 1 pixellel egy másik pont irányába, ha egy vonalba esnek.
     *
     * @param other Másik pont.
     */
    void translateOnePixelToTargetPos(Point other) {
        if (other.x > x)
            translate(1, 0);
        if (other.x < x)
            translate(-1, 0);
        if (other.y > y)
            translate(0, 1);
        if (other.y < y)
            translate(0, -1);
    }

    /**
     * Pontok közül visszaadja a legközelebbit.
      * @param others Többi pont.
     * @return Legközelebbi pont.
     */
    Point closestPoint(ArrayList<? extends Point> others){
        Point min = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for(Point p:others){
            if(this.distanceFromOtherPoint(p) < minDistance){
                minDistance = this.distanceFromOtherPoint(p);
                min = p;
            }
        }
        return min;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
