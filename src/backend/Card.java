package backend;

import backend.Player.Player;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileFreeParking;
import backend.Tiles.TileProperty;

import java.util.*;

public class Card {

    String description;
    Map<Integer, Properties> effects;

    /**
     * createse a card with these two value set
     * @param description the string description of a card
     * @param effects the map of effect of the card
     */
    public Card(String description, Map<Integer, Properties> effects) {
        this.description = description;
        this.effects = effects;
    }

    public int playCard(Player player, Board board){
        int outstandingAmount = 0;
        Integer currentID = null;
        Properties current = null;
        for(Map.Entry<Integer,Properties> pair : effects.entrySet())
        {
            currentID = pair.getKey();
            current = pair.getValue();
        }
            int amount = (int) current.getOrDefault("amount", 0 );
            int houseCost = (int) current.getOrDefault("houseCost", 0 );
            int hotelCost = (int) current.getOrDefault("hotelCost", 0 );
            String name = (String) current.getOrDefault("location","NA");
            int noSpaces = (int) current.getOrDefault("noSpaces", 0 );
            
            switch (currentID)
            {
                case 0:
                    bankPayPlayer(player, amount);
                    break;
                case 1:
                    outstandingAmount = payBank(player, amount);
                    break;
                case 2:
                    outstandingAmount = payRepairs(player, houseCost, hotelCost);
                    break;
                case 3:
                    outstandingAmount = payFine(player, (TileFreeParking) board.getTile(board.getFreeParkingPos()), amount);
                    break;
                case 4:
                    paidByPlayers(player, board.getPlayers(), amount);
                    break;
                case 5:
                    moveForwardTo(player, name, board.getTiles());
                    break;
                case 6:
                    moveBackwardTo(player, name, board.getTiles());
                    break;
                case 7:
                    moveSetSpaces(player, noSpaces);
                case 8:

            }
            return outstandingAmount;
        }


    /**
     * a method to play card by directly inputting the values
     * @param effectId the effects id
     * @param player the player using the card
     * @param amount the amount to be paid
     * @param houseCost the cost per houses to be paid
     * @param hotelCost the cost per hotel to be paid
     * @param noSpaces the number of space to move
     * @param name the name of the tile to move too
     * @param choiceIDs if the card is a choice card
     * @param board the board the game is on
     */
    public void playCard(int effectId, Player player, int amount, int houseCost, int hotelCost, int noSpaces, String name, int[] choiceIDs, Board board) {    }

    /**
     * player pays fine to bank
     * @param player player paying bank
     * @param amount amount being paid
     * @return amount outstanding
     */
    public int payBank(Player player, int amount) {
        return amount - player.removeMoney(amount);
    }

    /**
     * one player pays a fine to another
     * @param finedPlayer player paying money
     * @param payee player getting paid
     * @param amount amount to pay
     * @return amount outstanding
     */
    public int playerPayPlayer(Player finedPlayer, Player payee, int amount) {
        int money = finedPlayer.removeMoney(amount);
        payee.addMoney(money);
        return amount - money;
    }

    /**
     * bank gives player money
     * @param player player getting paid
     * @param amount amount to pay
     */
    public void bankPayPlayer(Player player, int amount) {
        player.addMoney(amount);
    }

    /**
     * player pays a cost on each house/hotel
     * @param player player being charged
     * @param houseCost amount charged per house
     * @param hotelCost amount charged per hotel
     * @return amount outstanding from fine
     */
    public int payRepairs(Player player, int houseCost, int hotelCost){
        int toPay = 0;
        ArrayList<TileProperty> playerProperties = player.getProperties();

        //go through each property, get development level
        for (TileProperty property : playerProperties) {
            if (property instanceof TileBuilding) {
                int development = ((TileBuilding) property).getDevelopment();

                //add hotel cost
                if (development == 5) {
                    toPay += hotelCost;
                }

                //add house cost * num houses
                else {
                    toPay += (development * houseCost);
                }
            }
        }

        //take amount owed from player
        return (toPay - player.removeMoney(toPay));
    }

    /**
     * player pays a fine to free parking
     * @param player player being fined
     * @param freeParking free parking object
     * @param amount amount being fined
     * @return amount outstanding
     */
    public int payFine(Player player, TileFreeParking freeParking, int amount){
        int outstanding = amount - player.removeMoney(amount);
        freeParking.payFine(amount - outstanding);
        return outstanding;
    }

    /**
     * a player is paid an amount by all players
     * @param player player getting paid
     * @param otherPlayers other players
     * @param amount amount being played by each player
     * @return array of outstanding amounts, index corrisponds to an index of a player
     */
    public ArrayList<Integer> paidByPlayers(Player player, ArrayList<Player> otherPlayers, int amount){
        int pot = 0;

        //initilise arraylist for outstanding payments
        ArrayList<Integer> outstanding = new ArrayList<>(Collections.nCopies(otherPlayers.size(), 0));

        //get money from each player, amount not paid added to outstanding
        for (int i = 0; i < otherPlayers.size(); i++) {
            if (otherPlayers.get(i) != player) {
                //add fined money to pot
                outstanding.set(i, otherPlayers.get(i).removeMoney(amount));
                pot += amount - outstanding.get(i);
            }
        }

        //pay to player
        player.addMoney(pot);

        return outstanding;
    }

    /**
     * player moves to given tile, can pass go
     * @param player player moving
     * @param tileName name of tile to be moved to
     * @param tiles arraylist of all tiles
     */
    public void moveForwardTo(Player player, String tileName, Tile[] tiles){
        int pos = 0;
        boolean passedGo;

        //find tile position
        for(int i = 0; i < tiles.length-1; i++) {
            if (tiles[i].getName().equals(tileName)) {
                pos = i;
            }
        }

        passedGo = player.setPosition(pos);
        if (passedGo) {
            player.addMoney(200);
        }
    }

    /**
     * player moves to given tile, cannot pass go
     * @param player player moving
     * @param tileName name of tile to be moved to
     * @param tiles arraylist of all tiles
     */
    public void moveBackwardTo(Player player, String tileName, Tile[] tiles){
        int pos = 0;
        //find tile position
        for(int i = 0; i < tiles.length-1; i++) {
            if (Objects.equals(tiles[i].getName(), tileName))
            {
                pos = i;
            }
        }

        player.jump(pos);
    }

    /**
     * player advances a set number of spaces, can pass go
     * @param player player moving
     * @param noSpaces number of spaces to advance
     */
    public void moveSetSpaces(Player player, int noSpaces) {
        ArrayList<Integer> spaces = new ArrayList<>();
        spaces.add(noSpaces);
        player.move(spaces);
    }

    public String getDescription()
    {
        return  description;
    }
}
