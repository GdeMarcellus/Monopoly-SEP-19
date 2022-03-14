package backend;

import java.util.ArrayList;

public abstract class Player {

    private int position;
    private int balance;
    private int numDoubles;
    private ArrayList<Property> properties;

    public Player() {
        this.position = 0;
        this.balance = 0;
        this.numDoubles = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        //check position not beyond board limits
        if (this.position + position > 40) {
            this.position = position % 40;
        }
        else {
            this.position = position;
        }
    }

    public void toJail() {
        this.position = 41;
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

    public boolean addMoney(int money) {
        return true;
    }

    public int removeMoney(int money){
        return 0;
    }

    public int getMoney() {
        return 0;
    }

    public  Boolean addProperty(Property property) {
        return true;
    }

    public Boolean removeProperty(Property property){
        return true;
    }

    public ArrayList<Property> getProperties(){
        return new ArrayList<Property>();
    }

    public boolean hasProperty(Property property){
        return true;
    }

}
