package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    public Canvas mainCanvas;

    ArrayList<Block> blocks = new ArrayList<>();
    StarBase starBase;
    Instant lastUpdateInstant = null;

    double timeFromLastEnemyCreate = 0;
    double enemyCreateRate = 1;
    int totalEnemies = 0;
    int money = 500;
    int score = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(40),
                this::onTimerTick

        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        initBlocks();
    }

    //добавление starBase
    private void initBlocks() {
        starBase = new StarBase(mainCanvas.getWidth() / 2, mainCanvas.getHeight() / 2);
        blocks.add(starBase);
        /*blocks.add(new Tower(
                mainCanvas.getWidth() / 2 - 50,
                mainCanvas.getHeight() / 2 - 50,
                blocks,
                this::onEnemyDestroy
        ));
        blocks.add(new Tower(
                mainCanvas.getWidth() / 2 + 50,
                mainCanvas.getWidth() / 2 - 50,
                blocks,
                this::onEnemyDestroy
        ));
        blocks.add(new Tower(
                mainCanvas.getWidth() / 2 + 50,
                mainCanvas.getWidth() / 2 + 50,
                blocks,
                this::onEnemyDestroy
        ));
        blocks.add(new Tower(
                mainCanvas.getWidth() / 2 - 50,
                mainCanvas.getWidth() / 2 + 50,
                blocks,
                this::onEnemyDestroy
        ));*/
    }


    private void onEnemyDestroy(Enemy enemy) {
        totalEnemies += 1;
        money += enemy.maxLife;
        score += enemy.maxLife;
    }

    private void onTimerTick(javafx.event.ActionEvent actionEvent) {
        UpdateState();
        Render();
    }

    void Render(){
        GraphicsContext graphicsContext2D = mainCanvas.getGraphicsContext2D();
        graphicsContext2D.setFill(Color.BLACK);
        graphicsContext2D.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        for (Block block : blocks) {
            block.Render(graphicsContext2D);
        }
        RenderUI(graphicsContext2D);
    }

    private void RenderUI(GraphicsContext context) {
        context.setFill(Color.YELLOW);
        context.setFont(Font.font("Verdana", 16));
        context.fillText(
                String.format("Money: %s", money),
                10,20
        );
        context.fillText(
                String.format("Enemies killed: %s", totalEnemies),
                140,20
        );
        context.fillText(
                String.format("Score: %s", score),
                330,20
        );

        if (starBase.life <= 0){
            context.setFill(Color.RED);
            context.setFont(Font.font("Verdana", 45));
            context.fillText(
                    String.format("YOU DIED"),
                    mainCanvas.getWidth()/3, 150
            );
        }
    }


    void UpdateState(){
        Instant now = Instant.now();
        double delta = 0;
        if (lastUpdateInstant != null){
            delta = (double) java.time.Duration.between(lastUpdateInstant, now).toMillis() / 1000;
        }

        generateEnemies(delta);

        for (Block block : blocks) {
            block.UpdateState(delta);
        }

        List<LiveBlock> blocksToRemove = blocks.stream()
                .filter(block -> block instanceof LiveBlock)
                .map(block -> (LiveBlock) block)
                .filter(liveBlock -> liveBlock.life <= 0)
                .collect(Collectors.toList());

        blocks.removeAll(blocksToRemove);

        lastUpdateInstant = now;
    }

    private void generateEnemies(double delta) {

        if (starBase.life <= 0){
            return;
        }

        if (timeFromLastEnemyCreate < enemyCreateRate){
            timeFromLastEnemyCreate += delta;
            return;
        }

        Integer totalEnemyLife = blocks.stream()
                .filter(block -> block instanceof Enemy)
                .map(block -> (Enemy) block)
                .map(enemy -> enemy.life)
                .reduce(0, (sum, life) -> sum + life);

        Double totalPower = blocks.stream()
                .filter(block -> block instanceof Tower)
                .map(block -> (Tower) block)
                .map(tower -> (1d / tower.fireRate) * tower.power)
                .reduce(0d, (aDouble, aDouble2) -> aDouble + aDouble2);

        if (totalEnemyLife >= totalPower){
            return;
        }

        int enemyMaxLife = (int) (totalPower - totalEnemyLife);

        timeFromLastEnemyCreate = 0;

        int direction = ThreadLocalRandom.current().nextInt(0, 360);

        double x = 0;
        double y = 0;

        if (direction >= 0 && direction < 90){
            x = mainCanvas.getWidth();
            y = ThreadLocalRandom.current().nextInt(0, (int)mainCanvas.getHeight());
        } else if (direction >= 90 && direction < 180) {
            x = ThreadLocalRandom.current().nextInt(0, (int)mainCanvas.getHeight());
            y = 0;
        } else if (direction >= 180 && direction < 270) {
            x = 0;
            y = ThreadLocalRandom.current().nextInt(0, (int)mainCanvas.getHeight());
        } else if (direction >= 270 && direction < 360) {
            x = ThreadLocalRandom.current().nextInt(0, (int)mainCanvas.getHeight());
            y = mainCanvas.getHeight();
        }

        Enemy enemy = new Enemy(x, y, starBase);
        enemy.setMaxLife(enemyMaxLife);
        blocks.add(enemy);
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        if (money < 500){
            return;
        }
        money -=500;

        blocks.add(new Tower(
                mouseEvent.getX(),
                mouseEvent.getY(),
                blocks,
                this::onEnemyDestroy
        ));
    }
}
