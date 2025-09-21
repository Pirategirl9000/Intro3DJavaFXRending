package org.example.Java3DShooter;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import javafx.scene.shape.Box;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    /**
     * Serves as the primary display container for the application. Anything added to root will be displayed on stage
     */
    private final Group root = new Group();

    private final Group projectiles = new Group();

    /**
     * Scene which is being displayed by the stage. Displays what is contained in the Group 'root'
     */
    private final Scene scene = new Scene(root, 1920, 1080, true);

    /**
     * HashMap that tracks what keys are currently held
     * <p>
     * Through my testing this seems to be one of the fastest ways for adding and removing based on the value
     * I tested arraylists, vectors, arrays, and hashmaps; hashmaps stood out as the best option
     * So although we never wind up using the value of the hash key it still appears more effective for the purpose of input tracking
     * For something like key handling we should strive to use the fastest possible method and hashMaps seem to stand out as such
     */
    private final Map<String, Boolean> keysHeld = new HashMap<>();

    /**
     * Player object which controls the camera and will handle any player based events
     */
    private final Player player = new Player();

    /**
     * Width of the sun
     */
    private static final double SUNWIDTH = 40;

    /**
     * Height of the sun
     */
    private static final double SUNHEIGHT = 40;

    /**
     * Depth of the sun (z-length)
     */
    private static final double SUNDEPTH = 40;

    /**
     * Coordinates of the sun
     */
    private static final double[] SUNCOORDS = {3000, -1000, 3000};

    /**
     * Width of the ground
     */
    private static final int GROUNDWIDTH = 3000;

    /**
     * Height (thickness) of the ground plane
     */
    private static final int GROUNDHEIGHT = 10;

    /**
     * Depth (length of z axis) of the ground plane
     */
    private static final int GROUNDDEPTH = GROUNDWIDTH;

    /**
     * AnimationTimer that controls the game loop
     */
    private final AnimationTimer gameLoop = new AnimationTimer() {
        public void handle(long now) {
            player.move(keysHeld);


        }
    };

    /**
     * Driver code for the program
     * @param args NULL
     */
    public static void main(String[] args) { launch(); }

    /**
     * Sets the translation of a node
     * @param node the node to translate
     * @param x x position of the node
     * @param y y position of the node
     * @param z z position of the node
     */
    private void setTranslate(Node node, double x, double y, double z) {
        node.setTranslateX(x);
        node.setTranslateY(y);
        node.setTranslateZ(z);
    }

    /**
     * Creates a trans flag centered on a certain point
     * @param width width of the flag
     * @param height height of the flag
     * @param depth depth of the flag
     * @param x x position to center it on
     * @param y y position to center it on
     * @param z z position to center it on
     * @return an array of Boxes that make up the flag
     */
    private Box[] makeTransFlag(int width, int height, int depth, int x, int y, int z) {
        final PhongMaterial pinkBanner = new PhongMaterial();
        final PhongMaterial blueBanner = new PhongMaterial();
        final PhongMaterial whiteBanner = new PhongMaterial();

        pinkBanner.setDiffuseColor(Color.LIGHTPINK);
        blueBanner.setDiffuseColor(Color.LIGHTBLUE);
        whiteBanner.setDiffuseColor(Color.WHITE);

        Box[] boxes = new Box[] {
                new Box(width, height, depth),
                new Box(width, height, depth),
                new Box(width, height, depth),
                new Box(width, height, depth),
                new Box(width, height, depth),
        };

        boxes[0].setMaterial(blueBanner);
        boxes[1].setMaterial(pinkBanner);
        boxes[2].setMaterial(whiteBanner);
        boxes[3].setMaterial(pinkBanner);
        boxes[4].setMaterial(blueBanner);

        for (int i = -2; i < boxes.length - 2; i++) {
            setTranslate(boxes[i + 2], x, y + height * i, 0);
        }

        return boxes;
    }

    /**
     * Sets up the scene being displayed by the stage
     */
    private void initializeScene() {
        scene.setCamera(player.getCamera());
        scene.setFill(Color.SKYBLUE);

        // Set up keyListeners
        // See AnimationTimer for keyHandling
        scene.setOnKeyPressed(e -> keysHeld.put(e.getCode().getName(), true));
        scene.setOnKeyReleased(e -> keysHeld.remove(e.getCode().getName()));
    }

    /**
     * Runs the current scene on the stage and starts the application loop
     */
    private void run(Stage primaryStage) {
        primaryStage.setTitle("3D Rendering");
        primaryStage.setScene(scene);
        gameLoop.start();
        primaryStage.show();
    }

    /**
     * Create a box with preset attributes
     * @param width width of the box
     * @param height height of the box
     * @param depth depth of the box
     * @param x x position for center of box
     * @param y y position for center of box
     * @param z z position for center of box
     * @param color JavaFX.Color to draw the box as
     * @return Box with the set attributes
     */
    private Box createBox(double width, double height, double depth, double x, double y, double z, Color color) {
        Box box = new Box(width, height, depth);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        box.setMaterial(material);
        setTranslate(box, x, y, z);

        return box;
    }

    /**
     * Returns a bounding box of a Box object
     * @param box Box to find the boundaries of
     * @return double[3][2] {{minX, maxX}, {minY, maxY}, {minZ, maxZ}}
     */
    private double[][] calculateBoundingBox(Box box) {
        double dx = box.getWidth() * 0.5;
        double dy = box.getHeight() * 0.5;
        double dz = box.getDepth() * 0.5;

        return new double[][] {
            {box.getTranslateX() - dx, box.getTranslateX() + dx},
            {box.getTranslateY() - dy, box.getTranslateY() + dy},
            {box.getTranslateZ() - dz, box.getTranslateZ() + dz},
        };
    }

    /**
     * Checks the collision between two Box's
     * @param n One of the boxes to check collsion with
     * @param j One of the boxes to check collsion with
     * @return boolean signifying whether there is a collision
     */
    private boolean isColliding(Box n, Box j) {
        double[][] nBound = calculateBoundingBox(n);
        double[][] jBound = calculateBoundingBox(j);

        // Thanks to https://developer.mozilla.org/en-US/docs/Games/Techniques/3D_collision_detection for the help since I hate writing these
        return (
                nBound[0][0] <= jBound[0][1] &&
                nBound[0][1] >= jBound[0][0] &&
                nBound[1][0] <= jBound[1][1] &&
                nBound[1][1] >= jBound[1][0] &&
                nBound[2][0] <= jBound[2][1] &&
                nBound[2][1] >= jBound[2][0]
        );
    }

    /**
     * Start point for the application
     * @param primaryStage stage to display content on
     */
    @Override
    public void start(Stage primaryStage) {
        // Set up the scene
        initializeScene();

        // Create any objects
        Box[] transflag = makeTransFlag(100, 20, 100, 0, -50, 0);
        Box ground = createBox(GROUNDWIDTH, GROUNDHEIGHT, GROUNDDEPTH, 0, GROUNDHEIGHT * 0.5, 0, Color.GREEN);
        Box sun = createBox(SUNWIDTH, SUNHEIGHT, SUNDEPTH, SUNCOORDS[0], SUNCOORDS[1], SUNCOORDS[2], Color.YELLOW);


        // Add the objects to root
        root.getChildren().addAll(transflag);
        root.getChildren().add(ground);
        root.getChildren().add(player);  // Player stores both the player's hitbox and all bullets
        root.getChildren().add(sun);
        root.getChildren().add(new AmbientLight(Color.WHITE));  // Add an ambient light since I suck at pointLights and it provides even glow

        // Start the gameloop and display application
        run(primaryStage);
    }
}