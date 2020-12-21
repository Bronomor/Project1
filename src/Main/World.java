package Main;

import MapParameters.MapParameters;
import PopUpWindow.ExceptionWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class World extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            // Take basic map parameters
            MapParameters mapParameters = new MapParameters();

            // Initialize window, mapParameters.getAmountOfMaps() - entry parameter in parameters.json
            for (int i = 0; i < mapParameters.getAmountOfMaps(); i++) {
                SingleWorld singleWorld = new SingleWorld(mapParameters);
                singleWorld.start();
            }
        }
        catch (IllegalArgumentException e) {
            ExceptionWindow exceptionWindow = new ExceptionWindow();
            exceptionWindow.start(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
