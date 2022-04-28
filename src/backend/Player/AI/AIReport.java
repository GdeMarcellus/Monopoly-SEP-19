package backend.Player.AI;

import backend.Board;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AIReport {
    Board board;
    private Queue<AIEvent> events;
    private boolean bankrupt;
    private ArrayList<TileBuilding> improvedPropertys;
    private int improvedPropertysCounter;
    private TileProperty purchasedProperty;
    private TileProperty rentProperty;
    private Tile landedTile;
    private ArrayList<Integer> diceRoll;
    private boolean rolledDouble;
    private int rentPaid;
    private int freeParkingMoney;
    private int taxMoney;
    private ArrayList<TileProperty> soldPropertys;
    private int soldPropertysCounter;
    private ArrayList<TileBuilding> soldHouses;
    private int soldHousesCounter;

    public AIReport(Board board)
    {
        events = new LinkedList<>();
        this.board = board;
    }

    public void newTurn() {
        bankrupt = false;
        purchasedProperty = null;
        rentProperty = null;
        landedTile = null;
        diceRoll = null;
        rolledDouble = false;
        improvedPropertys = new ArrayList<>();
        rentPaid = -1;
        freeParkingMoney = -1;
        taxMoney = -1;
        soldPropertys = new ArrayList<>();
        soldHouses = new ArrayList<>();
        improvedPropertysCounter = 0;
        soldPropertysCounter = 0;
        soldHousesCounter = 0;
    }

    //get/set methods

    /**
     * adds a new event in an AI players turn
     * @param eventType type of event
     */
    public void addEvent(Event eventType) {
        AIEvent event = new AIEvent();
        event.setEvent(eventType);
        event.setDescription(generateDescription(eventType));
        events.add(event);
    }

    /**
     * generates a discription for an event in an AI players turn
     * @param eventType type of event
     * @return String containg the description
     */
    private String generateDescription(Event eventType) {
        String description = null;
        switch (eventType) {
            case DiceRoll -> description = "Rolled a " + diceRoll.get(0) + " and a " + diceRoll.get(1);
            case DiceRollDouble -> description = "Rolled a " + diceRoll.get(0) + " and a " + diceRoll.get(1) + ", a double!";
            case Move -> description = "Moved to: " + landedTile.getName();
            case PaidTax -> description = "Paid £" + taxMoney + "in tax";
            case PaidRent -> description = "Paid £" + rentPaid + "in rent to player " + ((board.getPlayers().indexOf(rentProperty.getOwner())) + 1);
            case GoneToJail -> description = "Sent to jail :(";
            case FreeParking -> description = "Collected £" + freeParkingMoney + " from free parking";
            case OwnProperty -> description = "Relaxing at my own property";
            case PropertyPurchase -> description = "Purchased " + purchasedProperty.getName() + " for £" + purchasedProperty.getPrice();
            case HousePurchase -> {
                description = "Developed the property" + improvedPropertys.get(improvedPropertysCounter).getName();
                improvedPropertysCounter++;
            }
            case PropertySell -> {
                description = "Sold " + soldPropertys.get(soldPropertysCounter).getName() + "back to the bank";
                soldPropertysCounter++;
            }
            case HouseSell -> {
                description = "Sold a house on " + soldHouses.get(soldHousesCounter).getName() + "back to the bank";
                soldHousesCounter++;
            }
            case Bankrupt -> description = "I am bankrupt :(";
        }
        return description;
    }

    public AIEvent getNextEvent() {
        if (events.isEmpty()) {
            return null;
        }
        return events.poll();
    }

    public void setRentProperty(TileProperty rentProperty) {
        this.rentProperty = rentProperty;
    }

    public TileProperty getRentProperty() {
        return rentProperty;
    }

    public void setLandedTile(Tile landedTile) {
        this.landedTile = landedTile;
    }

    public Tile getLandedTile() {
        return landedTile;
    }

    public void setDiceRoll(ArrayList<Integer> diceRoll) {
        this.diceRoll = diceRoll;
    }

    public ArrayList<Integer> getDiceRoll() {
        return diceRoll;
    }

    public boolean getRolledDouble() {
        return rolledDouble;
    }

    public void setBankrupt(boolean bankrupt) {
        this.bankrupt = bankrupt;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    public void setPurchasedProperty(TileProperty purchasedProperty) {
        this.purchasedProperty = purchasedProperty;
    }

    public TileProperty getPurchasedProperty() {
        return purchasedProperty;
    }

    public void addImprovedProperty(TileBuilding improvedProperty) {
        this.improvedPropertys.add(improvedProperty);
    }

    public ArrayList<TileBuilding> getImprovedPropertys() {
        return improvedPropertys;
    }

    public void setRentPaid(int rentPaid) {
        this.rentPaid = rentPaid;
    }

    public int getRentPaid() {
        return rentPaid;
    }

    public void setFreeParkingMoney(int freeParkingMoney) {
        this.freeParkingMoney = freeParkingMoney;
    }

    public int getFreeParkingMoney() {
        return freeParkingMoney;
    }

    public void setTaxMoney(int taxMoney) {
        this.taxMoney = taxMoney;
    }

    public int getTaxMoney() {
        return taxMoney;
    }

    public void addSoldProperty(TileProperty property) {
        soldPropertys.add(property);
    }

    public TileProperty getSoldProperty() {
        if (soldPropertys.size() > 0) {
            return soldPropertys.remove(0);
        }
        else {
            return null;
        }
    }

    public void addSoldHouse(TileBuilding house) {
        soldHouses.add(house);
    }

    public TileBuilding getSoldHouse() {
        if (soldHouses.size() > 0) {
            return soldHouses.remove(0);
        }
        else {
            return null;
        }
    }
}
