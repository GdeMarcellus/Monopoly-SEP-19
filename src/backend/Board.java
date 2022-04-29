package backend;

import backend.Exception.*;
import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileFreeParking;
import backend.Tiles.TileProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

public class Board {

    private Tile[] tiles;
    private ArrayList<Player> players;
    private Queue<Card> opportunityCard;
    private Queue<Card> potLuckCard;
    private Player bank;
    private int maxDoubles = 3;
    private final ArrayList<Integer> auctionBids = new ArrayList<>();
    private int freeParkingPos = 20;

    /**
     * Board constructor
     * initiate the board with 41 tiles and empty player arraylist
     */
    public Board() {
        this.tiles = new Tile[41] ;
        this.players = new ArrayList<>();
        //TODO bank player class?
        this.bank = new HumanPlayer();
        bank.setBalance(999999999);

    }

    /**
     * check if the dice are a double
     * @param dices the size 2 array containing the dice
     * @return boolean true if both dice are the same, unless player sent to jail
     */
    public boolean checkDouble(ArrayList<Integer> dices){
        return dices.get(0) == dices.get(1);
    }

    /**
     * Facilitates a player purchasing a property
     * @param playerIndex index of player
     * @param tileIndex index of property
     * @param priceOverride optional override of property's price
     * @return amount spent by player in transaction
     * @throws NonPropertyTileException if tile is not player-ownable
     * @throws InsufficientFundsException if player lacks funds for transaction
     */
    public int playerPurchase(int playerIndex, int tileIndex, int priceOverride) throws NonPropertyTileException, InsufficientFundsException {
        //Check tile is ownable by player
        if (!(tiles[tileIndex] instanceof TileProperty property)) {
            throw new NonPropertyTileException();
        }

        Player player = players.get(playerIndex);
        int moneyOwed;

        //get price for property
        if (priceOverride != 0){
            moneyOwed = priceOverride;
        }
        else {
            moneyOwed = property.getPrice();
        }

        //check can afford
        if (player.getMoney() < moneyOwed) {
            throw new InsufficientFundsException();
        }

        //complete transaction
        purchase(player, property.getOwner(), property, moneyOwed);

        return moneyOwed;
    }

    /**
     * Facilitates player selling a property to the bank
     * @param playerIndex players index
     * @param tileIndex property index
     * @return amount player recived
     * @throws OwnershipException player does not own property
     * @throws PropertyDevelopedException property contains houses/hotel
     */
    public int sellToBank(int playerIndex, int tileIndex) throws OwnershipException, PropertyDevelopedException {
        //For readability
        TileProperty property = (TileProperty) tiles[tileIndex];
        Player player = players.get(playerIndex);
        int moneyOwed;

        //check owned by player
        if (property.getOwner() != player) {
            throw new OwnershipException();
        }

        if (property instanceof TileBuilding) {
            //check not developed
            if (((TileBuilding) property).getDevelopment() != 0) {
                throw new PropertyDevelopedException();
            }
        }

        //check mortgage
        if (property.isMortgaged()){
            moneyOwed = property.getPrice() / 2;
        }
        else {
            moneyOwed = property.getPrice();
        }

        //complete transaction
        purchase(bank, player, property, moneyOwed);

        return moneyOwed;
    }

    /**
     * facilitates purchase of a property between two players
     * @param buyer player buying
     * @param owner player selling
     * @param property property involved in transaction
     * @param price price of property
     * @return any outstanding amount
     */
    public int purchase(Player buyer,Player owner, TileProperty property, int price){

            //take money owed from buyer
            int money = buyer.removeMoney(price);

            //Pay available funds to property owner
            owner.addMoney(money);

            //property ownership transferred to buyer
            owner.removeProperty(property);
            property.setOwner(buyer);
            buyer.addProperty(property);

            return price - money;
    }

    /**
     * Calls the payRent method of a tile
     * @param playerIndex index of player paying rent
     * @param tileIndex index of property player landed on
     * @param dice dice values
     * @return Payment outstanding, 0 if rent fully paid
     * @throws NonPropertyTileException if tile not a player own-able tile
     * @throws IsMortgagedException if tile mortgaged
     * @throws IsInJailException if payee is in jail
     */

    public int payRent(int playerIndex, int tileIndex, ArrayList<Integer> dice) throws NonPropertyTileException, IsMortgagedException, IsInJailException {
        //Check tile is ownable by player
        if (!(tiles[tileIndex] instanceof TileProperty))
        {
            throw new NonPropertyTileException();
        }
        else
        {
            return ((TileProperty) tiles[tileIndex]).payRent(players.get(playerIndex), dice.get(0) + dice.get(1));
        }
    }

    /**
     * @param playerIndex index of player
     * @return Object of tile player is currently located on
     */
    public Tile getPlayerTile(int playerIndex) {
        return tiles[players.get(playerIndex).getPosition()];
    }

    //Auction methods

    /**
     * Populates auctionBids ArrayList
     */
    public void auctionInitialise() {
        for (int i = 0; i < players.size(); i++) {
            auctionBids.add(0);
        }
    }

    /**
     * Sets all bids to 0
     */
    public void auctionStart() {
        Collections.fill(auctionBids, 0);
    }

    /**
     * @return [the highest bid, number of bids at highest bid, index of winning player]
     */
    public int[] auctionHighestBid() {
        int highest = -1, numHigh = 0, index = 0;
        for (int i = 0; i < auctionBids.size(); i++) {
            if (auctionBids.get(i) > highest) {
                highest = auctionBids.get(i);
                numHigh = 1;
                index = i;
            }
            else if (auctionBids.get(i) == highest) {
                numHigh += 1;
            }
        }
        return new int[]{highest, numHigh, index};
    }

    /**
     * Enables a player to make a bid in auction
     * @param playerIndex index of player making bid
     * @param amount bid amount
     * @return true if bid make, false if bid less than current bid
     * @throws InsufficientFundsException if player bid greater than player funds
     */
    public boolean auctionMakeBid(int playerIndex, int amount) throws InsufficientFundsException {
        if (amount > players.get(playerIndex).getMoney()) {
            throw new InsufficientFundsException();
        }
        else if (auctionBids.get(playerIndex) >= amount) {
            return false;
        }
        else {
            auctionBids.set(playerIndex, amount);
            return true;
        }
    }

    /**
     * @param playerIndex index of player
     * @return players current bid
     */
    public int auctionGetBid(int playerIndex) {
        return auctionBids.get(playerIndex);
    }

    //set/get methods

    public Player getBank(){
        return bank;
    }

    public Tile getTile(int tileIndex) {
        return tiles[tileIndex];
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }

    public int getTileIndex(Tile searchTile) {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].equals(searchTile)) {
                return i;
            }
        }
        return -1;
    }

    public int getFreeParkingPos() {
        return freeParkingPos;
    }

    public void setFreeParkingPos(int freeParkingPos) {
        this.freeParkingPos = freeParkingPos;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public int getPlayerIndex(Player player) {
        return players.indexOf(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player newPlayer) {
        this.players.add(newPlayer);
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    public int getMaxDoubles() {
        return maxDoubles;
    }

    public void setMaxDoubles(int maxDoubles) {
        this.maxDoubles = maxDoubles;
    }

    public Queue<Card> getOpportunityCard() {
        return opportunityCard;
    }

    public void setOpportunityCard(Queue<Card> opportunityCard) {
        this.opportunityCard = opportunityCard;
    }

    public Queue<Card> getPotLuckCard() {
        return potLuckCard;
    }

    public void setPotLuckCard(Queue<Card> potLuckCard) {
        this.potLuckCard = potLuckCard;
    }
}
