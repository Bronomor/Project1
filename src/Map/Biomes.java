package Map;

import Components.Vector2d;
import java.util.Random;

public class Biomes {
    private final Vector2d lower;
    private final Vector2d higher;
    private final Vector2d lowerHole;
    private final Vector2d upperHole;
    private int mapAvailablePositions;
    private final IWorldMap iWorldMap;
    private int grassCount = 0;

    public Biomes(Vector2d lower, Vector2d higher, Vector2d lowerHolePosition, Vector2d upperHolePosition, IWorldMap iWorldMap){
        this.lower = lower;
        this.higher = higher;
        this.lowerHole = lowerHolePosition;
        this.upperHole = upperHolePosition;
        this.mapAvailablePositions = (higher.x- lower.x+1) * (higher.y-lower.y+1);
        if(lowerHole != null) this.mapAvailablePositions -=  (upperHole.x- lowerHole.x+1) * (upperHole.y-lowerHole.y+1);
        this.iWorldMap = iWorldMap;
    }

    public void addGrass(){
        Random r = new Random();
        if(grassCount >= mapAvailablePositions) return;

        int ForeverLotsLops = 0;
        for (int i=0; i<1; i++){
            Vector2d pos = new Vector2d(r.nextInt(higher.x-lower.x+1)+lower.x,r.nextInt(higher.y-lower.y+1)+lower.y);

            if(checkPosition(pos)) iWorldMap.addGrass(pos);
            else if(ForeverLotsLops > 0.7*mapAvailablePositions) {
                pos = findEmptyPosition();
                if(pos != null) iWorldMap.addGrass(pos);
            }
            else {
                ForeverLotsLops += 1;
                i -= 1;
            }
        }
    }
    private boolean checkPosition(Vector2d pos){
        if(iWorldMap.isOccupied(pos)) return false;
        if(lowerHole != null) {
            return !pos.precedes(upperHole) || !pos.follows(lowerHole);
        }
        return true;
    }
    private Vector2d findEmptyPosition(){
        for(int x=lower.x; x<higher.x+1; x++){
            for(int y=lower.y; y<higher.y+1; y++){
                Vector2d pos = new Vector2d(x,y);
                if(checkPosition(pos)) return pos;
            }
        }
        return null;
    }

    public void removeGrass(Vector2d pos){
        grassCount -= 1;
        iWorldMap.removeGrass(pos);
    }
    public boolean containPosition(Vector2d pos){ return iWorldMap.getGrass().containsKey(pos); }
}
