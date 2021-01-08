package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class Tower extends LiveBlock{
    private ArrayList<Block> blocks;
    int radius = 100;
    double fireRate = 0.25;
    double timeFromLastShot = 0;
    private Enemy targetEnemy;
    int power = 25;

    Consumer<Enemy> onEnemyDestroy = null;

    public Tower(double x, double y, ArrayList<Block> blocks, Consumer<Enemy> onEnemyDestroy) {
        super(x, y);
        this.blocks = blocks;
        setMaxLife(100);
        this.onEnemyDestroy = onEnemyDestroy;
    }

    @Override
    void Render(GraphicsContext context) {
        context.setFill(Color.YELLOW);
        context.fillOval(
                x - 10,
                y - 10,
                10 * 2,
                10 * 2
        );
        context.setStroke(Color.YELLOW);
        context.strokeOval(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2
        );
        if (targetEnemy != null && targetEnemy.life > 0){
            context.setStroke(Color.AZURE);
            context.strokeLine(
                    x, y, targetEnemy.x, targetEnemy.y
            );
        }
    }

    @Override
    void UpdateState(double delta) {
        timeFromLastShot += delta;
        if (timeFromLastShot > fireRate){
            // поиск ближайшего врага
            blocks.stream()
                    .filter(block -> block instanceof Enemy)
                    .map(block -> (Enemy)block)
                    .min(Comparator.comparing(enemy -> {
                        double gX = enemy.x - x;
                        double gY = enemy.y - y;
                        double length = Math.sqrt(gX * gX + gY * gY);

                        return length;
                    })).ifPresent(enemy -> {
                double gX = enemy.x - x;
                double gY = enemy.y - y;
                double length = Math.sqrt(gX * gX + gY * gY);
                if (length < radius){
                    targetEnemy = enemy;
                }
            });
            if (targetEnemy != null && targetEnemy.life > 0){
                targetEnemy.life -= power;
                if (onEnemyDestroy != null && targetEnemy.life <= 0){
                    onEnemyDestroy.accept(targetEnemy);
                }
            }
            timeFromLastShot = 0;
        }
    }
}
