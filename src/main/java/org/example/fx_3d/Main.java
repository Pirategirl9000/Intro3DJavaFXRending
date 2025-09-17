package org.example.fx_3d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.AmbientLight;
import javafx.scene.Scene;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import javafx.scene.shape.Box;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {


    /**
     * Serves as the primary display container for the application. Anything added to root will be displayed on stage
     */
    private final Group root = new Group();

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
    Player player = new Player();

    /**
     * AnimationTimer that controls the game loop
     */
    private final AnimationTimer gameLoop = new AnimationTimer() {
        public void handle(long now) { player.move(keysHeld); }
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
    private Box createBox(int width, int height, int depth, int x, int y, int z, Color color) {
        Box box = new Box(width, height, depth);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        box.setMaterial(material);
        setTranslate(box, x, y, z);

        return box;
    }



    /**
     * Start point for the application
     * @param primaryStage stage to display content on
     */
    @Override
    public void start(Stage primaryStage) {

        initializeScene();

        Box[] transflag = makeTransFlag(100, 20, 100, 0, -50, 0);
        Box ground = createBox(1000, 10, 1000, 0, 5, 0, Color.GREEN);

        Player player = new Player();


        root.getChildren().addAll(transflag);
        root.getChildren().add(ground);
        root.getChildren().add(player.getHitbox());
        root.getChildren().add(new AmbientLight(Color.WHITE));  // Add an ambient light since I suck at pointLights and it provides even glow

        run(primaryStage);


    }
}