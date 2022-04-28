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

    /**
     * @return ruterns the player current position 
     */
    public int getPosition() {
        return position;
    }

    /**
     * moves the player by the total within the dice rolls
     * @param diceRoll dice rolls to move by
     * @return if go was passed
     */
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
     * jump to a position on the board and sskips go
     * @param position the positin ot jump to
     */
    public void jump(int position){
        this.position = position;
    }

    /**
     *send the player to jail and takes away a GOJ card if possible
     */
    public void toJail()
    {
        setTurnsInJail(2);
        this.position = 10;
    }

    /**
     * add an amout of money to the players balance
     * @param money amount to be added
     */
    public void addMoney(int money) {
        balance += money;
    }

    /**
     * remove an amout of money to the players balance
     * @param money amount to be removed
     */
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

    /**
     * get the player balance
     * @return the players balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * set the player balance
     * @param balance the value to set the balance to
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * get the number of doubles rolled by the player
     * @return the number of doubles rolled by the player
     */
    public int getNumDoubles() {
        return numDoubles;
    }

    /**
     * set the number of doubles rolled by the player
     * @param numDoubles the number of doubles rolled by the player
     */
    public void setNumDoubles(int numDoubles) {
        this.numDoubles = numDoubles;
    }

    /**
     * @return the player balance
     */
    public int getMoney() {
        return balance;
    }

    /**
     * add property to the player properties
     * @param property property to be added
     */
    public void addProperty(TileProperty property) {
        properties.add(property);
    }

    /**
     * removes a property from the player
     * @param property property to be removed
     * @return if the removal was succesfull
     */
    public boolean removeProperty(TileProperty property){
       if (properties.contains(property)){
           properties.remove(property);
           return true;
        }
       else{
           return false;
       }
    }

    /**
     * returns the player properties
     * @return the player properties
     */
    public ArrayList<TileProperty> getProperties(){
        return properties;
    }

    /**
     * check if the player has this property
     * @param property to be checked for
     * @return if the player has it or not
     */
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

    /**
     * adds a Get out of Jail Free card to the player
     */
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

    /**
     * get the number of Get out of Jail Free card  the player has
     * @return the number  of Get out of Jail Free card
     */
    public int getNoGOJF() {
        return noGOJF;
    }

    /**
     * set the number of turn the player cna spend in jail
     * @param turns number of turns
     */
    public void setTurnsInJail(int turns) {
        turnsInJail = turns;
    }

    /**
     * get the number of turn the player cna spend in jail
     * @return number of turns
     */
    public int getTurnsInJail() {
        return turnsInJail;
    }

    /**
     * spends a turn iin jail for the player if all turns are spent outputs a true
     * @return if the counter it 0
     */
    public boolean jailNewTurn(){
        turnsInJail -= 1;
        if (turnsInJail == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @return true if the player is in jail
     */
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
