package Elements;

import java.util.ArrayList;
import java.util.Map;

public interface IWorldMap {

    void place(Animal animal);

    boolean isOccupied(Vector2d position);

    Vector2d adjustingPositionToMap(Vector2d position);

    Map<Vector2d, Grass> getGrass();
    Map<Vector2d, ArrayList<Animal>> getAnimals();
    ArrayList<Animal> getAnimalsAtPosition(Vector2d position);
    Animal getAnimal(Vector2d position, int index);

    void giveGrassAnimal(Vector2d position, int grassEnergy);
    void addGrass(Vector2d position);
    void removeGrass(Vector2d position);
}