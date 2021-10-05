package fr.takehere.raycaster;

import fr.takehere.ethereal.Game;
import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.utils.Vector2;
import fr.takehere.ethereal.utils.maths.MathUtils;

import java.awt.*;
import java.util.HashMap;

public class Map {
    private static Map instance = Map.get();

    int mapX = 50, mapY = 50, mapS = 500;
    int map[] = {
            1,1,1,1,1,1,1,1,1,1,
            1,0,0,0,0,0,0,0,0,1,
            1,1,1,1,1,1,1,1,0,1,
            1,0,1,0,0,0,0,0,0,1,
            1,0,1,1,0,1,1,1,1,1,
            1,0,1,1,0,1,2,2,2,1,
            1,0,0,0,0,1,2,3,2,1,
            1,0,1,1,1,1,2,0,2,1,
            1,0,0,0,0,0,0,0,0,1,
            1,1,1,1,1,1,1,1,1,1
    };

    /*
    int mapX = 25, mapY = 25, mapS = 500;
    int map[] = {
            1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
            1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,2,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,
            1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,
            1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,2,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,
            1,0,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
            1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
    };
     */

    /*
    int mapX = 125, mapY = 125, mapS = 500;
    int map[] = {
            1,1,1,1,
            1,0,0,1,
            1,0,2,1,
            1,1,1,1,
    };
     */

    HashMap<Rectangle, Integer> rectanglesData;

    private Map() {
        generateMap();
    }

    public void drawRays(Game game){
        Graphics2D g2d = game.gameWindow.getGraphics();

        Actor player = Raycaster.getInstance().player;
        Vector2 playerCenterLocation = new Vector2(player.dimension.width/2, player.dimension.height/2).add(player.location);

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(10));

        float rayAngleAddition = 0;
        int resolutionMultiplicator = 2;
        int rayNumber = 60 * resolutionMultiplicator;
        double playerAngle = player.rotation - rayNumber/(2*resolutionMultiplicator);

        float lineThickness = Raycaster.getInstance().gameWindow.getWidth() / rayNumber;
        Vector2 lastRayLocation = new Vector2(Raycaster.getInstance().gameWindow.getWidth() / 2, 0);

        for (int i = 0; i < rayNumber; i++) {
            double distance = 0;
            boolean calculate = true;
            while (calculate){
                double rayAngle = playerAngle + rayAngleAddition;
                Vector2 targetLocation = new Vector2((int) (playerCenterLocation.x + Math.cos(Math.toRadians(rayAngle)) * distance)
                        ,(playerCenterLocation.y + Math.sin(Math.toRadians(rayAngle)) * distance));

                for (java.util.Map.Entry<Rectangle, Integer> entry : rectanglesData.entrySet()) {
                    if (MathUtils.isColliding(new Rectangle((int) targetLocation.x, (int) targetLocation.y, 1, 1), entry.getKey())){
                        if (distance == 0) distance=1;
                        distance = distance * Math.cos(Math.toRadians(player.rotation - (rayAngle)));
                        float lineH = (float) ((Raycaster.getInstance().gameWindow.getHeight() * 60) / distance);
                        float lineUp = (Raycaster.getInstance().gameWindow.getHeight()/2) - (lineH/2);

                        Color wallColor = convertColor(entry.getValue());

                        g2d.setColor(wallColor);
                        g2d.setStroke(new BasicStroke(5));
                        g2d.drawLine((int) playerCenterLocation.x, (int) playerCenterLocation.y, (int) targetLocation.x, (int) targetLocation.y);

                        /*
                        if (MathUtils.isBetween((float) normalize(rayAngle), 90, 270)){
                            float hsbVals[] = Color.RGBtoHSB( wallColor.getRed(),
                                    wallColor.getGreen(),
                                    wallColor.getBlue(), null );
                            wallColor = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * hsbVals[2] );
                        }
                         */

                        g2d.setColor(wallColor);
                        g2d.setStroke(new BasicStroke(lineThickness));
                        g2d.drawLine((int) lastRayLocation.x, (int) lineUp, (int) lastRayLocation.x, (int) (lineH + lineUp));

                        //Draw Sky
                        g2d.setColor(new Color(7, 140, 199));
                        g2d.drawLine((int) lastRayLocation.x, 0, (int) lastRayLocation.x, (int) lineUp);

                        //Draw floor
                        g2d.setColor(new Color(90, 90, 90));
                        g2d.drawLine((int) lastRayLocation.x, (int) (lineH + lineUp), (int) lastRayLocation.x, (int) (lineH + lineUp + lineUp));

                        lastRayLocation = new Vector2(lastRayLocation.x + lineThickness / 2, lastRayLocation.y);
                        calculate = false;
                    }
                }
                distance+=1;
            }
            rayAngleAddition = rayAngleAddition + (1f/resolutionMultiplicator);
        }

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawLine((int) playerCenterLocation.x, (int) playerCenterLocation.y, (int) (playerCenterLocation.x + Math.cos(Math.toRadians(player.rotation)) * 40), (int) (playerCenterLocation.y + Math.sin(Math.toRadians(player.rotation)) * 40));
    }

    public static double normalize(final double angle) {
        return (angle >= 0 ? angle : (360 - ((-angle) % 360))) % 360;
    }

    public static double normalizeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    public void drawMap(Game game){
        Graphics2D g2d = game.gameWindow.getGraphics();

        for (java.util.Map.Entry<Rectangle, Integer> entry : rectanglesData.entrySet()) {
            Color color = convertColor(entry.getValue());
            float hsbVals[] = Color.RGBtoHSB( color.getRed(),
                    color.getGreen(),
                    color.getBlue(), null );
            color = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * hsbVals[2] );

            g2d.setColor(color);
            g2d.fillRect(entry.getKey().x, entry.getKey().y, entry.getKey().width, entry.getKey().height);
        }
    }

    public void generateMap(){
        rectanglesData = new HashMap<>();
        int caseNumber = 0;

        for (int y = 0; y < mapS; y+=mapY) {
            for (int x = 0; x < mapS; x+=mapX) {
                if (map[caseNumber] != 0){
                    rectanglesData.put(new Rectangle(x, y, mapX, mapY), map[caseNumber]);
                    if (map[caseNumber] == 3)
                        Raycaster.getInstance().mapHitbox = new Rectangle(x, y, mapX, mapY);
                }

                caseNumber++;
            }
        }
    }

    public Color convertColor(int data){
        switch (data) {
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.GREEN;
        }

        return null;
    }

    public static Map get(){
        if(instance == null)
            instance = new Map();
        return instance;
    }
}
