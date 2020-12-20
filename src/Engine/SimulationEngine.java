package Engine;
import Components.Animal;
import Components.Grass;
import Map.IWorldMap;
import Components.Vector2d;
import MapParameters.MapParameters;
import Map.Biome;

import java.util.*;

public class SimulationEngine implements IEngine {

    private IWorldMap iWorldMap;
    private final List<Animal> animals = new ArrayList<>();
    private final int startEnergy;
    private final int grassEnergy;
    private final int moveEnergy;
    private int totalAnimalEnergy = 0;
    private int deadAnimalTime = 0;
    private int deadAnimalAmount = 0;
    private int totalChildren = 0;
    private TreeMap<String, Integer> AllGenotypes = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            if (o1.equals(o2)) return 0;
            else return o1.compareTo(o2);
        }
    });
    Biome jungle;
    Biome step;


    public SimulationEngine(IWorldMap iWorldMap, ArrayList<Vector2d> positions, MapParameters mapParameters){
        this.iWorldMap = iWorldMap;
        this.startEnergy = mapParameters.getStartEnergy();
        this.grassEnergy = mapParameters.getPlantEnergy();
        this.moveEnergy = mapParameters.getMoveEnergy();

        for (Vector2d pos : positions) {
            Animal animal = new Animal(iWorldMap,pos, startEnergy,0,false);

            short[] genotype = new short[32];
            for(int i=0; i<32; i++){
                int kind =  new Random().nextInt(8);
                genotype[i] = (short) kind;
            }

            Arrays.sort(genotype);
            if(!checkGenotypeValidation(genotype)) genotype = repairGenotype(genotype);
            animal.setGenotype(genotype);
            String tmp = animal.getStringGenotype();
            if(AllGenotypes.containsKey(tmp)) AllGenotypes.put(tmp, AllGenotypes.get(tmp) +1);
            else AllGenotypes.put(tmp,1);
            this.iWorldMap.place(animal);
            animals.add(animal);
        }

        jungle = new Biome(mapParameters.getJungleLower(),mapParameters.getJungleHigher(),iWorldMap);
        step = new Biome(mapParameters.getMapLower(), mapParameters.getMapHigher(), mapParameters.getJungleLower(), mapParameters.getJungleHigher(), iWorldMap);
    }

    @Override
    public void run(int actualEpoch,boolean keepChildren,boolean allEpoch) {
        removeDeadAnimals(actualEpoch,allEpoch);

        // Tablica pozycji które są podejrzane o to że zwierzęta na nich mogę się rozmnożyć
        HashSet<Vector2d> SuspiciouslyMultiplicationPosition = new HashSet<>();
        SuspiciouslyMultiplicationPosition.addAll(moveAnimals());

        grassEat();
        animalReproduction(SuspiciouslyMultiplicationPosition, actualEpoch,keepChildren);
        grassReproduction(1);
    }

    private String genotypeToString(short[] genotype){
        String tmp = "";
        for(int i=0; i<32; i++) tmp += Short.toString(genotype[i]);
        return tmp;
    }
    private void removeDeadAnimals(int actualEpoch,boolean allEpoch){
        Iterator<Animal> iter = animals.iterator();
        while (iter.hasNext()) {
            Animal animal = iter.next();
            if(animal.getEnergy() <= 0) {
                deadAnimalTime += actualEpoch - animal.getBornEpoch();
                deadAnimalAmount += 1;

                String tmp = genotypeToString(animal.getGenotype());
                if(AllGenotypes.get(tmp) != null && AllGenotypes.get(tmp) > 1 && !allEpoch) AllGenotypes.put(tmp,AllGenotypes.get(tmp) -1);
                else if(!allEpoch) AllGenotypes.remove(tmp);

                if(animal.getCopulationProduct() && !allEpoch) totalChildren -= 1;
                animal.positionChanged(animal, null);
                animal.removeObserver(animal);
                iter.remove();
            }
        }
    }

    private boolean checkGenotypeValidation(short[] genotype){
        short actualGene = 0;
        for(int i=0; i<32; i++){
            if(actualGene == genotype[i]) actualGene +=1;
        }
        if(actualGene == 8) return true;
        return false;
    }
    private short[] repairGenotype(short[] genotype){
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
        return genotype;
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
    private void grassEat(){
        totalAnimalEnergy = 0;
        for (Animal animal : animals){
            if(iWorldMap.getJungleGrass().containsKey(animal.getPosition())){
                iWorldMap.eatGrass(animal.getPosition(),grassEnergy);
                iWorldMap.removeGrass(animal.getPosition());

            }
            else if(iWorldMap.getStepGrass().containsKey(animal.getPosition())){
                iWorldMap.eatGrass(animal.getPosition(),grassEnergy);
                iWorldMap.removeGrass(animal.getPosition());
            }
            totalAnimalEnergy += animal.getEnergy();
        }
    }
    private void animalReproduction(HashSet<Vector2d> SuspiciouslyMultiplicationPosition,int actualEpoch,boolean keepChildren){
        for (Vector2d position : SuspiciouslyMultiplicationPosition) {
            CreateAnimal createAnimal = new CreateAnimal(iWorldMap.getAnimals().get(position),iWorldMap,position,startEnergy,actualEpoch);

            ArrayList<Animal> animal = createAnimal.createChild();
            if(animal != null) {
                animals.add(animal.get(0));

                String tmp = animal.get(0).getStringGenotype();
                if(AllGenotypes.containsKey(tmp)) AllGenotypes.put(tmp,AllGenotypes.get(tmp).intValue()+1);
                else AllGenotypes.put(tmp,1);

                iWorldMap.place(animal.get(0));
                if(keepChildren) {
                    animal.get(1).addChild(animal.get(0));
                    animal.get(2).addChild(animal.get(0));
                }
                totalChildren +=1;
            }
        }
    }
    private void grassReproduction(int grassCount){
        //iWorldMap.addStepGrass(grassCount);
        //iWorldMap.addJungleGrass(grassCount);
       Vector2d pos = jungle.addGrass();
       if(pos != null) iWorldMap.getJungleGrass().put(pos,new Grass(pos));
       Vector2d pos2 = step.addGrass();
       if(pos2 != null) iWorldMap.getStepGrass().put(pos2,new Grass(pos2));
    }

    private String findDominantGenotype(){
        int tmpMax = 0;
        String result = "";
        for(Map.Entry<String,Integer> entry : AllGenotypes.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if(value > tmpMax){
                result = key;
            }
        }
        return result;
    }

    public int getAnimalsAmount() {return animals.size(); }
    public int getAverageAnimalEnergy() {return animals.size() > 0 ? totalAnimalEnergy / animals.size() : 0;}
    public double getAverageAnimalTime() {return (double) deadAnimalTime > 0 ? deadAnimalTime/deadAnimalAmount : 0;}
    public double getAverageAnimalChildren() { return animals.size() > 0 ? (double) totalChildren / (animals.size()) : 0;}
    public String dominantGenotype() { return findDominantGenotype();}
    public int getTotalAnimalEnergy() { return totalAnimalEnergy; }
    public int getDeadAnimalTime() {return deadAnimalTime;}
    public int getDeadAnimalAmount() {return deadAnimalAmount;}
    public int getTotalChildren() {return totalChildren;}
}