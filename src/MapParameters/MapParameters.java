package MapParameters;

import Elements.Vector2d;

public class MapParameters {
    private final int worldWidth;
    private final int worldHeight;
    private final Vector2d worldLower;
    private final Vector2d worldHigher;

    private final int jungleHeight;
    private final int jungleWidth;
    private final Vector2d jungleLower;
    private final Vector2d jungleHigher;

    private final int animalsCount;
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private final int mapCount;

    public MapParameters() throws Exception {
        ReadJSONMapParameters readJSONMapParameters = new ReadJSONMapParameters();
        int[] Parameters = readJSONMapParameters.readParameters();

        if(Parameters[2] == 0) throw new IllegalArgumentException("Jungle ratio is 0");
        for(int i : Parameters){
            if(i < 0) throw new IllegalArgumentException("Invalid data! Parameters are negative. Change it to positive");
        }

        worldWidth = Parameters[0];
        worldHeight = Parameters[1];
        worldLower = new Vector2d(0,0);
        worldHigher = new Vector2d(worldWidth-1,worldHeight-1);

        int jungleRatio = Parameters[2];
        jungleWidth = Math.round(worldWidth / jungleRatio);
        jungleHeight = Math.round(worldHeight / jungleRatio);

        jungleLower  = new Vector2d(Math.round((worldWidth - jungleWidth) >> 1),Math.round((worldHeight - jungleHeight) >> 1));
        jungleHigher = new Vector2d(Math.round(jungleLower.x+jungleWidth-1),Math.round(jungleLower.y+jungleHeight-1));

        animalsCount = Parameters[3];
        startEnergy = Parameters[4];
        moveEnergy = Parameters[5];
        plantEnergy = Parameters[6];
        mapCount = Parameters[7];
    }

    public int getMapWidth() { return this.worldWidth; }
    public int getMapHeight(){ return this.worldHeight; }
    public Vector2d getMapLower() { return this.worldLower; }
    public Vector2d getMapHigher() { return this.worldHigher; }

    public int getJungleWidth(){ return this.jungleWidth; }
    public int getJungleHeight() { return this.jungleHeight; }
    public Vector2d getJungleLower(){ return this.jungleLower; }
    public Vector2d getJungleHigher(){ return this.jungleHigher; }

    public int getStartEnergy() { return startEnergy; }
    public int getAnimalsAmount() { return animalsCount; }
    public int getPlantEnergy() { return plantEnergy; }
    public int getMoveEnergy() { return moveEnergy; }
    public int getAmountOfMaps() { return mapCount; }
}
