package Engine;

import Elements.Animal;

import java.util.Arrays;
import java.util.Random;

public abstract class AbstractGenotype {

    protected abstract short[] createGenotype(Animal animal1, Animal animal2);

    protected boolean incorrectGenotype(short[] genotype) {
        short actualGene = 0;
        for (int i = 0; i < 32; i++) {
            if (actualGene == genotype[i]) actualGene += 1;
        }
        return actualGene != 8;
    }

    protected void repairGenotype(short[] genotype) {
        int[] areAllGenotype = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 32; i++) areAllGenotype[genotype[i]] += 1;

        for (int i = 0; i < 8; i++) {
            if (areAllGenotype[i] == 0) {
                int replace = new Random().nextInt(32);
                while (areAllGenotype[genotype[replace]] < 2) replace = new Random().nextInt(32);
                genotype[replace] = (short) i;
            }
        }
        Arrays.sort(genotype);
    }
}


