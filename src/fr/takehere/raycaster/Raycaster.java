package fr.takehere.raycaster;

import fr.takehere.ethereal.Game;
import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.utils.ImageUtil;
import fr.takehere.ethereal.utils.Vector2;
import fr.takehere.ethereal.utils.maths.MathUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Raycaster extends Game {
    private static Raycaster instance;

    public Actor player;

    public static void main(String[] args) {
        instance = new Raycaster("Raycaster", 500,1000,60);
    }

    public Raycaster(String title, int height, int width, int targetFps) {
        super(title, height, width, targetFps);
    }

    @Override
    public void init() {
        player = new Actor(new Vector2(75,75), new Dimension(20,20), ImageUtil.placeholder, "Player", this);
    }

    float playerSpeed = 2;
    float turnSpeed = 3;

    int right = KeyEvent.VK_RIGHT;
    int left = KeyEvent.VK_LEFT;
    int up = KeyEvent.VK_UP;
    int down = KeyEvent.VK_DOWN;

    public Rectangle mapHitbox;

    @Override
    public void gameLoop(double v) {
        Graphics2D g2d = this.gameWindow.getGraphics();

        g2d.fillRect(0,0, gameWindow.getWidth(), gameWindow.getHeight());

        //Player movement
        Vector2 forwardVector = new Vector2(Math.sin(Math.toRadians(player.rotation - 90)) * 1, (Math.cos(Math.toRadians(player.rotation - 90)) * 1) * -1);
        if (gameWindow.isPressed(down)) player.velocity = forwardVector.multiply(playerSpeed);
        if (gameWindow.isPressed(up)) player.velocity = forwardVector.multiply(playerSpeed *-1);
        if (!gameWindow.isPressed(up) && !gameWindow.isPressed(down)) player.velocity = new Vector2(0,0);

        if (gameWindow.isPressed(left)) player.rotation -= turnSpeed;
        if (gameWindow.isPressed(right)) player.rotation += turnSpeed;

        //Collision detection
        for (Rectangle rectangle : Map.get().rectanglesData.keySet()) {
            if (Map.get().rectanglesData.get(rectangle) != 3){
                if (MathUtils.isColliding(new Rectangle((int) player.location.add(player.velocity).x, (int) player.location.add(player.velocity).y, 1,1), rectangle)){
                    player.velocity = new Vector2(0,0);
                }
            }
        }

        Map.get().drawMap(this);
        Map.get().render(this);

        if (MathUtils.isColliding(player.boundingBox.getBounds(), mapHitbox)){
            Map.get().map = new int[]{
                    1,1,1,1,
                    1,0,0,1,
                    1,0,2,1,
                    1,1,1,1,
            };
            Map.get().mapX = 125;
            Map.get().mapY = 125;
            Map.get().generateMap();
            Map.get().rectanglesData.put(new Rectangle(200,150, 20,20), 2);

            player.location = new Vector2(150,150);
        }
    }

    public static Raycaster getInstance() {
        return instance;
    }
}
