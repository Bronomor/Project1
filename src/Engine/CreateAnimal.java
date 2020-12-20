package Engine;
import Components.*;
import Map.IWorldMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CreateAnimal {
    private ArrayList<Animal> animalArray;
    private IWorldMap iWorldMap;
    private Vector2d position;
    private int startEnergy;
    private int actualEpoch;

    public CreateAnimal(ArrayList<Animal> animalArray, IWorldMap iWorldMap, Vector2d position, int startEnergy, int actualEpoch) {
        this.iWorldMap = iWorldMap;
        this.animalArray = animalArray;
        this.position = position;
        this.startEnergy = startEnergy;
        this.actualEpoch = actualEpoch;
    }

    public ArrayList<Animal> createChild() {
        // ktorej zwierze ruszyło sie z pozycji
        if (animalArray.size() < 2) return null;

        int healthyAnimals = 0;
        int[] idx = new int[2];
        Animal[] animalsToCopulation = new Animal[2];
        for (int i = 0; i < animalArray.size(); i++) {
            if (animalArray.get(i).getEnergy() >= this.startEnergy / 2) {
                if (healthyAnimals < 2) {
                    animalsToCopulation[healthyAnimals] = animalArray.get(i);
                    idx[healthyAnimals] = i;
                    healthyAnimals++;
                } else break;
            }
        }

        // Jesli nie ma zdrwych zwierzat
        if (healthyAnimals < 2) return null;

        Animal animal = null;
        Animal animal1 = animalsToCopulation[0];
        Animal animal2 = animalsToCopulation[1];

        //szukanie wolnej pozycji
        boolean allPositionOccupied = true;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue; // pozycja rodziców

                if (!iWorldMap.isOccupied(position.add(new Vector2d(i, j)))) {
                    animal = new Animal(iWorldMap, position.add(new Vector2d(i, j)), animal1.getEnergy() / 4 + animal2.getEnergy() / 4, actualEpoch, true);
                    allPositionOccupied = false;
                    break;
                }
            }
            if (!allPositionOccupied) break;
        }

        // jesli wszystkie pozycje sa zajete
        if (allPositionOccupied) {
            int x = new Random().nextInt(2) - 1;
            int y = new Random().nextInt(2) - 1;

            if (x == 0 && y == 0) y = 1;
            Vector2d position2 = position.add(new Vector2d(x, y));
            animal = new Animal(iWorldMap, position2, animal1.getEnergy() / 4 + animal2.getEnergy() / 4, actualEpoch, true);
            if (iWorldMap.getJungleGrass().containsKey(position2)) iWorldMap.getJungleGrass().remove(position2);
            if (iWorldMap.getStepGrass().containsKey(position2)) iWorldMap.getStepGrass().remove(position2);
        }

        animal1.subtractEnergy(animal1.getEnergy() / 4);
        animal2.subtractEnergy(animal2.getEnergy() / 4);
        iWorldMap.getAnimals().get(position).get(idx[0]).subtractEnergy(animal1.getEnergy() / 4);
        iWorldMap.getAnimals().get(position).get(idx[1]).subtractEnergy(animal2.getEnergy() / 4);

        // tworzenie genów
        short[] genotype = new short[32];
        short[][] genotypeParents = new short[2][32];
        genotypeParents[0] = animal1.getGenotype();
        genotypeParents[1] = animal2.getGenotype();

        int pipe1 = new Random().nextInt(31) + 1;
        int pipe2 = new Random().nextInt(31) + 1;
        while (pipe2 == pipe1) pipe2 = new Random().nextInt(31) + 1;

        if (pipe1 > pipe2) {
            int tmp = pipe1;
            pipe1 = pipe2;
            pipe2 = tmp;
        }

        int segment = new Random().nextInt(3);
        int TwoGenes = new Random().nextInt(2);
        int whoseSegment = 0;
        for (int i = 0; i < 32; i++) {
            if (TwoGenes == 0 && segment != whoseSegment) genotype[i] = genotypeParents[0][i];
            else if (TwoGenes == 0) genotype[i] = genotypeParents[1][i];
            else if (segment != whoseSegment) genotype[i] = genotypeParents[1][i];
            else genotype[i] = genotypeParents[0][i];

            if (i >= pipe1) whoseSegment = 1;
            else if (i >= pipe2) whoseSegment = 2;
        }

        // sprawdzanie wybrakowanego genotypu
        Arrays.sort(genotype);
        if (!checkGenotypeValidation(genotype)) genotype = repairGenotype(genotype);
        animal.setGenotype(genotype);

        ArrayList<Animal> result = new ArrayList<>();
        result.add(animal);
        result.add(animal1);
        result.add(animal2);
        return result;
    }

    private boolean checkGenotypeValidation(short[] genotype) {
        short actualGene = 0;
        for (int i = 0; i < 32; i++) {
            if (actualGene == genotype[i]) actualGene += 1;
        }
        if (actualGene == 8) return true;
        return false;
    }
    private short[] repairGenotype(short[] genotype) {
        int[] AreAllGenotype = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 32; i++) AreAllGenotype[genotype[i]] += 1;

        for (int i = 0; i < 8; i++) {
            if (AreAllGenotype[i] == 0) {
                int replace = new Random().nextInt(32);
                while (AreAllGenotype[genotype[replace]] < 2) replace = new Random().nextInt(32);
                genotype[replace] = (short) i;
            }
        }
        Arrays.sort(genotype);
        return genotype;
    }
}
