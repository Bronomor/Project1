package Engine;
import Elements.Animal;
import Elements.IWorldMap;
import Elements.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MultiplyAnimal extends AbstractGenotype {
    private final ArrayList<Animal> animalArray;
    private final IWorldMap iWorldMap;
    private final Vector2d position;
    private final int startEnergy;
    private final int actualEpoch;
    private final IEngine engine;

    public MultiplyAnimal(ArrayList<Animal> animalArray, IWorldMap iWorldMap, Vector2d position, int startEnergy, int actualEpoch, IEngine engine) {
        this.iWorldMap = iWorldMap;
        this.animalArray = animalArray;
        this.position = position;
        this.startEnergy = startEnergy;
        this.actualEpoch = actualEpoch;
        this.engine = engine;
    }

    public Animal createChild(boolean keepChildren) {

        if (animalArray.size() < 2) return null;

        int healthyAnimals = 0;
        int[] index = new int[2];
        Animal[] animalParents = new Animal[2];
        for (int i = 0; i < animalArray.size(); i++) {
            if (animalArray.get(i).getEnergy() >= this.startEnergy / 2) {
                if (healthyAnimals < 2) {
                    animalParents[healthyAnimals] = animalArray.get(i);
                    index[healthyAnimals] = i;
                    healthyAnimals++;
                } else break;
            }
        }

        if (healthyAnimals < 2) return null;

        Animal animal;
        Animal animal1 = animalParents[0];
        Animal animal2 = animalParents[1];

        Vector2d positionEmpty = findEmptyPosition(position);

        animal = new Animal(iWorldMap, positionEmpty, animal1.getEnergy() / 4 + animal2.getEnergy() / 4, actualEpoch);
        animal1.subtractEnergy(animal1.getEnergy() / 4);
        animal2.subtractEnergy(animal2.getEnergy() / 4);
        iWorldMap.getAnimal(position,index[0]).subtractEnergy(animal1.getEnergy() / 4);
        iWorldMap.getAnimal(position,index[1]).subtractEnergy(animal2.getEnergy() / 4);

        short[] genotype = createGenotype(animal1, animal2);
        if (incorrectGenotype(genotype)) repairGenotype(genotype);
        animal.setGenotype(genotype);

        iWorldMap.place(animal);

        if(keepChildren) {
            animal1.addChild(animal);
            animal2.addChild(animal);
        }
        return animal;
    }
    private Vector2d findEmptyPosition(Vector2d position) {

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue; // pozycja rodziców
                if (!iWorldMap.isOccupied(position.add(new Vector2d(i, j)))) return position.add(new Vector2d(i, j));
            }
        }

        // jesli wszystkie pozycje sa zajete
        int x = new Random().nextInt(2) - 1;
        int y = new Random().nextInt(2) - 1;

        if (x == 0 && y == 0) y = 1;
        Vector2d position2 = position.add(new Vector2d(x, y));

        for (int i = 0; i < engine.getBiomes().length; i++)
            if (engine.getBiomes()[i].containPosition(position2)) engine.getBiomes()[i].removeGrass(position2);

        return position2;
    }
    public short[] createGenotype(Animal animal1, Animal animal2) {
        short[] genotype = new short[32];
        short[][] genotypeParents = new short[2][32];
        genotypeParents[0] = animal1.getGenotype();
        genotypeParents[1] = animal2.getGenotype();

        int cut1 = new Random().nextInt(31) + 1;
        int cut2 = new Random().nextInt(31) + 1;
        while (cut1 == cut2) cut2 = new Random().nextInt(31) + 1;

        if (cut1 > cut2) {
            int tmp = cut1;
            cut1 = cut2;
            cut2 = tmp;
        }

        int segment = new Random().nextInt(3);
        int twoGenes = new Random().nextInt(2);
        int segmentID = 0;
        for (int i = 0; i < 32; i++) {
            if (twoGenes == 0 && segment != segmentID) genotype[i] = genotypeParents[0][i];
            else if (twoGenes == 0) genotype[i] = genotypeParents[1][i];
            else if (segment != segmentID) genotype[i] = genotypeParents[1][i];
            else genotype[i] = genotypeParents[0][i];

            if (i >= cut1) segmentID = 1;
            else if (i >= cut2) segmentID = 2;
        }
        Arrays.sort(genotype);
        return genotype;
    }
}
