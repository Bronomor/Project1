package MapParameters;

import Components.Vector2d;

public class MapParameters {
    private final int worldWidth;
    private final int worldHeight;
    private final Vector2d worldLower;
    private final Vector2d worldHigher;
    private final int mapAvailablePositions;

    private final int jungleHeight;
    private final int jungleWidth;
    private final Vector2d jungleLower;
    private final Vector2d jungleHigher;
    private final int jungleAvailablePositions;

    private final int animalsAmount;

    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private final int amountOfMaps;

    public MapParameters(){
        ReadJSONMapParameters readJSONMapParameters = new ReadJSONMapParameters();
        int[] Parameters = readJSONMapParameters.readParameters();

        worldWidth = Parameters[0];
        worldHeight = Parameters[1];
        worldLower = new Vector2d(0,0);
        worldHigher = new Vector2d(worldWidth-1,worldHeight-1);
        mapAvailablePositions = (worldHigher.x-worldLower.x+1)*(worldHigher.y-worldLower.y+1);

        int jungleRatio = Parameters[2];
        jungleWidth = Math.round(worldWidth / jungleRatio);
        jungleHeight = Math.round(worldHeight / jungleRatio);
        jungleLower  = new Vector2d(Math.round((worldWidth-jungleWidth) / 2),Math.round((worldHeight-jungleHeight) / 2));
        jungleHigher = new Vector2d(Math.round(jungleLower.x+jungleWidth-1),Math.round(jungleLower.y+jungleHeight-1));
        jungleAvailablePositions = (jungleWidth)*(jungleHeight);

        animalsAmount = Parameters[3];
        startEnergy = Parameters[4];
        moveEnergy = Parameters[5];
        plantEnergy = Parameters[6];
        amountOfMaps = Parameters[7];
    }

    public int getMapWidth() {
        return this.worldWidth;
    }
    public int getMapHeight(){
        return this.worldHeight;
    }
    public Vector2d getMapLower() { return this.worldLower;}
    public Vector2d getMapHigher() { return this.worldHigher;}
    public int getMapAvailablePositions() {return this.mapAvailablePositions; }

    public int getJungleWidth(){
        return this.jungleWidth;
    }
    public int getJungleHeight() { return this.jungleHeight; }
    public Vector2d getJungleLower(){
        return this.jungleLower;
    }
    public Vector2d getJungleHigher(){
        return this.jungleHigher;
    }
    public int getJungleAvailablePositions() {return this.jungleAvailablePositions; }

    public int getStartEnergy() {return startEnergy;}
    public int getAnimalsAmount() {return animalsAmount;}
    public int getPlantEnergy() {return plantEnergy;}
    public int getMoveEnergy() {return moveEnergy; }
    public int getAmountOfMaps() { return amountOfMaps; }
}
