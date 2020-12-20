package Map;

import MapParameters.MapParameters;
import javafx.application.Application;
import javafx.stage.Stage;

public class World extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        // Pobieram podstawowe parametry które posiada każdy świat z pliku parameters.json
        MapParameters mapParameters = new MapParameters();

        //Inicjalizuje okno i układam w nim layouty, Robie to tyle razy ile jest wpisane okien w parameters.json
        for(int i=0; i<mapParameters.getAmountOfMaps(); i++){
            SingleWorld singleWorld = new SingleWorld(mapParameters);
            singleWorld.start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
