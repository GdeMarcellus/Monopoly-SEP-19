package backend.Tiles;

public abstract class Tile {
    protected String name;

    //utility methods
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
