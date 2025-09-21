package org.example.Java3DShooter;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.Map;

/**
 * The Player class contains information regarding the player. The player is a type of Group containing the player's hitbox. The player also has a PerspectiveCamera attached
 * to it allowing for first person movement. The player's hitbox and camera are both have position's tied to the move function which will move both Node's in accordance with the defined
 * movement controls when called
 */
public class Player extends Group {
    /**
     * Height of the player's hitbox in pixels
     */
    private final int PLAYERHEIGHT = 20;

    /**
     * Width of the player's hitbox in pixels
     */
    private final int PLAYERWIDTH = 10;

    /**
     * All the current projectiles fired by the player as a list of Bullet's
     */
    private final ArrayList<Bullet> projectiles = new ArrayList<>();

    /**
     * All the projectiles as a group
     */
    private final Group projectilesGroup = new Group();

    /**
     * Depth of the player's hitbox in pixels
     */
    private final int PLAYERDEPTH = 10;

    /**
     * The speed at which the camera can move. Serves as a magnitude for our motion vectors
     */
    private final double SPEED = 0.5;

    /**
     * Look speed for the camera, impacts how fast the camera will tilt
     */
    private final double LOOKSPEED = 0.5;

    /**
     * Field of view of the camera
     */
    private final double FOV = 40F;

    /**
     * The camera for the 3D environment, initialized through the initializeCamera(args) function
     */
    private final PerspectiveCamera camera = new PerspectiveCamera(true);

    /**
     * Hitbox for the player
     */
    private Box hitbox = new Box(PLAYERWIDTH, PLAYERHEIGHT, PLAYERDEPTH);

    /**
     * Current speed at which our camera is tilting, this value is used when updating the angle of the camera (x, y, z)
     */
    private double[] turnVelocity = {0, 0, 0};

    /**
     * Current speed at which our camera is moving, this value is determined by the lateral angle of the camera (xTilt) and is multiplied by the SPEED
     */
    private double[] velocity = {0, 0, 0};  // x, y, z move velocity

    /**
     * The xTilt transformer of the camera
     * <p>
     * It says Rotate.Y_AXIS but this is backwards because of how FX considers the coordinate plane
     */
    private final Rotate xTilt = new Rotate(0, Rotate.Y_AXIS);

    /**
     * The yTilt transformer of the camera
     * <p>
     * It says Rotate.X_AXIS but this is backwards because of how FX considers the coordinate plane
     */
    private final Rotate yTilt = new Rotate(0, Rotate.X_AXIS);

    /**
     * Initializes a player with x, y, and z position as well as a set farClip and nearClip for the camera
     * @param x x position
     * @param y y position
     * @param z z position
     * @param farClip farClip of the camera (render distance)
     * @param nearClip nearClip of the camera (near-render distance)
     */
    public Player(int x, int y , int z, int farClip, int nearClip) {
        initializeCamera(x, y, z, farClip, nearClip, new Transform[] {xTilt, yTilt});
        initializeHitbox(x, y, z);
        this.getChildren().add(projectilesGroup); // Stores and updates the currently living bullets
    }

    /**
     * Initializes a player with a set x, y, and z position
     * @param x x position
     * @param y y position
     * @param z z postition
     */
    public Player(int x, int y, int z) {
        this(x, y, z, 1000, 10);
    }

    /**
     * Initializes a player with default parameters
     */
    public Player() {
        this(0, -10, -200, 5000, 10);
    }

    /**
     * Gets the x position of the player (hitbox and camera)
     * @return x position
     */
    public double getX() { return hitbox.getTranslateX(); }

    /**
     * Gets the y position of the player (hitbox and camera)
     * @return y position
     */
    public double getY() { return hitbox.getTranslateY(); }

    /**
     * Gets the z position of the player (hitbox and camera)
     * @return z position
     */
    public double getZ() { return hitbox.getTranslateZ(); }

    /**
     * Gets the perspective camera of the player
     * @return PerspectiveCamera
     */
    public PerspectiveCamera getCamera() { return this.camera; }

    /**
     * Gets the hitbox of the player
     * @return Box hitbox
     */
    public Box getHitbox() { return this.hitbox; }

    /**
     * Sets the translation properties of a node
     * @param n node to be translated
     * @param x node's new x position
     * @param y node's new y position
     * @param z node's new z position
     */
    private static void setTranslate(Node n, double x, double y, double z) {
        n.setTranslateX(x);
        n.setTranslateY(y);
        n.setTranslateZ(z);
    }

    /**
     * Returns a vector of motion < cos(angle), sin(angle) >
     * <p>
     * This is used to determine the movement on two separate axis based on the angle of the camera allowing forward movement to take you in the direction of view
     * <p>
     * For x and z motion use the camera's xTilt and for z-movement use the return[0] and return[1] for x-movement
     * <p>
     * For x and y motion use the camera's yTilt and then use return[0] for x and return[1] for y
     * @param angle the angle of tilt/inclination
     * @return vector of motion for magnitude = 1
     */
    private double[] calculateMotionVector(double angle) {
        angle = Math.toRadians(angle);
        return new double[] {Math.cos(angle), Math.sin(angle)};
    }

    /**
     * Sets up the camera for the scene
     * @param x initial x position of the camera
     * @param y initial y position of the camera
     * @param z initial z position of the camera
     * @param farClip the far render distance for the camera
     * @param nearClip the near render distance for the camera
     * @param transforms the transforms or rotations for the camera
     */
    private void initializeCamera(int x, int y, int z, int farClip, int nearClip, Transform[] transforms) {
        setTranslate(camera, x, y, z);
        camera.setNearClip(nearClip);
        camera.setFarClip(farClip);
        camera.getTransforms().addAll(transforms);
        camera.setFieldOfView(FOV);
    }

    private void initializeHitbox(int x, int y, int z) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        hitbox.setMaterial(material);
        setTranslate(hitbox, x, y, z);
        this.getChildren().add(hitbox);
    }

    /**
     * Creates a new bullet at the player's position
     */
    private void shoot() {
        double[] xTiltVector = calculateMotionVector(xTilt.getAngle());
        double[] yTiltVector = calculateMotionVector(yTilt.getAngle());

        // Add a new bullet to the projectiles list with the camera's coordinates then the velocity of the x, y, and z axis
        // Y-axis is negative here because of how the y-axis is reversed in the world of programming
        projectiles.add(new Bullet(
                camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(),
                xTiltVector[1], -yTiltVector[1], xTiltVector[0]
        ));

    }

    /**
     * Updates the positions of every bullet and kills any bullets whose timeToLive is expired
     */
    private void moveBullets() {
        for (int i = 0; i < projectiles.size(); i++) {
            Bullet bullet = projectiles.get(i);

            // If the bullet's TTL is expired we remove it from our projectiles list
            if (bullet.getTimeToLive() <= 0) {
                projectiles.remove(bullet);
                continue;
            }

            bullet.move();
        }

        // Reset the projectilesGroup so that it only has the currently living projectiles
        projectilesGroup.getChildren().setAll(projectiles);
    }

    /**
     * Moves the player forward a frame
     * @param keysHeld the keys currently being help
     */
    public void move(Map<String, Boolean> keysHeld) {
        // Update the bullet's positions
        moveBullets();

        // Clear our old velocities so we can reassign them based on the inputs held
        velocity = new double[] {0, 0, 0};
        turnVelocity = new double[] {0, 0, 0};


        // Calculate our motionVectors for x and z axial movement
        double[] zMotionVector = calculateMotionVector(xTilt.getAngle());
        double[] xMotionVector = calculateMotionVector(xTilt.getAngle() + 90);

        // Handle the different key presses here
        for (String key : keysHeld.keySet()) {
            switch (key) {
                // Camera Controls
                case "Up":
                    turnVelocity[1] = LOOKSPEED;
                    break;
                case "Down":
                    turnVelocity[1] = -LOOKSPEED;
                    break;
                case "Left":
                    turnVelocity[0] = -LOOKSPEED;
                    break;
                case "Right":
                    turnVelocity[0] = LOOKSPEED;
                    break;

                // Movement Controls
                case "W":
                    velocity[0] = zMotionVector[1] * SPEED;
                    velocity[2] = zMotionVector[0] * SPEED;
                    break;
                case "S":
                    velocity[0] = zMotionVector[1] * -SPEED;
                    velocity[2] = zMotionVector[0] * -SPEED;
                    break;
                case "A":
                    velocity[0] = xMotionVector[1] * -SPEED;
                    velocity[2] = xMotionVector[0] * -SPEED;
                    break;
                case "D":
                    velocity[0] = xMotionVector[1] * SPEED;
                    velocity[2] = xMotionVector[0] * SPEED;
                    break;
                case "Space":
                    velocity[1] = -SPEED;
                    break;
                case "Shift":
                    velocity[1] = SPEED;
                    break;
                case "Y":
                    shoot();
                    break;
            }
        }

        // Set the new position
        double[] newPosition = {camera.getTranslateX() + velocity[0], camera.getTranslateY() + velocity[1], camera.getTranslateZ() + velocity[2]};
        setTranslate(camera, newPosition[0], newPosition[1], newPosition[2]);
        setTranslate(hitbox, newPosition[0], newPosition[1], newPosition[2]);

        // Camera Movement
        double newXTilt = xTilt.getAngle() + turnVelocity[0];
        double newYTilt = yTilt.getAngle() + turnVelocity[1];

        // Constrain how far they can look up or down to a 180deg range
        if (newYTilt > 90) {
            newYTilt = 90;
        } else if (newYTilt < -90) {
            newYTilt = -90;
        }

        xTilt.setAngle(newXTilt);
        yTilt.setAngle(newYTilt);

    }
}
