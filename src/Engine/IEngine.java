package Engine;

import Elements.Biomes;


public interface IEngine {

    void run(int ActualEpoch, boolean keepChildren, boolean allEpoch);
    void clearDominantGenotype();

    int getAnimalsAmount();
    double getAverageAnimalChildren();
    int getAverageAnimalEnergy();
    int getAverageAnimalTime();
    Biomes[] getBiomes();
    int getDeadAnimalTime();
    int getDeadAnimalAmount();
    String getDominantGenotype();
    int getTotalAnimalEnergy();
    int getTotalChildren();

}

