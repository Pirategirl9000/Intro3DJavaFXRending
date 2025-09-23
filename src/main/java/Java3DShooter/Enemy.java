package Java3DShooter;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Enemy extends Box {

    /**
     * The bounding box of the ground plane as defined by {{xMin, xMax}, {yMin, yMax}, {zMin, zMax}}
     */
    private static double[][] groundPlaneBoundingBox;

    /**
     * Width of the enemy's sprite
     */
    private static final double WIDTH = 10;

    /**
     * Height of the enemy's sprite
     */
    private static final double HEIGHT = 20;

    /**
     * Depth of the enemy's sprite (length of z-axis)
     */
    private static final double DEPTH = 10;

    /**
     * Color of the enemy's sprite
     */
    private static final Color COLOR = Color.RED;

    /**
     * initializes a new Enemy
     * <p>
     * Ensure that you call {@link #setGroundPlaneBoundingBox(double[][])} beforehand so the enemy's spawn coordinates can be determined
     * @throws IllegalArgumentException if the {@link #setGroundPlaneBoundingBox(double[][])} was not called beforehand
     */
    public Enemy() throws IllegalArgumentException {
        super(WIDTH, HEIGHT, DEPTH);

        // Not the best practice way to do things, but it ensures we have fast creation of enemies
        // Better than passing the groundPlane every time we create an enemy
        if (groundPlaneBoundingBox == null) {
            throw new IllegalArgumentException("No groundPlaneBoundingBox set, call Enemy.setGroundPlaneBoundingBox(double[][]) before instantiating any Enemies");
        }

        PhongMaterial material = new PhongMaterial(COLOR);
        super.setMaterial(material);
    }

    /**
     * Sets the Ground plane's bounding box which is used to determine where an enemy can spawn
     * </p>
     * Must be called before instantiation of an Enemy
     * @param boundingBox the bounding box as defined by {{xMin, xMax}, {yMin, yMax}, {zMin, zMax}}
     */
    public static void setGroundPlaneBoundingBox(double[][] boundingBox) {groundPlaneBoundingBox = boundingBox;}

    /**
     * Returns the set groundPlaneBoundingBox
     * @return the bounding box as defined by {{xMin, xMax}, {yMin, yMax}, {zMin, zMax}}
     */
    public static double[][] getGroundPlaneBoundingBox() {return groundPlaneBoundingBox;}
}
