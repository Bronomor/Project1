package Components;

public class Grass {

    private Vector2d Grassposition;

    public Grass(Vector2d vector2d){
        this.Grassposition = vector2d;
    }

    public Vector2d getPosition() {
        return Grassposition;
    }

    @Override
    public String toString() {
        return "*";
    }
}
