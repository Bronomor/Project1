package Engine;

import java.util.HashSet;

/**
 * The interface responsible for managing the moves of the animals.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo
 *
 */
public interface IEngine {
    /**
     * Move the animal on the map according to the provided move directions. Every
     * n-th direction should be sent to the n-th animal on the map.
     *
     */
    void run(int ActualEpoch, boolean keepChildren, boolean allEpoch);
    int getAnimalsAmount();
    int getAverageAnimalEnergy();
    double getAverageAnimalTime();
    double getAverageAnimalChildren();
    String dominantGenotype();
    int getTotalAnimalEnergy();
    int getDeadAnimalTime();
    int getDeadAnimalAmount();
    int getTotalChildren();
}

