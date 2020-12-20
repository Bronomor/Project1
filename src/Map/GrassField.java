package Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Components.*;
import MapParameters.MapParameters;

public class GrassField implements IWorldMap, IPositionChangeObserver {

    protected HashMap<Vector2d, Grass> jungleGrass          = new HashMap<>();
    protected HashMap<Vector2d,Grass> stepGrass             = new HashMap<>();
    protected HashMap<Vector2d, ArrayList<Animal>>  animals = new HashMap<>();

    private final Vector2d jungleLower;
    private final Vector2d jungleHigher;
    private final Vector2d mapLower;
    private final Vector2d mapHigher;
    private final int mapAvailablePositions;
    private final int jungleAvailablePositions;

    public GrassField(MapParameters mapParameters) {
        jungleLower = mapParameters.getJungleLower();
        jungleHigher = mapParameters.getJungleHigher();
        mapLower = mapParameters.getMapLower();
        mapHigher = mapParameters.getMapHigher();
        mapAvailablePositions = mapParameters.getMapAvailablePositions();
        jungleAvailablePositions = mapParameters.getJungleAvailablePositions();
    }

    public boolean place(Animal animal) {
        positionChanged(animal, animal.getPosition());
        return true;
    }

    public boolean isOccupied(Vector2d position) {
        if(animals.containsKey(position)){
            if(animals.get(position).size() > 0) return true;
        }
        if(stepGrass.containsKey(position)) return true;
        else return jungleGrass.containsKey(position);
    }

    public Vector2d AdjustingPositionToMap(Vector2d position){

        if (position.x < mapLower.x) position = position.add(new Vector2d(mapHigher.x-mapLower.x+1,0));
        else if (position.x >= mapHigher.x+1) position = position.substract(new Vector2d(mapHigher.x-mapLower.x+1,0));

        if (position.y < mapLower.y) position = position.add(new Vector2d(0,mapHigher.y-mapLower.y+1));
        else if (position.y >= mapHigher.y+1) position = position.substract(new Vector2d(0,mapHigher.y-mapLower.y+1));

        return position;
    }

    public Object objectAt(Vector2d position) {
        if(animals.get(position) != null) return animals.get(position);
        if(jungleGrass.get(position) != null) return jungleGrass.get(position);
        if(stepGrass.get(position) != null) return stepGrass.get(position);
        return null;
    }

    public void putAnimalValues(Animal animal, Vector2d newPosition){
        if(animals.containsKey(newPosition)) {
            ArrayList<Animal> animalsList = animals.get(newPosition);
            int i =0;
            while(i < animalsList.size() && animalsList.get(i).getEnergy() > animal.getEnergy()) i++;
            animalsList.add(i,animal);
        }
        else{
            ArrayList<Animal> animalsList = new ArrayList<>();
            animalsList.add(animal);
            animals.put(newPosition,animalsList);
        }
    }

    public void positionChanged(Animal animal, Vector2d newPosition) {
        if(animals.containsKey(animal.getPosition())) {
            ArrayList<Animal> animalsList = animals.get(animal.getPosition());
            animalsList.remove(animal);
            animals.replace(animal.getPosition(),animalsList);
        }
        if(newPosition != null) putAnimalValues(animal,newPosition);

    }

    public Map<Vector2d,Grass> getJungleGrass(){
        return jungleGrass;
    }

    public Map<Vector2d,Grass> getStepGrass(){
        return stepGrass;
    }

    public Map<Vector2d, ArrayList<Animal>> getAnimals(){
        return animals;
    }

    public void addStepGrass(int GrassCount){
        Random r = new Random();
        if(stepGrass.size() >= mapAvailablePositions-jungleAvailablePositions) return;

        int ForeverLotsLops = 0;
        for (int i=0; i<GrassCount; i++){
            Vector2d pos = new Vector2d(r.nextInt(mapHigher.x-mapLower.x+1)+mapLower.x,r.nextInt(mapHigher.y-mapLower.y+1)+mapLower.y);
            if(ForeverLotsLops > 10){
                for(int x=mapLower.x; x<mapHigher.x+1; x++){
                    for(int y=mapLower.y; y<mapHigher.y+1; y++){
                        pos = new Vector2d(x,y);
                        if(!isOccupied(pos) && !(pos.precedes(jungleHigher) && pos.follows(jungleLower))) {
                            stepGrass.put(pos,new Grass(pos));
                            return;
                        }
                    }
                }
            }
            else if(!isOccupied(pos) && !(pos.precedes(jungleHigher) && pos.follows(jungleLower))) {
                stepGrass.put(pos,new Grass(pos));
                ForeverLotsLops = 0;
            }
            else {
                ForeverLotsLops += 1;
                i -= 1;
            }
        }
    }

    public void addJungleGrass(int GrassCount){
        Random r = new Random();
        if(jungleGrass.size() >= jungleAvailablePositions) return;
        int ForeverLotsLops = 0;

        for (int i=0; i<GrassCount; i++){
            Vector2d pos = new Vector2d(r.nextInt(jungleHigher.x-jungleLower.x+1) + jungleLower.x,r.nextInt(jungleHigher.y-jungleLower.y+1) + jungleLower.y);

            if (ForeverLotsLops <= 10) {
                if(!isOccupied(pos)) {
                    jungleGrass.put(pos, new Grass(pos));
                    ForeverLotsLops = 0;
                }
                else {
                    ForeverLotsLops += 1;
                    i -= 1;
                }
            }
            else {
                for(int x=jungleLower.x; x<jungleHigher.x+1; x++){
                    for(int y=jungleLower.y; y<jungleHigher.y+1; y++){
                        pos = new Vector2d(x,y);
                        if(!jungleGrass.containsKey(pos)) {
                            jungleGrass.put(pos,new Grass(pos));
                            return;
                        }
                    }
                }
            }
        }
    }
    public void eatGrass(Vector2d pos,int grassEnergy){
        ArrayList<Animal> animalsList = animals.get(pos);
        int MaxEnergy = animalsList.get(0).getEnergy();
        int i=0;
        while(i < animalsList.size() && animalsList.get(i).getEnergy() == MaxEnergy) i++;

        for(int idx=0; idx<i; idx++){
            animals.get(pos).get(idx).addEnergy(grassEnergy/i);
        }
    }
    public void removeGrass(Vector2d pos){
        if(jungleGrass.containsKey(pos)) jungleGrass.remove(pos);
        else stepGrass.remove(pos);
    }
}
