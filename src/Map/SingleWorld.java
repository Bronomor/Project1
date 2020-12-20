package Map;

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
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import Components.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SingleWorld {
    static final int WINDOW_WIDTH = 1300;
    static final int WINDOW_HEIGHT = 800;

    private IEngine engine;
    private IWorldMap grassField;
    private final MapParameters mapParameters;
    private Vector2d mapScale;
    private final Timeline timeline = new Timeline();
    private int actualEpoch = 0;

    Stage window;
    private final HBox chartLayout = new HBox();
    private final Group mapLayout  = new Group();
    private final ObservableList<XYChart.Data> grassChartList = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data> animalsChartList = FXCollections.observableArrayList();
    private LineChart populationChart;

    Button stopStartSimulationButton = new Button("Start Simulation");
    Text whoseEpochText              = new Text("Actual Epoch: 0");
    Text allAnimalsText              = new Text("Animals: 0");
    Text allPlantText                = new Text("Grass: 0");
    Text dominantGenotypeText        = new Text("Dominujący Genotyp: ");
    Text averageEnergyText           = new Text("Average Animals Energy: ");
    Text averageAnimalTimeText       = new Text("Average Animal life expectancy time");
    Text averageChildrenText         = new Text("Average Children for Animal");
    Button dominantGenotypeAnimalsButton   = new Button("Get animals with dominant genotype");
    TextField saveStatisticNTextView = new TextField();
    Button saveStatisticButton      = new Button("Get animals with dominant genotype");

    public SingleWorld(MapParameters mapParameters){
        this.mapParameters = mapParameters;
        //Wyliczanie skali mapy
        mapScale = new Vector2d((int) Math.round((double) (WINDOW_WIDTH-600) / mapParameters.getMapWidth()),(int) Math.round((double) WINDOW_HEIGHT/ mapParameters.getMapHeight()));
    }

    public void start() throws Exception {
        window = new Stage();
        window.setTitle("Darvin World");
        window.setScene(prepareLayouts());
        window.setMaxWidth(WINDOW_WIDTH);
        window.setMaxHeight(WINDOW_HEIGHT);
        window.show();

        prepareEngine();
        animation();
        events();
    }


    private void prepareEngine(){
        grassField = new GrassField(mapParameters);

        // umieszczanie poczatkowych zwierzat Adam/Ewa
        ArrayList<Vector2d> positions = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i< mapParameters.getAnimalsAmount(); i++){
            Vector2d pos = new Vector2d(random.nextInt(mapParameters.getMapWidth()),random.nextInt(mapParameters.getMapHeight()));
            if(positions.contains(pos)) i-=1;
            else positions.add(pos);
        }

        Vector2d[][] biomesRestriction = new Vector2d[2][4];
        biomesRestriction[0][0] = mapParameters.getMapLower();
        biomesRestriction[0][1] = mapParameters.getMapHigher();
        biomesRestriction[0][2] = mapParameters.getJungleLower();
        biomesRestriction[0][3] = mapParameters.getJungleHigher();

        biomesRestriction[1][0] = mapParameters.getJungleLower();
        biomesRestriction[1][1] = mapParameters.getJungleHigher();
        biomesRestriction[1][2] = null;
        biomesRestriction[1][3] = null;

        engine = new SimulationEngine(grassField, positions, mapParameters, biomesRestriction);
    }
    private Scene prepareLayouts(){
        // Wykres
        System.out.println(Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));
        chartLayout.setPrefHeight(WINDOW_HEIGHT/2);
        chartLayout.setPrefWidth(WINDOW_WIDTH-Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));
        chartLayout.setBackground(new Background(new BackgroundFill(Color.GRAY,null,null)));
        chartLayout.setAlignment(Pos.CENTER);

        ObservableList<XYChart.Series> seriesList = FXCollections.observableArrayList();
        seriesList.add(new XYChart.Series("Grass grown", grassChartList));
        seriesList.add(new XYChart.Series("Animals population", animalsChartList));

        populationChart = new LineChart(new NumberAxis(), new NumberAxis(), seriesList);
        populationChart.setCreateSymbols(false);
        populationChart.setAlternativeColumnFillVisible(false);
        populationChart.setAlternativeRowFillVisible(false);
        populationChart.setAnimated(false);
        populationChart.setVerticalZeroLineVisible(true);
        populationChart.setEffect(null);
        chartLayout.getChildren().add(populationChart);

        // lewa dolna strona aplikacji, menu podręczne
        VBox additionalLayout   = new VBox();
        additionalLayout.setMinHeight(WINDOW_HEIGHT/2);
        additionalLayout.setPrefWidth(WINDOW_WIDTH-Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));
        additionalLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,null,null)));

        whoseEpochText.setFont(new Font(20));
        whoseEpochText.setTextAlignment(TextAlignment.JUSTIFY);
        saveStatisticNTextView.setPromptText("Print N value to get statistic");
        saveStatisticNTextView.setMaxWidth(200);
        additionalLayout.getChildren().addAll(stopStartSimulationButton,whoseEpochText,allAnimalsText,allPlantText,dominantGenotypeText,averageEnergyText);
        additionalLayout.getChildren().addAll(averageAnimalTimeText,averageChildrenText,dominantGenotypeAnimalsButton,saveStatisticNTextView,saveStatisticButton);
        additionalLayout.setSpacing(10);
        additionalLayout.setAlignment(Pos.BASELINE_CENTER);

        // dopasowanie mapy do prawej strony aplikacji
        mapLayout.prefHeight(WINDOW_HEIGHT);
        mapLayout.prefWidth(Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)));

        return new Scene(new HBox(new VBox(chartLayout,additionalLayout),mapLayout),WINDOW_WIDTH, WINDOW_HEIGHT);
    }
    private void animation(){
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(
                Duration.millis(5),
                (ActionEvent event) -> {
                    update(false,false);
                    display();
                }
        ));
    }
    private void display(){
        // aktualna epoka
        whoseEpochText.setText("Actual Epoch: " + actualEpoch);

        // aktualizowanie wykresu
        if(actualEpoch % 5000 == 0){
            animalsChartList.clear();
            grassChartList.clear();
            ObservableList<XYChart.Series> seriesList = FXCollections.observableArrayList();
            seriesList.add(new XYChart.Series("Grass grown", grassChartList));
            seriesList.add(new XYChart.Series("Animals population", animalsChartList));
            populationChart = new LineChart(new NumberAxis(), new NumberAxis());
        }
        animalsChartList.add(new XYChart.Data(actualEpoch % 5000, engine.getAnimalsAmount()));
        grassChartList.add(new XYChart.Data(actualEpoch % 5000, grassField.getGrass().size()));

        //aktualizowanie statystyk
        allAnimalsText.setText("Animals: " + engine.getAnimalsAmount());
        allPlantText.setText("Grass: " + (grassField.getGrass().size()));
        averageEnergyText.setText("Average Animals Energy: " + engine.getAverageAnimalEnergy());
        averageAnimalTimeText.setText("Average Animal life expectancy time " + engine.getAverageAnimalTime());
        averageChildrenText.setText("Average Children for Animal " + engine.getAverageAnimalChildren());
        dominantGenotypeText.setText("Dominujący Genotyp: "+ engine.dominantGenotype());

        // Tutaj rysuje to wszystko co dzieje się na mapie
        mapLayout.getChildren().clear();

        Canvas canvas = new Canvas(Math.ceil(mapScale.x*(mapParameters.getMapHigher().x-mapParameters.getMapLower().x+1)),WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLUE);
        gc.fillRect(75,75,100,100);
        gc.setFill(Color.RED);
        gc.fillRect(100,100,100,100);

        gc.setFill(Color.rgb(100, 190, 0));
        gc.fillRect(0,0, mapParameters.getMapWidth()*mapScale.x, mapParameters.getMapHeight()*mapScale.y);

        gc.setFill(Color.rgb(123, 200, 12));
        gc.fillRect(mapParameters.getJungleLower().x*mapScale.x, mapParameters.getJungleLower().y*mapScale.y, mapParameters.getJungleWidth()*mapScale.x, mapParameters.getJungleHeight()*mapScale.y);

        gc.setFill(Color.rgb(43, 122, 16));

        for(Grass grass : grassField.getGrass().values()) {
            gc.fillRect(grass.getPosition().x * mapScale.x, grass.getPosition().y * mapScale.y, mapScale.x, mapScale.y);
        }

        for(ArrayList<Animal> animalList : grassField.getAnimals().values()){
            for(Animal animal : animalList){

                // przypisanie kolorów do zwierzęcia

                // 85-100% energi
                if (animal.getEnergy() > mapParameters.getStartEnergy() * 0.85) gc.setFill(Color.rgb(105, 16, 16));
                    // 50-85% enegi
                else if (animal.getEnergy() > mapParameters.getStartEnergy() * 0.50) gc.setFill(Color.rgb(176, 28, 28));
                    // 15-50% energi
                else if (animal.getEnergy() > mapParameters.getStartEnergy() * 0.15) gc.setFill(Color.rgb(245, 93, 93));
                    // < 15 % energi
                else gc.setFill(Color.rgb(194, 96, 93));
                gc.fillOval(animal.getPosition().x * mapScale.x, animal.getPosition().y * mapScale.y, mapScale.x, mapScale.y);
            }
        }
        mapLayout.getChildren().add(canvas);
    }
    private void update(boolean keepChildren, boolean allEpoch){
        actualEpoch +=1;
        engine.run(actualEpoch,keepChildren,allEpoch);
    }
    private void events(){
        stopStartSimulationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
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

            }
        });
        mapLayout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY){
                    Vector2d position = new Vector2d((int) Math.floor(mouseEvent.getX() / mapScale.x),(int) Math.floor(mouseEvent.getSceneY()/ mapScale.y));
                    ArrayList<Animal> animal = grassField.getAnimals().get(position);
                    if(animal != null && animal.size() > 0){
                        animalShow(animal.get(0));
                    }
                    else{
                        System.out.println("Nie wybrano zwierzęcia");
                    }
                }
            }
        });
        dominantGenotypeAnimalsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                allDominantGenotypeShow();
            }
        });
        saveStatisticButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    int animalsAllEpoch = 0;
                    int grassAllEpoch = 0;
                    int energyAllEpoch = 0;
                    int deadAnimal = 0;
                    int deadAnimalLife = 0;
                    int allChildren = 0;
                    int N = Integer.parseInt(saveStatisticNTextView.getText());
                    String dominantGenotype = "";
                    if(N>0) {
                        for (int i = 0; i < N; i++) {
                            update(false,true);
                            animalsAllEpoch += engine.getAnimalsAmount();
                            grassAllEpoch += grassField.getGrass().size();
                            energyAllEpoch += engine.getTotalAnimalEnergy();
                            deadAnimal += engine.getDeadAnimalAmount();
                            deadAnimalLife += engine.getDeadAnimalTime();
                            allChildren = engine.getTotalChildren();
                            dominantGenotype = engine.dominantGenotype();
                        }
                    }
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("statistic.txt"), "utf-8"))) {
                        writer.write(Integer.toString(animalsAllEpoch/N)+"\n");
                        writer.write(Integer.toString(grassAllEpoch/N)+"\n");
                        writer.write(Integer.toString(energyAllEpoch/animalsAllEpoch)+"\n");
                        writer.write(Double.toString((double) deadAnimal/deadAnimalLife)+"\n");
                        writer.write(Double.toString((double) allChildren/animalsAllEpoch)+"\n");
                        writer.write(dominantGenotype);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Number");
                }
            }
        });
    }

    private void animalShow(Animal animal){
        AnimalWindow animalWindow = new AnimalWindow(animal, engine,grassField,actualEpoch);
        animalWindow.showWindow();

        animalWindow.getStage().setOnCloseRequest(event -> {
            engine = animalWindow.getUpdatedEngine();
            actualEpoch = animalWindow.getUpdatedEpoch();
            grassField = animalWindow.getUpdatedMap();
            display();
        });
    }
    private void allDominantGenotypeShow(){
        String genotype= engine.dominantGenotype();

        ArrayList<Animal> result = new ArrayList<>();
        for (ArrayList<Animal> animalsList : grassField.getAnimals().values()){
            for(Animal animal : animalsList) {
                if(animal.getStringGenotype().equals(genotype)) result.add(animal);
            }
        }
        DominantGenotypeWindow dominantGenotypeWindow = new DominantGenotypeWindow();
        dominantGenotypeWindow.start(result);
    }


}
