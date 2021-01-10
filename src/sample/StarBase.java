package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StarBase extends LiveBlock{
    int radius = 50;

    public StarBase(double x, double y) {
        super(x, y);
        setMaxLife(1000);
    }

    @Override
    void Render(GraphicsContext context) {
        context.setFill(Color.YELLOWGREEN);
        context.fillOval(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2
        );

        context.setFill(Color.YELLOWGREEN);
        context.setStroke(Color.GREEN);
        DrawLife(x - radius, y + radius + 5, context);
    }

    public void hit(Enemy enemy){
        enemy.life = 0;
        life -= enemy.power;
    }
}
