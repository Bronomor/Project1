package Engine;

import Elements.*;
import MapParameters.MapParameters;

import java.util.*;

public class SimulationEngine extends AbstractGenotype implements IEngine, IGenotypeConverter {

    private final IWorldMap iWorldMap;
    private final List<Animal> animals = new ArrayList<>();
    private final int startEnergy;
    private final int grassEnergy;
    private final int moveEnergy;
    private int totalAnimalEnergy = 0;
    private int deadAnimalTime = 0;
    private int deadAnimalAmount = 0;
    private int totalChildren = 0;
    private final TreeMap<String, Integer> allGenotypes = new TreeMap<>((o1, o2) -> {
        if (o1.equals(o2)) return 0;
        else return o1.compareTo(o2);
    });
    protected Biomes[] biomes;

    public SimulationEngine(IWorldMap iWorldMap, ArrayList<Vector2d> positions, MapParameters mapParameters, Vector2d[][] biomesRestriction){
        this.iWorldMap = iWorldMap;
        this.startEnergy = mapParameters.getStartEnergy();
        this.grassEnergy = mapParameters.getPlantEnergy();
        this.moveEnergy = mapParameters.getMoveEnergy();

        for (Vector2d pos : positions) {
            Animal animal = new Animal(iWorldMap,pos, startEnergy,0);

            // Create random genotype
            short[] genotype = createGenotype(null,null);

            // Check the correctness of the genotype
            if(incorrectGenotype(genotype)) repairGenotype(genotype);
            animal.setGenotype(genotype);
            String tmp = genotypeToString(genotype);
            if(allGenotypes.containsKey(tmp)) allGenotypes.put(tmp, allGenotypes.get(tmp) + 1);
            else allGenotypes.put(tmp,1);

            this.iWorldMap.place(animal);
            animals.add(animal);
        }

        biomes = new Biomes[biomesRestriction.length];
        for(int i=0; i< biomes.length;i++) biomes[i] = new Biomes(biomesRestriction[i][0],biomesRestriction[i][1],biomesRestriction[i][2],biomesRestriction[i][3],iWorldMap);
    }

    @Override
    public void run(int actualEpoch,boolean keepChildren,boolean allEpoch) {

        removeDeadAnimals(actualEpoch,allEpoch);

        // Move Animals
        HashSet<Vector2d> suspectReproducePositions = new HashSet<>(moveAnimals());

        // Feed animals after move
        totalAnimalEnergy = 0;
        for (Animal animal : animals) {
            Vector2d position = animal.getPosition();
            for (Biomes biom : biomes) {
                if (biom.containPosition(position)) {
                    iWorldMap.giveGrassAnimal(position, grassEnergy);
                    biom.removeGrass(position);
                    break;
                }
            }
            totalAnimalEnergy += animal.getEnergy();
        }

        animalReproduction(suspectReproducePositions, actualEpoch,keepChildren);

        // Reproduce Grass
        for (Biomes biome : biomes) biome.addGrass();
    }

    private void removeDeadAnimals(int actualEpoch,boolean allEpoch){
        Iterator<Animal> iterator = animals.iterator();
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            if(animal.getEnergy() <= 0) {
                deadAnimalTime += actualEpoch - animal.getBornEpoch();
                deadAnimalAmount += 1;

                String tmp = genotypeToString(animal.getGenotype());
                if(allGenotypes.get(tmp) != null && allGenotypes.get(tmp) > 1 && !allEpoch) allGenotypes.put(tmp,allGenotypes.get(tmp) -1);
                else if(!allEpoch) allGenotypes.remove(tmp);

                if(animal.getIsChild() && !allEpoch) totalChildren -= 1;
                animal.positionChanged(animal, null);
                animal.removeObserver(animal);
                iterator.remove();
            }
        }
    }

    private HashSet<Vector2d> moveAnimals(){
        HashSet<Vector2d> SuspiciouslyMultiplicationPosition = new HashSet<>();
        for (Animal animal : animals){
            animal.move();
            if(iWorldMap.getAnimals().get(animal.getPosition()).size() >= 2) SuspiciouslyMultiplicationPosition.add(animal.getPosition());
            animal.subtractEnergy(moveEnergy);
        }
        return SuspiciouslyMultiplicationPosition;
    }
    
    private void animalReproduction(HashSet<Vector2d> suspectReproducePositions,int actualEpoch,boolean keepChildren){
        for (Vector2d position : suspectReproducePositions) {
            MultiplyAnimal multiplyAnimal = new MultiplyAnimal(iWorldMap.getAnimals().get(position),iWorldMap,position,startEnergy,actualEpoch, this);
            Animal animal = multiplyAnimal.createChild(keepChildren);
            if(animal != null) {
                animals.add(animal);

                String tmp = genotypeToString(animal.getGenotype());
                if(allGenotypes.containsKey(tmp)) allGenotypes.put(tmp,allGenotypes.get(tmp)+1);
                else allGenotypes.put(tmp,1);
                totalChildren +=1;
            }
        }
    }
    
    public short[] createGenotype(Animal animal1, Animal animal2) {
        short[] genotype = new short[32];
        for(int i=0; i<32; i++){
            int kind =  new Random().nextInt(8);
            genotype[i] = (short) kind;
        }
        Arrays.sort(genotype);
        return genotype;
    }
    
    private String findDominantGenotype(){
        int tmpMax = 0;
        String result = "";
        for(Map.Entry<String,Integer> entry : allGenotypes.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if(value > tmpMax){
                result = key;
            }
        }
        return result;
    }

    public void clearDominantGenotype() {
        this.allGenotypes.clear();
        for (Animal animal : animals) {
            String genotype = genotypeToString(animal.getGenotype());
            if (allGenotypes.containsKey(genotype))
                allGenotypes.put(genotype, allGenotypes.get(genotype) + 1);
            else allGenotypes.put(genotype, 1);
        }
    }
    
    public int getAnimalsAmount() { return animals.size(); }
    public int getAverageAnimalEnergy() { return animals.size() > 0 ? totalAnimalEnergy / animals.size() : 0; }
    public int getAverageAnimalTime() { return deadAnimalTime > 0 ? deadAnimalTime/deadAnimalAmount : 0; }
    public double getAverageAnimalChildren() { return animals.size() > 0 ? (double) totalChildren / (animals.size()) : 0; }
    public String getDominantGenotype() { return findDominantGenotype(); }
    public int getTotalAnimalEnergy() { return totalAnimalEnergy; }
    public int getDeadAnimalTime() { return deadAnimalTime; }
    public int getDeadAnimalAmount() { return deadAnimalAmount; }
    public int getTotalChildren() { return totalChildren; }
    public Biomes[] getBiomes() { return biomes != null ? biomes : new Biomes[0]; }


}