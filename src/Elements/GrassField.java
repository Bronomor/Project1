package Elements;

import MapParameters.MapParameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrassField implements IWorldMap, IPositionChangeObserver {

    private final HashMap<Vector2d, Grass> grass = new HashMap<>();
    private final HashMap<Vector2d, ArrayList<Animal>> animals = new HashMap<>();

    private final Vector2d mapLower;
    private final Vector2d mapHigher;

    public GrassField(MapParameters mapParameters) {
        mapLower = mapParameters.getMapLower();
        mapHigher = mapParameters.getMapHigher();
    }

    public void place(Animal animal) { positionChanged(animal, animal.getPosition()); }

    public boolean isOccupied(Vector2d position) {
        if(animals.containsKey(position)){
            if(animals.get(position).size() > 0) return true;
        }
        return grass.containsKey(position);
    }

    public Vector2d adjustingPositionToMap(Vector2d position){
        if (position == null) return null;

        if (position.x < mapLower.x) position = position.add(new Vector2d(mapHigher.x-mapLower.x+1,0));
        else if (position.x >= mapHigher.x+1) position = position.subtract(new Vector2d(mapHigher.x-mapLower.x+1,0));

        if (position.y < mapLower.y) position = position.add(new Vector2d(0,mapHigher.y-mapLower.y+1));
        else if (position.y >= mapHigher.y+1) position = position.subtract(new Vector2d(0,mapHigher.y-mapLower.y+1));

        return position;
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

    public Map<Vector2d, Grass> getGrass() { return grass; }
    public Map<Vector2d, ArrayList<Animal>> getAnimals(){ return animals; }
    public ArrayList<Animal> getAnimalsAtPosition(Vector2d position) { return animals.get(position); }
    public Animal getAnimal(Vector2d position, int index) { return animals.get(position) != null ? animals.get(position).get(index) : null; }

    public void giveGrassAnimal(Vector2d pos,int grassEnergy){
        ArrayList<Animal> animalsList = animals.get(pos);
        int MaxEnergy = animalsList.get(0).getEnergy();
        int animalWithMaxEnergy=0;
        while(animalWithMaxEnergy < animalsList.size() && animalsList.get(animalWithMaxEnergy).getEnergy() == MaxEnergy) animalWithMaxEnergy++;

        if (animalWithMaxEnergy != 0) {
            for (int idx = 0; idx < animalWithMaxEnergy; idx++) {
                animals.get(pos).get(idx).addEnergy(grassEnergy / animalWithMaxEnergy);
            }
        }
    }
    public void addGrass(Vector2d pos){ grass.put(pos,new Grass(pos)); }
    public void removeGrass(Vector2d pos){ grass.remove(pos); }
}
