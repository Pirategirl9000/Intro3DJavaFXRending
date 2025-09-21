package org.example.Java3DShooter;

import javafx.scene.shape.Box;

public class Bullet extends Box {
    /**
     * Width of the bullet
     */
    private static final double WIDTH = 5;

    /**
     * Height of the bullet
     */
    private static final double HEIGHT = 5;

    /**
     * Depth (z length) of the bullet
     */
    private static final double DEPTH = 5;

    /**
     * Speed of the bullet, serves as a magnitude for the motion vector
     */
    private static final double BULLETSPEED = 1;

    /**
     * Number of move frames before the bullet dies
     */
    private int timeToLive = 3000;

    /**
     * X, Y, Z velocity of the bullet - Does not decrease as it travels
     */
    private final double[] velocity;

    /**
     * Creates a new bullet at the set position
     */
    public Bullet(double x, double y, double z, double xVel, double yVel, double zVel) {
        // Hey that's cool, you can't reference these fields(WIDTH, HEIGHT, DEPTH) unless they are static because super precedes the object's creation
        // I guess that's why super() has to be the first call in the constructor since the parent class has to be initialized first before the child
        // I did not know that :)
        super(WIDTH, HEIGHT, DEPTH);

        super.setTranslateX(x);
        super.setTranslateY(y);
        super.setTranslateZ(z);

        velocity = new double[] {xVel * BULLETSPEED, yVel * BULLETSPEED, zVel * BULLETSPEED};
    }

    /**
     * Returns the bullet's remaining timeToLive
     * @return Frames left before death
     */
    public int getTimeToLive() { return timeToLive; }

    /**
     * Moves the bullet forward by one frame
     */
    public void move() {
        this.setTranslateX(this.getTranslateX() + velocity[0]);
        this.setTranslateY(this.getTranslateY() + velocity[1]);
        this.setTranslateZ(this.getTranslateZ() + velocity[2]);

        timeToLive--;
    }
}
