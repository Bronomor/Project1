package Elements;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Vector2d {
    public final int x;
    public final int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public boolean precedes(Vector2d other) { return (this.x <= other.x && this.y <= other.y); }

    public boolean follows(Vector2d other) { return (this.x >= other.x && this.y >= other.y); }

    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(max(this.x,other.x), max(this.y,other.y));
    }

    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(min(this.x,other.x), min(this.y,other.y));
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    public Vector2d product(Vector2d other){
        return new Vector2d(this.x*other.x, this.y * other.y);
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Vector2d)) {
            return false;
        } else {
            Vector2d that = (Vector2d)other;
            return this.x == that.x && this.y == that.y;
        }
    }

    public Vector2d opposite() {
        return new Vector2d(Integer.signum(this.x), Integer.signum(this.y));
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash += this.x * 31;
        hash += this.y * 17;
        return hash;
    }
}
