package org.example.Java3DShooter;

import javafx.scene.shape.Box;
import java.util.Random;

public class Enemy extends Box {
    private static double[][] groundPlaneBoundingBox;
    private static final double WIDTH;
    private static final double HEIGHT;
    private static final double DEPTH;

    /**
     * initializes a new Enemy
     * @throws NullPointerException if the groundPlaneBoundingBox was not set beforehand
     */
    public Enemy() throws NullPointerException {
        super(WIDTH, HEIGHT, DEPTH);

        if (groundPlaneBoundingBox == null) {
            throw new NullPointerException("No groundPlaneBoundingBox set");
        }
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
