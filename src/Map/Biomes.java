package Map;

import Components.Grass;
import Components.Vector2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class Biomes {
    private final Vector2d lower;
    private final Vector2d higher;
    private final Vector2d lowerCase;
    private final Vector2d upperCase;
    private int mapAvailablePositions;
    private HashSet<Vector2d> grass = new HashSet<>();
    private IWorldMap iWorldMap;

    public Biomes(Vector2d lower, Vector2d higher, Vector2d lowerCase, Vector2d upperCase, IWorldMap iWorldMap){
        this.lower = lower;
        this.higher = higher;
        this.lowerCase = lowerCase;
        this.upperCase = upperCase;
        this.mapAvailablePositions = (higher.x- lower.x+1) * (higher.y-lower.y+1);
        if(lowerCase != null) this.mapAvailablePositions -=  (upperCase.x- lowerCase.x+1) * (upperCase.y-lowerCase.y+1);
        this.iWorldMap = iWorldMap;
    }

    public Vector2d addGrass(){
        Random r = new Random();
        if(grass.size() >= mapAvailablePositions) return null;

        int ForeverLotsLops = 0;
        for (int i=0; i<1; i++){
            Vector2d pos = new Vector2d(r.nextInt(higher.x-lower.x+1)+lower.x,r.nextInt(higher.y-lower.y+1)+lower.y);
            if(ForeverLotsLops > 0.7*mapAvailablePositions){
                for(int x=lower.x; x<higher.x+1; x++){
                    for(int y=lower.y; y<higher.y+1; y++){
                        pos = new Vector2d(x,y);
                        if(!iWorldMap.isOccupied(pos)) {
                            if(lowerCase != null){
                                if(!(pos.precedes(upperCase) && pos.follows(lowerCase))){
                                    grass.add(pos);
                                    return pos;
                                }
                            }
                            else{
                                grass.add(pos);
                                return pos;
                            }

                        }
                    }
                }
            }
            else if(!iWorldMap.isOccupied(pos)) {
                if(lowerCase != null){
                    if(!(pos.precedes(upperCase) && pos.follows(lowerCase))){
                        grass.add(pos);
                        return pos;
                    }
                }
                else{
                    grass.add(pos);
                    return pos;
                }
            }
            else {
                ForeverLotsLops += 1;
                i -= 1;
            }
        }
        return null;
    }

    public void removeGrass(Vector2d pos){
        grass.remove(pos);
    }

    public boolean containPosition(Vector2d pos){ return grass.contains(pos); }

}
