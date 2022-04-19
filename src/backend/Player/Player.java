package backend.Player;

import backend.Tiles.TileBuilding;
import backend.Tiles.TileProperty;

import java.util.ArrayList;

public abstract class Player {

    private int position;
    private int balance;
    private int numDoubles;
    private ArrayList<TileProperty> properties;


    public Player() {
        this.position = 0;
        this.balance = 0;
        this.numDoubles = 0;
        this.properties = new ArrayList<TileProperty>();
    }

    public int getPosition() {
        return position;
    }

    public boolean move(int diceRoll) {
        boolean passedGo = false;

        //check position not beyond board limits
        if (this.position + diceRoll > 40) {
            this.position = position % 40;
            passedGo = true;
        }
        else {
            this.position += diceRoll;
        }
        return passedGo;
    }

    public boolean setPosition(int position) {
        boolean passedGo = false;
        if (position < this.position){
            passedGo = true;
        }
            this.position = position;
        return passedGo;
    }

    /**
     * @param position
     */
    public void jump(int position){
        this.position = position;
    }

    /**
     *
     */
    public void toJail() {
        this.position = 41;
    }

    public void addMoney(int money) {
        balance += money;
    }

    public int removeMoney(int money){
        int returnvalue = 0;
        if (balance >= money){
            balance -= money;
        }
        else {
            returnvalue = balance;
            balance = 0;
        }
        return returnvalue;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getNumDoubles() {
        return numDoubles;
    }

    public void setNumDoubles(int numDoubles) {
        this.numDoubles = numDoubles;
    }

    public int getMoney() {
        return balance;
    }

    public void addProperty(TileProperty property) {
        properties.add(property);
    }

    public boolean removeProperty(TileProperty property){
       if (properties.contains(property)){
           properties.remove(property);
           return true;
        }
       else{
           return false;
       }
    }

    public ArrayList<TileProperty> getProperties(){
        return properties;
    }

    public boolean hasProperty(TileProperty property){
        return properties.contains(property);
    }

    /**
     * Calculates the total wealth of the player (cash + properties)
     * @return total wealth of player
     */
    public int getTotalWealth() {
        int wealth = balance;

        for (TileProperty property : properties) {
            //get property value
            if (property.isMortgaged()) {
                wealth += property.getPrice() / 2;
            }
            else {
                wealth += property.getPrice();
            }
            //get value of houses / hotels
            if (property instanceof TileBuilding) {
                wealth += (((TileBuilding) property).getDevelopment() * ((TileBuilding) property).getDevelopmentCost());
            }
        }

        return wealth;
    }

}
