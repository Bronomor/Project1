package Components;

import Map.IWorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal implements IPositionChangeObserver {
    private MapDirection orientation;
    private Vector2d position;
    private final IWorldMap map;
    private List<IPositionChangeObserver> observers = new ArrayList<>();

    private int bornEpoch;
    private int energy;
    private ArrayList<Animal> children = new ArrayList<>();
    private short[] genotype = new short[32];

    private Boolean copulationProduct = false;

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
        switch(this.orientation){
            case NORTH:
                return "N";
            case NORTHEAST:
                return "NE";
            case NORTHWEST:
                return "NW";
            case SOUTH:
                return "S";
            case SOUTHEAST:
                return "SE";
            case SOUTHWEST:
                return "SW";
            case EAST:
                return "E";
            case WEST:
                return "W";
            default:
                return null;
        }
    }

    public MapDirection numberToDirection(int number) {
        switch(number) {
            case 0:
                return MapDirection.NORTH;
            case 1:
                return MapDirection.NORTHEAST;
            case 2:
                return MapDirection.EAST;
            case 3:
                return MapDirection.SOUTHEAST;
            case 4:
                return MapDirection.SOUTH;
            case 5:
                return MapDirection.SOUTHWEST;
            case 6:
                return MapDirection.WEST;
            case 7:
                return MapDirection.NORTHWEST;
            default:
                return null;
        }
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
        String tmp = "";
        for(int i=0; i<32; i++) tmp += Short.toString(this.genotype[i]);
        return tmp;
    }
    public int getBornEpoch() {return bornEpoch;}
    public ArrayList<Animal> getChildren() {return this.children;}
    public boolean getCopulationProduct() {return copulationProduct;}

}
