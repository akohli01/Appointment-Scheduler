package application.utility;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneHandler {

    private final Stage primaryStage;
    private final FXMLLoader loader;
    private final FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/application/view/Menu.fxml"));

    //define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;

    public SceneHandler(String fxmlDocument, Stage primaryStage){
        this.primaryStage = primaryStage;
        loader = new FXMLLoader(getClass().getResource(fxmlDocument));
    }

    public SceneHandler(String fxmlDocument, Stage primaryStage, ResourceBundle bundle){
        this.primaryStage = primaryStage;
        loader = new FXMLLoader(getClass().getResource(fxmlDocument), bundle);
    }

    public void createScene() throws IOException {

        Parent pane = loader.load();

        primaryStage.initStyle(StageStyle.UNDECORATED);

        //grab your root here
        pane.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        pane.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void createPopupScene() throws IOException {

        Parent pane = loader.load();

        //grab your root here
        pane.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        pane.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Used only with BorderPane layout
    public void createAppendableScene() throws IOException {

        Parent menuBar = menuLoader.load();

        Parent switchableTab = loader.load();

        BorderPane borderPane = new BorderPane();

        BorderPane.setAlignment(menuBar, Pos.TOP_RIGHT);
        borderPane.setTop(menuBar);
        borderPane.setCenter(switchableTab);

        //grab your root here
        borderPane.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        borderPane.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Get the menu loader (used if further manipulation is needed concerning the menu bar)
    public FXMLLoader getMenuLoader() {
        return menuLoader;
    }

    //Get the loader for the particular scene (used if further manipulation is needed concerning the menu bar)
    public FXMLLoader getSceneLoader() {
        return loader;
    }

}
