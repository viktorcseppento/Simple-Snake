package Logic;

import GUI.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class Fruit extends Point implements Drawable {
    private Color fruitColor;

    /**
     * @param loop       Játék magja.
     * @param fruitColor Gyümölcs színe.
     */
    public Fruit(GameLoop loop, Color fruitColor) { //Random helyre leteszi
        this.fruitColor = fruitColor;

        boolean validPoint;
        Random rand = new Random();
        do {
            validPoint = true;
            x = rand.nextInt(loop.getColumns()) * loop.getCellSize();
            y = rand.nextInt(loop.getRows()) * loop.getCellSize();

            for (Fruit f : loop.getFruits()) //Többi gyümölcsre megnézzük
                if (f.intersect(this, loop.getCellSize()))
                    validPoint = false;

            for (Snake s : loop.getEnemies()) { //Ha beleér valamelyik ellenségbe
                for (Point p : s.getPoints()) {
                    if (p.intersect(this, loop.getCellSize()))
                        validPoint = false;
                }
            }
            for (Point p : loop.getSnake().getPoints()) { //Ha beleér a játékosba
                if (p.intersect(this, loop.getCellSize()))
                    validPoint = false;
            }

        } while (!validPoint);
    }

    /**
     * Gyümölcsöt kirajzolja.
     *
     * @param gc       JavaFX GraphicContext.
     * @param cellSize Gyümölcs mérete.
     */
    public void draw(GraphicsContext gc, int cellSize) {
        gc.setFill(fruitColor);
        gc.fillOval(x, y, cellSize, cellSize);

    }
}
