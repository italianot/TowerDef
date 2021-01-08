package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends LiveBlock{
    private StarBase starBase;
    int speed = 60;
    int power = 30;

    public Enemy(double x, double y, StarBase starBase) {
        super(x, y);
        this.starBase = starBase;
        setMaxLife(20);
    }

    public int getRadius() {
        return (int) Math.sqrt(maxLife);
    }


    @Override
    void Render(GraphicsContext context) {
        context.setFill(Color.RED);
        context.fillOval(
                x - getRadius() / 2,
                y - getRadius() / 2,
                getRadius(),
                getRadius()
        );
        context.setFill(Color.RED);
        context.setStroke(Color.DARKRED);
        DrawLife(x - getRadius() / 2, y + getRadius() / 2 + 5, context);
    }

    @Override
    void UpdateState(double delta) {
        if (starBase.life <= 0){
            return;
        }

        double gX = starBase.x - x;
        double gY = starBase.y - y;
        double length = Math.sqrt(gX * gX + gY * gY);

        gX /= length;
        gY /= length;

        x += gX * speed * delta;
        y += gY * speed * delta;

        if (starBase.radius + getRadius() > length){
            starBase.hit(this);
        }
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
