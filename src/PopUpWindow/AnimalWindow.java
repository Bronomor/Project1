package PopUpWindow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Components.Animal;
import Engine.IEngine;
import Map.IWorldMap;

import java.util.ArrayList;
import java.util.HashSet;

public class AnimalWindow {

    static final int WINDOW_WIDTH = 600;
    static final int WINDOW_HEIGHT = 400;

    private Animal animal;
    private IEngine engine;
    private IWorldMap grassField;
    private int actualEpoch;
    private boolean undead = true;

    Stage stage = new Stage();
    Text concreteAnimalPosition;
    Text concreteAnimalOrient;
    Text nValueText;
    Text nValue;
    Text childrenText;
    Text descendantText;
    Text deadEpochText;
    TextField nTextField;
    Button startSimulation;
    Text ConcreteAnimalGenotype;

    public AnimalWindow(Animal animal, IEngine engine, IWorldMap grassField, int actualEpoch){
        this.animal = animal;
        this.engine = engine;
        this.grassField = grassField;
        this.actualEpoch = actualEpoch;
    }

    public void showWindow(){
        Scene scene = new Scene(prepareLayout(animal.getStringGenotype()), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Animal preferences");
        stage.setScene(scene);
        stage.show();
        events();
    }

    private VBox prepareLayout(String genotypeString){
        concreteAnimalPosition = new Text("Animal position: " + animal.getPosition());
        concreteAnimalOrient = new Text("Animal orientation: " + animal);
        nValueText = new Text("Are you want to see a animal after N epoch? ");
        nValue = new Text("If you like, please enter N below: ");
        childrenText =  new Text("Amount of animal children");
        descendantText =  new Text("Amount of animal descendant");
        deadEpochText = new Text("Animal death Epoch ");
        nTextField = new TextField();
        nTextField.setMaxWidth(200);
        startSimulation = new Button("See animal after N epoch");
        ConcreteAnimalGenotype = new Text("Animal orientation: " + genotypeString);

        VBox components = new VBox(concreteAnimalPosition,concreteAnimalOrient,ConcreteAnimalGenotype,nValueText,nValue,nTextField,startSimulation,childrenText,descendantText,deadEpochText);
        components.setAlignment(Pos.CENTER);
        components.setSpacing(10);

        return components;
    }
    private void countDescendant(Animal animal,HashSet<Animal> descendant){
        if(animal.getChildren() == null || animal.getChildren().size() <= 0) return;
        else {
            descendant.addAll(animal.getChildren());
            for(Animal animalOne : animal.getChildren()){
                countDescendant(animalOne,descendant);
            }
        }
    }
    private void events(){
        startSimulation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    int N = Integer.parseInt(nTextField.getText());

                    //resetowanie dzieci, zaczynamy od 0
                    for(ArrayList<Animal> animalList : grassField.getAnimals().values()){
                        for(int i=0; i<animalList.size(); i++){
                            animalList.get(i).resetChildren();
                        }
                    }

                    //symulacja n epok
                    for(int i=0; i<N; i++){
                        actualEpoch+=1;
                        engine.run(actualEpoch,true,false);
                        if(animal.getEnergy() <= 0 && undead) {
                            undead = false;
                            deadEpochText.setText("Animal death Epoch: " + actualEpoch);
                        }
                    }

                    HashSet<Animal> descendant = new HashSet<>();
                    countDescendant(animal,descendant);
                    childrenText.setText("Amount of animal children: " + animal.getChildren().size());
                    descendantText.setText("Amount of animal descendant: " + descendant.size());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Number");
                }
            }
        });
    }

    public IEngine getUpdatedEngine() {return engine;}
    public int getUpdatedEpoch() {return actualEpoch;}
    public IWorldMap getUpdatedMap() {return grassField;}
    public Stage getStage() { return stage; }
}
