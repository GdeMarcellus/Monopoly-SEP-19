package backend.Player;

import backend.Board;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileJail;
import backend.Tiles.TileProperty;

import java.util.ArrayList;

public abstract class Player {

    protected int position;
    protected int balance;
    protected int numDoubles;
    protected ArrayList<TileProperty> properties;

    protected int noGOJF;

    protected int turnsInJail;


    public Player() {
        this.position = 0;
        this.balance = 0;
        this.numDoubles = 0;
        this.properties = new ArrayList<TileProperty>();
    }

    public int getPosition() {
        return position;
    }

    public boolean move(ArrayList<Integer> diceRoll) {
        boolean passedGo = false;

        int diceValue = 0;
        for (int i = 0; i < diceRoll.size(); i++) {
            diceValue += diceRoll.get(i);
        }

        //check position not beyond board limits
        if (this.position + diceValue >= 40) {
            this.position = (position + diceValue) % 40;
            passedGo = true;
        }
        else {
            position += diceValue;
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
    public void toJail(){
        if (getNoGOJF() > 0){
            if (removeGOJFCard()){
                return;
            };
        }
        setTurnsInJail(2);
        this.position = 10;

    }

    public void addMoney(int money) {
        balance += money;
    }

    public int removeMoney(int money){
        int returnvalue = 0;
        if (balance >= money){
            balance -= money;
            returnvalue = money;
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

    public void addGOJFCard() {
        noGOJF += 1;
    }

    /**
     * -1 to noGOJF
     * @return false if no cards to remove, true if card removed
     */
    public boolean removeGOJFCard() {
        if (noGOJF == 0 ) {
            return false;
        }
        else {
            noGOJF -= 1;
            return true;
        }
    }

    public int getNoGOJF() {
        return noGOJF;
    }

    public void setTurnsInJail(int turns) {
        turnsInJail = turns;
    }

    public int getTurnsInJail() {
        return turnsInJail;
    }

    public boolean jailNewTurn(){
        turnsInJail -= 1;
        if (turnsInJail == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isInJail(){
        return turnsInJail > 0;
    }

    /**
     * reset the player turn in jail to 0 and return the location of
     * either the first just visting tile or the first tile of the board
     * @param board the board of the game
     * @return
     */
    public int getOuOfJail(Board board){
        setTurnsInJail(0);
        Tile[] tiles = board.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            Tile tile = tiles[i];
            if (tile instanceof TileJail){

                return i;
            }
        }
        return 0;
    }

}
