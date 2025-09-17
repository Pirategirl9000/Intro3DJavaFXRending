package org.example.fx_3d;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.util.Map;

public class Player extends Group {
    private int height;
    private int width;
    private int depth;

    private double[] position = {0, 0, 0};

    /**
     * The camera for the 3D environment, initialized through the initializeCamera(args) function
     */
    private final PerspectiveCamera camera = new PerspectiveCamera(true);

    /**
     * Current speed at which our camera is tilting, this value is used when updating the angle of the camera (x, y, z)
     */
    private final double[] cameraTiltVelocity = {0, 0, 0};

    /**
     * Current speed at which our camera is moving, this value is determined by the lateral angle of the camera (xTilt) and is multiplied by the SPEED
     */
    private final double[] cameraVelocity = {0, 0, 0};  // x, y, z move velocity

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
     * The speed at which the camera can move. Serves as a magnitude for our motion vectors
     */
    private final double SPEED = 0.5;

    /**
     * Look speed for the camera, impacts how fast the camera will tilt
     */
    private final double LOOKSPEED = 0.5;

    public Player(int x, int y , int z, int farClip, int nearClip) {
        initializeCamera(x, y, z, farClip, nearClip, new Transform[] {xTilt, yTilt});
    }

    public Player(int x, int y, int z) {
        this(x, y, z, 1000, 10);
    }

    public Player() {
        this(0, 0, -200, 1000, 10);
    }


    public PerspectiveCamera getCamera() {
        return this.camera;
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
    private double[] getMotionVector(double angle) {
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
        camera.setTranslateX(x);
        camera.setTranslateY(y);
        camera.setTranslateZ(z);
        camera.setNearClip(nearClip);
        camera.setFarClip(farClip);
        camera.getTransforms().addAll(transforms);
    }

    public void move(Map<String, Boolean> keysHeld) {
        // Reset the values through index assignment since the arrays are final
        cameraVelocity[0] = 0;
        cameraVelocity[1] = 0;
        cameraVelocity[2] = 0;
        cameraTiltVelocity[0] = 0;
        cameraTiltVelocity[1] = 0;
        cameraTiltVelocity[2] = 0;


        // Calculate our motionVectors for x and z axial movement
        double[] zMotionVector = getMotionVector(xTilt.getAngle());
        double[] xMotionVector = getMotionVector(xTilt.getAngle() + 90);

        // Handle the different key presses here
        // We do it this way so there is dynamic movement where you can turn the camera and move at the same time
        for (String key : keysHeld.keySet()) {
            switch (key) {
                // Camera Controls
                case "Up":
                    cameraTiltVelocity[1] = LOOKSPEED;
                    break;
                case "Down":
                    cameraTiltVelocity[1] = -LOOKSPEED;
                    break;
                case "Left":
                    cameraTiltVelocity[0] = -LOOKSPEED;
                    break;
                case "Right":
                    cameraTiltVelocity[0] = LOOKSPEED;
                    break;

                // Movement Controls
                case "W":
                    cameraVelocity[0] = zMotionVector[1] * SPEED;
                    cameraVelocity[2] = zMotionVector[0] * SPEED;
                    break;
                case "S":
                    cameraVelocity[0] = zMotionVector[1] * -SPEED;
                    cameraVelocity[2] = zMotionVector[0] * -SPEED;
                    break;
                case "A":
                    cameraVelocity[0] = xMotionVector[1] * -SPEED;
                    cameraVelocity[2] = xMotionVector[0] * -SPEED;
                    break;
                case "D":
                    cameraVelocity[0] = xMotionVector[1] * SPEED;
                    cameraVelocity[2] = xMotionVector[0] * SPEED;
                    break;
                case "Space":
                    cameraVelocity[1] = -SPEED;
                    break;
                case "Shift":
                    cameraVelocity[1] = SPEED;
                    break;
            }
        }

        camera.setTranslateX(camera.getTranslateX() + cameraVelocity[0]);
        camera.setTranslateY(camera.getTranslateY() + cameraVelocity[1]);
        camera.setTranslateZ(camera.getTranslateZ() + cameraVelocity[2]);

        // Camera Movement
        double newXTilt = xTilt.getAngle() + cameraTiltVelocity[0];
        double newYTilt = yTilt.getAngle() + cameraTiltVelocity[1];

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
