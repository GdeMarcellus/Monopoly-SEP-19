package backend;

import java.util.ArrayList;

public class Board {

    private Tile[] tiles;
    private ArrayList<Player> players;
    private int maxDoubles = 3;

    /**
     * Board constructor
     * initiate the board with 41 tiles and empty player arraylist
     */
    public Board() {
        this.tiles = new Tile[41] ;
        for (Tile current: tiles) {
            current = new Tile();
        }
        this.players = new ArrayList<>();
    }

    /***
     * takes the turn for the player
     * moves the player by the total of the dices
     * check if the player has a double
     * check if player goes to jail
     * @param playerIndex index of current player
     * @param dices arraylist of dice values
     * @return boolean true if player rolled double and not in jail, false otherwise
     */
    public boolean turn(int playerIndex, ArrayList<Integer> dices) {
        //update player position
        players.get(playerIndex).setPosition(players.get(playerIndex).getPosition()
                + dices.get(0) + dices.get(1));

        //roll double check
        boolean rolledDouble;
        rolledDouble = check_double(dices);

        if (rolledDouble){
            players.get(playerIndex).setNumDoubles(players.get(playerIndex).getNumDoubles() + 1);

            //jail check
            if (players.get(playerIndex).getNumDoubles() == 3) {
                //send to jail (tile 41)
                players.get(playerIndex).toJail();
                rolledDouble = false;
            }
        }
        else {
            players.get(playerIndex).setNumDoubles(0);
        }

        return rolledDouble;
    }

    /**
     * check if the dice are a double
     * @param dices the size 2 array containing the dice
     * @return boolean true if both dice are the same, unless player sent to jail
     */
    public boolean check_double(ArrayList<Integer> dices){
        boolean returnValue = dices.get(0) == dices.get(1);
        return returnValue;
    }

    /**
     * Returns amount remaining if player purchased property
     * @param playerIndex index of player making the purchase
     * @param tileIndex index of tile being purchased
     */
    public int remainingBalance(int playerIndex, int tileIndex) {
        return players.get(playerIndex).getBalance() -
                ((Property) tiles[tileIndex]).getPrice();
    }

    /**
     * facilitates the purchase ofa given property by a given player
     * @param playerIndex index of player making the purchase
     * @param tileIndex index of tile being purchased
     * @return money outstanding after purchase (player couldn't afford purchase)
     */

    public int purchase(int playerIndex, int tileIndex) {
        //For readability
        Property property = (Property)tiles[tileIndex];
        Player player = players.get(playerIndex);

        //take money owed from player
        int outstanding = property.getPrice() - players.get(playerIndex).removeMoney(property.getPrice());

        //Pay available funds to property owner

        property.getOwner().addMoney(property.getPrice() - outstanding);

        //property ownership transferred to player
        property.getOwner().removeProperty(property);
        property.setOwner(player);
        player.addProperty(property);

        return outstanding;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public void addPlayer(Player newPlayer) {
        this.players.add(newPlayer);
    }

    public int getMaxDoubles() {
        return maxDoubles;
    }

    public void setMaxDoubles(int maxDoubles) {
        this.maxDoubles = maxDoubles;
    }
}
