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

    public void addMoney(int money) {
        balance += money;
    }

    public int removeMoney(int money){
        int returnvalue = 0;
        if (balance >= money){
            returnvalue = money;
            balance -= money;
        }
        else {
            returnvalue = balance;
            balance = 0;
        }
        return returnvalue;
    }

    public int getMoney() {
        return balance;
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

    public Boolean removeProperty(Property property){
       if (properties.contains(property)){
           properties.remove(property);
           return true;
        }
       else{
           return false;
       }
    }

    public ArrayList<Property> getProperties(){
        return new ArrayList<Property>();
    }

    public boolean hasProperty(Property property){
        return properties.contains(property);
    }

}
