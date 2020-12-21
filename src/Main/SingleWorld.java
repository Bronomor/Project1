package Main;

import Elements.*;
import Engine.IEngine;
import Engine.SimulationEngine;
import MapParameters.MapParameters;
import PopUpWindow.AnimalWindow;
import PopUpWindow.DominantGenotypeWindow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import static javafx.scene.chart.XYChart.*;

public class SingleWorld implements IGenotypeConverter {
    static final int WINDOW_WIDTH = 1300;
    static final int WINDOW_HEIGHT = 800;

    private IEngine engine;
    private IWorldMap grassField;
    private final MapParameters mapParameters;
    private final Vector2d mapScale;
    private final Timeline timeline = new Timeline();
    private int actualEpoch = 0;

    private final HBox chartLayout = new HBox();
    private final VBox mapLayout  = new VBox();

    private final ObservableList<Series> seriesList = FXCollections.observableArrayList();
    private final ObservableList<Data> grassChartList = FXCollections.observableArrayList();
    private final ObservableList<Data> animalsChartList = FXCollections.observableArrayList();

    private final Button stopStartSimulationButton = new Button("Start Simulation");
    private final Text actualEpochText              = new Text("Actual Epoch: 0");
    private final Text allAnimalsText              = new Text("Animals: 0");
    private final Text allPlantText                = new Text("Grass: 0");
    private final Text dominantGenotypeText        = new Text("Dominant Genotype: ");
    private final Text averageEnergyText           = new Text("Average Animals Energy: ");
    private final Text averageAnimalTimeText       = new Text("Average Animal life expectancy time");
    private final Text averageChildrenText         = new Text("Average Children for Animal");
    private final Button dominantGenotypeAnimalsButton   = new Button("Get animals with dominant genotype");
    private final TextField saveStatisticNTextView = new TextField();
    private final Button saveStatisticButton      = new Button("Save statistic to file");

    public SingleWorld(MapParameters mapParameters){
        this.mapParameters = mapParameters;
        mapScale = new Vector2d((int) Math.round((double) (WINDOW_WIDTH-600) / mapParameters.getMapWidth()),(int) Math.round((double) WINDOW_HEIGHT/ mapParameters.getMapHeight()));
    }

    public void start() {
        try {
            Stage window = new Stage();
            window.setTitle("Darwin World");
            window.setScene(prepareLayouts());
            window.show();
            prepareEngine();
        } catch (Exception e){
            System.out.println("Something wrong with preparation in single World");
            e.printStackTrace();
        }

        try {
            animation();
            events();
        } catch (Exception e){
            System.out.println("Something wrong with animation");
            e.printStackTrace();
        }
    }


    private void prepareEngine(){
        grassField = new GrassField(mapParameters);

        // Create position to first Adam/Ewa animals
        ArrayList<Vector2d> positions = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i< mapParameters.getAnimalsAmount(); i++){
            Vector2d pos = new Vector2d(random.nextInt(mapParameters.getMapWidth()),random.nextInt(mapParameters.getMapHeight()));
            if(positions.contains(pos)) i-=1;
            else positions.add(pos);
        }

        // Create a biomes like jungle and step
        Vector2d[][] biomesParameters = new Vector2d[2][4];
        biomesParameters[0][0] = mapParameters.getMapLower();
        biomesParameters[0][1] = mapParameters.getMapHigher();
        biomesParameters[0][2] = mapParameters.getJungleLower();
        biomesParameters[0][3] = mapParameters.getJungleHigher();

        biomesParameters[1][0] = mapParameters.getJungleLower();
        biomesParameters[1][1] = mapParameters.getJungleHigher();
        biomesParameters[1][2] = null;
        biomesParameters[1][3] = null;

        engine = new SimulationEngine(grassField, positions, mapParameters, biomesParameters);
    }

    private Scene prepareLayouts(){

        chartLayout.setPrefHeight(WINDOW_HEIGHT >> 1);
        chartLayout.setPrefWidth(WINDOW_WIDTH-Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));
        chartLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,null,null)));
        chartLayout.setAlignment(Pos.CENTER);

        seriesList.add(new Series("Grass grown", grassChartList));
        seriesList.add(new Series("Animals population", animalsChartList));
        LineChart populationChart = new LineChart(new NumberAxis(), new NumberAxis(), seriesList);
        populationChart.setCreateSymbols(false);
        populationChart.setAlternativeColumnFillVisible(false);
        populationChart.setAlternativeRowFillVisible(false);
        populationChart.setAnimated(false);
        populationChart.setVerticalZeroLineVisible(true);
        populationChart.setEffect(null);
        chartLayout.getChildren().add(populationChart);

        VBox additionalLayout   = new VBox();
        additionalLayout.setMinHeight(WINDOW_HEIGHT >> 1);
        additionalLayout.setPrefWidth(WINDOW_WIDTH-Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));
        additionalLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,null,null)));

        actualEpochText.setFont(new Font(20));
        actualEpochText.setTextAlignment(TextAlignment.JUSTIFY);
        saveStatisticNTextView.setPromptText("Print N value to get statistic");
        saveStatisticNTextView.setMaxWidth(200);

        additionalLayout.getChildren().addAll(stopStartSimulationButton,actualEpochText,allAnimalsText,allPlantText,dominantGenotypeText,averageEnergyText);
        additionalLayout.getChildren().addAll(averageAnimalTimeText,averageChildrenText,dominantGenotypeAnimalsButton,saveStatisticNTextView,saveStatisticButton);
        additionalLayout.setSpacing(10);
        additionalLayout.setAlignment(Pos.BASELINE_CENTER);

        mapLayout.minHeight(WINDOW_HEIGHT);
        mapLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,null,null)));
        mapLayout.prefWidth(Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));

        return new Scene(new HBox(new VBox(chartLayout,additionalLayout),mapLayout),WINDOW_WIDTH, WINDOW_HEIGHT);
    }
    private void animation(){
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(
                Duration.millis(20),
                (ActionEvent event) -> {
                    update(false);
                    display();
                }
        ));
    }
    private void display(){

        // Actualize chart and statistic view
        actualEpochText.setText("Actual Epoch: " + actualEpoch);
        if(actualEpoch % 5000 == 0){
            animalsChartList.clear();
            grassChartList.clear();
        }
        animalsChartList.add(new Data(actualEpoch % 5000, engine.getAnimalsAmount()));
        grassChartList.add(new Data(actualEpoch % 5000, grassField.getGrass().size()));

        allAnimalsText.setText("Animals: " + engine.getAnimalsAmount());
        allPlantText.setText("Grass: " + (grassField.getGrass().size()));
        averageEnergyText.setText("Average Animals Energy: " + engine.getAverageAnimalEnergy());
        averageAnimalTimeText.setText("Average Animal life expectancy time " + engine.getAverageAnimalTime());
        averageChildrenText.setText("Average Children for Animal " + engine.getAverageAnimalChildren());
        dominantGenotypeText.setText("Dominant Genotype: "+ engine.getDominantGenotype());

        // Clear canvas
        mapLayout.getChildren().clear();

        Canvas canvas = new Canvas(Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)),WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // draw Step
        gc.setFill(Color.rgb(100, 190, 0));
        gc.fillRect(0,0, mapParameters.getMapWidth()*mapScale.x, mapParameters.getMapHeight()*mapScale.y);

        // draw Jungle
        gc.setFill(Color.rgb(123, 200, 12));
        gc.fillRect(mapParameters.getJungleLower().x*mapScale.x, mapParameters.getJungleLower().y*mapScale.y, mapParameters.getJungleWidth()*mapScale.x, mapParameters.getJungleHeight()*mapScale.y);

        // draw Grass
        gc.setFill(Color.rgb(43, 122, 16));
        for(Grass grass : grassField.getGrass().values()) {
            gc.fillRect(grass.getPosition().x * mapScale.x, grass.getPosition().y * mapScale.y, mapScale.x, mapScale.y);
        }

        // draw Animal on Canvas with other colors
        for(ArrayList<Animal> animalList : grassField.getAnimals().values()){
            for(Animal animal : animalList){
                    // Animal with more than 85% start energy
                if (animal.getEnergy() > mapParameters.getStartEnergy() * 0.85) gc.setFill(Color.rgb(105, 16, 16));
                    // Animal with energy between 50% and 85% start energy
                else if (animal.getEnergy() > mapParameters.getStartEnergy() * 0.50) gc.setFill(Color.rgb(176, 28, 28));
                    // Animal with energy between 15% and 50% start energy
                else if (animal.getEnergy() > mapParameters.getStartEnergy() * 0.15) gc.setFill(Color.rgb(245, 93, 93));
                    // Animal with energy lower than 15% start energy
                else gc.setFill(Color.rgb(194, 96, 93));
                gc.fillOval(animal.getPosition().x * mapScale.x, animal.getPosition().y * mapScale.y, mapScale.x, mapScale.y);
            }
        }
        mapLayout.getChildren().add(canvas);
    }

    private void update(boolean allEpoch){
        actualEpoch +=1;
        engine.run(actualEpoch, false,allEpoch);
    }

    private void events(){
        stopStartSimulationButton.setOnAction(actionEvent -> {
            if(stopStartSimulationButton.getText().equals("Start Simulation")){
                mapLayout.setDisable(true);
                dominantGenotypeAnimalsButton.setDisable(true);
                saveStatisticButton.setDisable(true);
                timeline.play();
                stopStartSimulationButton.setText("Stop Simulation");
            }
            else {
                timeline.stop();
                mapLayout.setDisable(false);
                dominantGenotypeAnimalsButton.setDisable(false);
                saveStatisticButton.setDisable(false);
                stopStartSimulationButton.setText("Start Simulation");
            }

        });

        mapLayout.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                Vector2d position = new Vector2d((int) Math.floor(mouseEvent.getX() / mapScale.x),(int) Math.floor(mouseEvent.getSceneY()/ mapScale.y));
                ArrayList<Animal> animal = grassField.getAnimalsAtPosition(position);

                if(animal != null && animal.size() > 0) animalShow(animal.get(0));
            }
        });

        dominantGenotypeAnimalsButton.setOnAction(actionEvent -> allDominantGenotypeShow());

        saveStatisticButton.setOnAction(actionEvent -> {
            try {
                writeStatisticToFile();
                display();
            } catch (NumberFormatException e) {
                System.out.println("Invalid Number");
            }
        });
    }

    private void writeStatisticToFile(){
        int animalsAllEpoch = 0;
        int grassAllEpoch = 0;
        int energyAllEpoch = 0;
        int deadAnimal = 0;
        int deadAnimalLife = 0;
        int allChildren = 0;
        int N = Integer.parseInt(saveStatisticNTextView.getText());
        String dominantGenotype = "";
        engine.clearDominantGenotype();
        if(N>0) {
            for (int i = 0; i < N; i++) {
                update(true);
                animalsAllEpoch += engine.getAnimalsAmount();
                grassAllEpoch += grassField.getGrass().size();
                energyAllEpoch += engine.getTotalAnimalEnergy();
                deadAnimal += engine.getDeadAnimalAmount();
                deadAnimalLife += engine.getDeadAnimalTime();
                allChildren = engine.getTotalChildren();
                dominantGenotype = engine.getDominantGenotype();
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("statistic.txt"), StandardCharsets.UTF_8))) {
            writer.write("Average animals amount: ");
            writer.write(animalsAllEpoch / N +"\n");
            writer.write("Average grass amount: ");
            writer.write(grassAllEpoch / N +"\n");
            writer.write("Average animals energy: ");
            writer.write((animalsAllEpoch > 0 ? energyAllEpoch / animalsAllEpoch : 0) +"\n");
            writer.write("Average dead animal life: ");
            writer.write((deadAnimalLife > 0 ? (double) deadAnimal / deadAnimalLife : 0) +"\n");
            writer.write("Average animal children: ");
            writer.write((animalsAllEpoch > 0 ? (double) allChildren / animalsAllEpoch : 0) +"\n");
            writer.write("Dominant genotype: ");
            writer.write(dominantGenotype);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void animalShow(Animal animal){
        AnimalWindow animalWindow = new AnimalWindow(animal, engine,grassField,actualEpoch);
        animalWindow.showWindow();

        animalWindow.getStage().setOnCloseRequest(event -> {
            actualEpoch = animalWindow.getUpdatedEpoch();
            display();
        });
    }
    private void allDominantGenotypeShow(){
        String genotype= engine.getDominantGenotype();

        ArrayList<Animal> result = new ArrayList<>();
        for (ArrayList<Animal> animalsList : grassField.getAnimals().values()){
            for(Animal animal : animalsList) {
                if(genotypeToString(animal.getGenotype()).equals(genotype)) result.add(animal);
            }
        }
        DominantGenotypeWindow dominantGenotypeWindow = new DominantGenotypeWindow();
        dominantGenotypeWindow.start(result);
    }


}
