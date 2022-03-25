package backend;

import java.util.ArrayList;

public class TileProperty extends Tile {

    private int price;
    private String name;
    private Player owner;
    private ArrayList<TileProperty> neighborhood;
    private boolean mortgaged;

    public TileProperty(int price, String name, Player owner, ArrayList<TileProperty> neighborhood, boolean mortgaged) {
        this.price = price;
        this.name = name;
        this.owner = owner;
        this.neighborhood = neighborhood;
        this.mortgaged = mortgaged;
    }

    /**
     * @param player player which need to pay the rent (AKA Incoming player)
     * @param diceRoll the dice roll made by the player (as a total)
     * @return amount to be still paid by the incoming player
     * @throws IsMortgagedException exception thrown when the property is mortgaged
     */
    public int payRent(Player player, int diceRoll) throws IsMortgagedException {
       return 0;
    }

    /**
     * @return True is owner if this property owner owns the entire neighborhood
     */
    public boolean ownsNeighborhood(){
        boolean returnValue = true;
        for (TileProperty each: neighborhood) {
            if (!owner.getProperties().contains(each)) {
                returnValue = false;
                break;
            }
        }
        return returnValue;
    }

    /**
     * @param bank the in-game bank
     * @return amount paid by mortgaging the property
     * @throws IsMortgagedException exception thrown when the property is already mortgaged
     * @throws PropertyDevelopedException exception thrown when the property has houses or hotels
     */
    public int mortgaged(Player bank) throws IsMortgagedException, PropertyDevelopedException {
        if (!mortgaged){
            int amountPaid = 0;
            mortgaged = true;
            amountPaid = price/2;
            getOwner().addMoney(amountPaid);
            return amountPaid;
        }
        else {
            throw new IsMortgagedException();
        }
    }

    /**
     * buys back the property if it was mortgaged and the owner has the funds
     * @throws InsufficientFundsException thrown if the player cannot afford the buy back
     * @throws IsMortgagedException thrown if the property is not currently mortgaged
     */
    public void mortgagedBuyBack() throws InsufficientFundsException, IsMortgagedException {
        int amountOutstanding = (int) ((price/2) * 1.1);
        if (!mortgaged){
            throw new IsMortgagedException();
        }
        if (owner.getBalance() < amountOutstanding){
            throw new InsufficientFundsException();
        }
        owner.removeMoney(amountOutstanding);
        mortgaged = false;
    }

    //get & set methods

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addToNeighborhood(TileProperty property) {
        this.neighborhood.add(property);
    }

    public void setNeighborhood(ArrayList<TileProperty> neighborhood) {
        this.neighborhood = neighborhood;
    }

    public ArrayList<TileProperty> getNeighborhood() {
        return neighborhood;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
    }
}
