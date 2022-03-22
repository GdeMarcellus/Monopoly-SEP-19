package backend;

import java.util.ArrayList;

public class TileProperty extends Tile {

    private int price;
    private String name;
    private Player owner;
    private ArrayList<Integer> neighborhood;



    //get & set methods

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addToNeighborhood(int property) {
        this.neighborhood.add(property);
    }

    public void setNeighborhood(ArrayList<Integer> neighborhood) {
        this.neighborhood = neighborhood;
    }

    public ArrayList<Integer> getNeighborhood() {
        return neighborhood;
    }
}
