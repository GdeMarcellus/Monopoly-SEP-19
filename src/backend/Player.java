package backend;

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

    public boolean setPosition(int position) {
        //check position not beyond board limits
        boolean passedGo = false;
        if (position < this.position){
            passedGo = true;
        }
        if (this.position + position > 40) {
            this.position = position % 40;
        }
        else {
            this.position = position;
        }
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
            returnvalue = money;
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

    public Boolean removeProperty(TileProperty property){
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

}
