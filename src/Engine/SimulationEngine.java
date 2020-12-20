package Engine;
import Components.Animal;
import Map.IWorldMap;
import Components.Vector2d;
import MapParameters.MapParameters;
import Map.Biomes;

import java.util.*;

public class SimulationEngine implements IEngine {

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
    Biomes[] biomes;

    public SimulationEngine(IWorldMap iWorldMap, ArrayList<Vector2d> positions, MapParameters mapParameters, Vector2d[][] biomesRestriction){
        this.iWorldMap = iWorldMap;
        this.startEnergy = mapParameters.getStartEnergy();
        this.grassEnergy = mapParameters.getPlantEnergy();
        this.moveEnergy = mapParameters.getMoveEnergy();

        for (Vector2d pos : positions) {
            Animal animal = new Animal(iWorldMap,pos, startEnergy,0,false);

            // Create random genotype
            short[] genotype = new short[32];
            for(int i=0; i<32; i++){
                int kind =  new Random().nextInt(8);
                genotype[i] = (short) kind;
            }

            // Check the correctness of the genotype
            Arrays.sort(genotype);
            if(!checkGenotypeValidation(genotype)) repairGenotype(genotype);
            animal.setGenotype(genotype);
            String tmp = animal.getStringGenotype();
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
            for (Biomes biome : biomes) {
                if (biome.containPosition(position)) {
                    iWorldMap.giveGrassAnimal(position, grassEnergy);
                    biome.removeGrass(position);
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

                String tmp = animal.getStringGenotype();
                if(allGenotypes.get(tmp) != null && allGenotypes.get(tmp) > 1 && !allEpoch) allGenotypes.put(tmp,allGenotypes.get(tmp) -1);
                else if(!allEpoch) allGenotypes.remove(tmp);

                if(animal.getCopulationProduct() && !allEpoch) totalChildren -= 1;
                animal.positionChanged(animal, null);
                animal.removeObserver(animal);
                iterator.remove();
            }
        }
    }
    private boolean checkGenotypeValidation(short[] genotype){
        short actualGene = 0;
        for(int i=0; i<32; i++){
            if(actualGene == genotype[i]) actualGene +=1;
        }
        return actualGene == 8;
    }
    private void repairGenotype(short[] genotype){
        int[] AreAllGenotype = new int [] {0,0,0,0,0,0,0,0};
        for(int i=0; i<32; i++) AreAllGenotype[genotype[i]] += 1;

        for(int i=0; i<8; i++){
            if(AreAllGenotype[i] == 0){
                int replace = new Random().nextInt(32);
                while(AreAllGenotype[genotype[replace]] < 2) replace = new Random().nextInt(32);
                genotype[replace] = (short) i;
            }
        }
        Arrays.sort(genotype);
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
            CreateAnimal createAnimal = new CreateAnimal(iWorldMap.getAnimals().get(position),iWorldMap,position,startEnergy,actualEpoch, this);
            ArrayList<Animal> animal = createAnimal.createChild();
            if(animal == null) continue;

            animals.add(animal.get(0));
            String tmp = animal.get(0).getStringGenotype();
            if(allGenotypes.containsKey(tmp)) allGenotypes.put(tmp,allGenotypes.get(tmp)+1);
            else allGenotypes.put(tmp,1);

            iWorldMap.place(animal.get(0));
            if(keepChildren) {
                animal.get(1).addChild(animal.get(0));
                animal.get(2).addChild(animal.get(0));
            }
            totalChildren +=1;
        }
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
            if (allGenotypes.containsKey(animal.getStringGenotype()))
                allGenotypes.put(animal.getStringGenotype(), allGenotypes.get(animal.getStringGenotype()) + 1);
            else allGenotypes.put(animal.getStringGenotype(), 1);
        }
    }
    public int getAnimalsAmount() {return animals.size(); }
    public int getAverageAnimalEnergy() {return animals.size() > 0 ? totalAnimalEnergy / animals.size() : 0;}
    public int getAverageAnimalTime() {return deadAnimalTime > 0 ? deadAnimalTime/deadAnimalAmount : 0;}
    public double getAverageAnimalChildren() { return animals.size() > 0 ? (double) totalChildren / (animals.size()) : 0;}
    public String dominantGenotype() { return findDominantGenotype();}
    public int getTotalAnimalEnergy() { return totalAnimalEnergy; }
    public int getDeadAnimalTime() {return deadAnimalTime;}
    public int getDeadAnimalAmount() {return deadAnimalAmount;}
    public int getTotalChildren() {return totalChildren;}
    public Biomes[] getBiomes() {return biomes != null ? biomes : new Biomes[0];}
}