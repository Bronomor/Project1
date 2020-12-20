package Map;

import Components.Animal;
import Components.Grass;
import Components.Vector2d;

import java.util.ArrayList;
import java.util.Map;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo
 *
 */
public interface IWorldMap {

    void place(Animal animal);

    /**
     * Return true if given position on the map is occupied. Should not be
     * confused with canMove since there might be empty positions where the animal
     * cannot move.
     *
     * @param position
     *            Position to check.
     * @return True if the position is occupied.
     */
    boolean isOccupied(Vector2d position);

    Vector2d AdjustingPositionToMap(Vector2d position);

    Map<Vector2d, Grass> getGrass();
    Map<Vector2d, ArrayList<Animal>> getAnimals();

    void giveGrassAnimal(Vector2d position, int grassEnergy);
    void addGrass(Vector2d position);
    void removeGrass(Vector2d position);
}