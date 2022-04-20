package backend;

import backend.Player.Player;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileFreeParking;
import backend.Tiles.TileProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class Card {

    String description;
    Map<Integer, Properties> effects;

    public Card(String description, Map<Integer, Properties> effects) {
        this.description = description;
        this.effects = effects;
    }

    public void playCard(Player player, Board board){
        for (int i = 0; i < effects.size(); i++) {
            Properties current = effects.get(i);

            int effectId = (int) current.get("effectId");
            int amount = (int) current.getOrDefault("Amount", 0 );
            int houseCost = (int) current.getOrDefault("houseCost", 0 );
            int hotelCost = (int) current.getOrDefault("hotelCost", 0 );
            String name = (String) current.getOrDefault("name","NA");
            int noSpaces = (int) current.getOrDefault("noSpaces", 0 );
            
            switch (effectId)
            {
                case 0:
                    bankPayPlayer(player, amount);
                    break;
                case 1:
                    payBank(player, amount);
                    break;
                case 2:
                    payRepairs(player, houseCost, hotelCost);
                    break;
                case 3:
                    payFine(player, (TileFreeParking) board.getTile(board.getFreeParkingPos()), amount);
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

        }
    }


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
    //TODO need validation somewhere to check that name is actual tile
    public void moveForwardTo(Player player, String tileName, Tile[] tiles){
        int pos = 0;
        boolean passedGo;

        //find tile position
        for(int i = 0; i < tiles.length; i++) {
            if (tiles[i].getName().equals(tileName)) {
                pos = i;
            }
        }

        passedGo = player.setPosition(pos);
        //TODO return value to indicate? let GUI handle giving money?
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
        for(int i = 0; i < tiles.length; i++) {
            if (tiles[i].getName().equals(tileName)) {
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
}
