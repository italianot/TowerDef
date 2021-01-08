package sample;

import com.sun.prism.Graphics;
import javafx.scene.canvas.GraphicsContext;

public class LiveBlock extends Block{

    int life;
    int maxLife;

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
        this.life = maxLife;
    }

    public LiveBlock(double x, double y) {
        super(x, y);
    }

    public void DrawLife(double x, double y, GraphicsContext context){
        context.fillRect(x, y, life / 10, 10);
        context.strokeRect(x, y, maxLife / 10, 10);
    }
}
