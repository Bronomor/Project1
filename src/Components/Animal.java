package Components;

import Map.IWorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal implements IPositionChangeObserver {
    private MapDirection orientation;
    private Vector2d position;
    private final IWorldMap map;
    private final List<IPositionChangeObserver> observers = new ArrayList<>();

    private final int bornEpoch;
    private int energy;
    private ArrayList<Animal> children = new ArrayList<>();
    private short[] genotype = new short[32];

    private final Boolean copulationProduct;

    public Animal(IWorldMap map, Vector2d initialPosition, int energy, int bornEpoch, boolean copulationProduct){
        this.energy = energy;
        this.bornEpoch = bornEpoch;
        this.map = map;
        this.position = initialPosition;
        this.orientation = numberToDirection(new Random().nextInt(8));
        this.copulationProduct = copulationProduct;

        addObserver((IPositionChangeObserver) map);
    }

    public String toString(){
        return switch (this.orientation) {
            case NORTH -> "N";
            case NORTHEAST -> "NE";
            case NORTHWEST -> "NW";
            case SOUTH -> "S";
            case SOUTHEAST -> "SE";
            case SOUTHWEST -> "SW";
            case EAST -> "E";
            case WEST -> "W";
        };
    }

    public MapDirection numberToDirection(int number) {
        return switch (number) {
            case 0 -> MapDirection.NORTH;
            case 1 -> MapDirection.NORTHEAST;
            case 2 -> MapDirection.EAST;
            case 3 -> MapDirection.SOUTHEAST;
            case 4 -> MapDirection.SOUTH;
            case 5 -> MapDirection.SOUTHWEST;
            case 6 -> MapDirection.WEST;
            case 7 -> MapDirection.NORTHWEST;
            default -> null;
        };
    }

    public void move(){
        Random random = new Random();
        int move = random.nextInt(32);
        switch (genotype[move]){
            case 7:
                this.orientation = this.orientation.next();
            case 6:
                this.orientation = this.orientation.next();
            case 5:
                this.orientation = this.orientation.next();
            case 4:
                this.orientation = this.orientation.next();
            case 3:
                this.orientation = this.orientation.next();
            case 2:
                this.orientation = this.orientation.next();
            case 1:
                this.orientation = this.orientation.next();
            default:
                break;
        }
        Vector2d positionNew = map.AdjustingPositionToMap(this.position.add(this.orientation.toUnitVector()));
        this.positionChanged(this, positionNew);
        position = positionNew;
    }

    public void addObserver(IPositionChangeObserver observer){
        observers.add(observer);
    }
    public void removeObserver(IPositionChangeObserver observer){
        observers.remove(observer);
    }
    public void positionChanged(Animal animal, Vector2d newPosition){
        for(IPositionChangeObserver obs : observers){
            obs.positionChanged(animal,newPosition);
        }
    }

    public void addChild(Animal animal) {this.children.add(animal);}
    public void subtractEnergy(int energy) {
        this.energy -= energy;
    }
    public void resetChildren() {this.children = new ArrayList<>();}
    public void setGenotype(short[] Genotype) {this.genotype = Genotype;}
    public void addEnergy(int energy) {this.energy += energy; }

    public Vector2d getPosition() {
        return this.position;
    }
    public int getEnergy() {return this.energy; }
    public short[] getGenotype() {return this.genotype;}
    public String getStringGenotype() {
        StringBuilder tmp = new StringBuilder();
        for(int i=0; i<32; i++) tmp.append(this.genotype[i]);
        return tmp.toString();
    }
    public int getBornEpoch() {return bornEpoch;}
    public ArrayList<Animal> getChildren() {return this.children;}
    public boolean getCopulationProduct() {return copulationProduct;}

}
