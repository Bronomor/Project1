package PopUpWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import Components.Animal;

import java.util.ArrayList;

public class DominantGenotypeWindow {

    static final int WINDOW_WIDTH = 600;
    static final int WINDOW_HEIGHT = 400;


    public void start(ArrayList<Animal> result){
        Stage stage = new Stage();
        ListView<String> list = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList ();
        for(Animal animal : result) items.add(animal.getPosition() + " " + animal + " " + animal.getStringGenotype());

        list.setItems(items);
        Scene scene = new Scene(list, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Animal preferences");
        stage.setScene(scene);
        stage.show();
    }
}
