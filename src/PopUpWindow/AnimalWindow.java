package PopUpWindow;

import Elements.Animal;
import Engine.IEngine;
import Elements.IGenotypeConverter;
import Elements.IWorldMap;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.HashSet;

public class AnimalWindow implements IGenotypeConverter {

    static final int WINDOW_WIDTH = 600;
    static final int WINDOW_HEIGHT = 400;

    private final Animal animal;
    private final IEngine engine;
    private final IWorldMap grassField;
    private int actualEpoch;
    private boolean undead = true;

    private final Stage stage = new Stage();
    private Text childrenText;
    private Text descendantText;
    private Text deadEpochText;
    private TextField nTextField;
    private Button startSimulation;

    public AnimalWindow(Animal animal, IEngine engine, IWorldMap grassField, int actualEpoch){
        this.animal = animal;
        this.engine = engine;
        this.grassField = grassField;
        this.actualEpoch = actualEpoch;
    }

    public void showWindow(){
        Scene scene = new Scene(prepareLayout(genotypeToString(animal.getGenotype())), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Animal preferences");
        stage.setScene(scene);
        stage.show();
        events();
    }

    private VBox prepareLayout(String genotypeString){
        Text concreteAnimalPosition = new Text("Animal position: " + animal.getPosition());
        Text concreteAnimalOrient = new Text("Animal orientation: " + animal);
        Text concreteAnimalGenotype = new Text("Animal orientation: " + genotypeString + "\n\n");

        Text nValueText = new Text("Are you want to see the animal after N epoch? ");
        Text nValue = new Text("If you like, please enter N below: ");
        nTextField = new TextField();
        nTextField.setMaxWidth(200);
        startSimulation = new Button("See animal after N epoch");

        childrenText =  new Text("Amount of animal children");
        childrenText.setVisible(false);
        descendantText =  new Text("Amount of animal descendant");
        descendantText.setVisible(false);
        deadEpochText = new Text("Animal death Epoch ");
        deadEpochText.setVisible(false);

        VBox components = new VBox(concreteAnimalPosition, concreteAnimalOrient, concreteAnimalGenotype, nValueText, nValue,nTextField,startSimulation,childrenText,descendantText,deadEpochText);
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
        startSimulation.setOnAction(actionEvent -> {
            childrenText.setVisible(true);
            descendantText.setVisible(true);
            deadEpochText.setVisible(true);

            try {
                int N = Integer.parseInt(nTextField.getText());

                // symuacje zaczynamy od pustej liczby dzieci
                for(ArrayList<Animal> animalList : grassField.getAnimals().values()){
                    for (Animal value : animalList) {
                        value.resetChildren();
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
        });
    }

    public int getUpdatedEpoch() { return actualEpoch; }
    public Stage getStage() { return stage; }
}
