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

    boolean place(Animal animal);

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

    /**
     * Return an object at a given position.
     *
     * @param position
     *            The position of the object.
     * @return Object or null if the position is not occupied.
     */
    Object objectAt(Vector2d position);

    Vector2d AdjustingPositionToMap(Vector2d position);


    Map<Vector2d, Grass> getJungleGrass();
    Map<Vector2d,Grass> getStepGrass();

    Map<Vector2d, ArrayList<Animal>> getAnimals();

    void addJungleGrass(int GrassCount);
    void addStepGrass(int GrassCount);

    void eatGrass(Vector2d position, int grassEnergy);

    void removeGrass(Vector2d position);
}