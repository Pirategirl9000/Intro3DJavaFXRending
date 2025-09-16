package org.example.fx_3d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    public void setTranslate(Node node, double x, double y, double z) {
        node.setTranslateX(x);
        node.setTranslateY(y);
        node.setTranslateZ(z);
    }

    public double[] getMotionVector(double angle) {
        angle = Math.toRadians(angle);
        return new double[] {Math.sin(angle), Math.cos(angle)};
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("3D Rendering");


        // Create the materials for the flag
        PhongMaterial pinkBanner = new PhongMaterial();
        PhongMaterial blueBanner = new PhongMaterial();
        PhongMaterial whiteBanner = new PhongMaterial();

        pinkBanner.setDiffuseColor(Color.LIGHTPINK);
        blueBanner.setDiffuseColor(Color.LIGHTBLUE);
        whiteBanner.setDiffuseColor(Color.WHITE);


        // Create the boxes used for the banner
        Box[] boxes = new Box[] {
                new Box(100, 20, 40),
                new Box(100, 20, 40),
                new Box(100, 20, 40),
                new Box(100, 20, 40),
                new Box(100, 20, 40),
        };

        // set the trans flag color
        boxes[0].setMaterial(blueBanner);
        boxes[1].setMaterial(pinkBanner);
        boxes[2].setMaterial(whiteBanner);
        boxes[3].setMaterial(pinkBanner);
        boxes[4].setMaterial(blueBanner);

        // Offset each segment of the banner in a manner where white is at 0, 0, 0
        for (int i = -2; i < boxes.length - 2; i++) {
            setTranslate(boxes[i + 2], 0, 20 * i, 0);
        }



        // Set up the camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        Rotate xTilt = new Rotate(0, Rotate.Y_AXIS);
        Rotate yTilt = new Rotate(0, Rotate.X_AXIS);
        camera.setTranslateZ(-100);
        camera.setFarClip(1000);
        camera.getTransforms().addAll(xTilt, yTilt);





        Group root = new Group();
        root.getChildren().addAll(boxes);
        root.getChildren().add(new AmbientLight(Color.WHITE));



        // Set up scene
        Scene scene = new Scene(root, 600, 600, true);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);

        final double[] cameraVelocity =     {0, 0, 0};  // x, y, z move velocity
        final double[] cameraTiltVelocity = {0, 0, 0};  // x, y, z tilt velocity
        final double SPEED = 0.5;
        final double LOOKSPEED = 0.5;

        // Why the hashmap?
        // Through my testing this seems to be one of the fastest ways for adding and removing based on the value
        // I tested vectors, arrays, and hashmaps and hashmaps stood out as the best option
        // So although we never wind up using the value of the hash key it still appears more effective in terms of popping by value
        final Map<String, Boolean> keysHeld = new HashMap<>();

        scene.setOnKeyPressed(e -> keysHeld.put(e.getCode().getName(), true));
        scene.setOnKeyReleased(e -> keysHeld.remove(e.getCode().getName()));

        new AnimationTimer() {
            public void handle(long now) {
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
                            cameraVelocity[0] = zMotionVector[0] * SPEED;
                            cameraVelocity[2] = zMotionVector[1] * SPEED;
                            break;
                        case "S":
                            cameraVelocity[0] = zMotionVector[0] * -SPEED;
                            cameraVelocity[2] = zMotionVector[1] * -SPEED;
                            break;
                        case "A":
                            cameraVelocity[0] = xMotionVector[0] * -SPEED;
                            cameraVelocity[2] = xMotionVector[1] * -SPEED;
                            break;
                        case "D":
                            cameraVelocity[0] = xMotionVector[0] * SPEED;
                            cameraVelocity[2] = xMotionVector[1] * SPEED;
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
        }.start();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}