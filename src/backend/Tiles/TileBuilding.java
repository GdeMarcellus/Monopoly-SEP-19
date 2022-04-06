package backend.Tiles;

import backend.Exception.*;
import backend.Player.Player;

import java.util.ArrayList;

public class TileBuilding extends TileProperty {

    private String hexColour;
    private ArrayList<Integer> rent;
    private int developmentCost;
    private int development;


    public TileBuilding(String hexColour, ArrayList<Integer> rent, int developmentCost, int development,
                        int price, String name, Player owner, ArrayList<TileProperty> neighborhood, boolean mortgaged) {
        super(price, name, owner,neighborhood,mortgaged);
        this.hexColour = hexColour;
        this.rent = rent;
        this.developmentCost = developmentCost;
        this.development = development;

    }

    /**
     * called when  player need to pay rent when landing on a property
     * @param player   player which need to pay the rent (AKA Incoming player)
     * @param diceRoll the dice roll made by the player (as a total)
     * @return amount to be paid by the incoming player
     * @throws IsMortgagedException thrown if the property has a mortgage on it
     */
    @Override
    public int payRent(Player player, int diceRoll) throws IsMortgagedException {
        if (!isMortgaged()) {
            int amountOutstanding;
            if (development == 0 && ownsNeighborhood()) {
                amountOutstanding = player.removeMoney(rent.get(0) * 2);
            } else {
                amountOutstanding = player.removeMoney(rent.get(development));
            }
            return amountOutstanding;
        }
        else {
            throw new IsMortgagedException();
        }
    }

    /**
     * @param bank the in-game bank
     * @return amount paid by mortgaging the property
     * @throws IsMortgagedException thrown if the property has a mortgage on it
     * @throws PropertyDevelopedException thrown if the property has houses or hotels
     */
    @Override
    public int mortgaged(Player bank) throws IsMortgagedException, PropertyDevelopedException {
        if (development == 0) {
            return super.mortgaged(bank);
        }
        else{
            throw new PropertyDevelopedException();
        }

    }

    /**
     * method used in order to buy a house on this property
     * @param bank the in-game bank
     * @return amount still to be paid
     * @throws PropertyDevelopedException exception thrown when the property has hotels
     * @throws LargeDevelopmentDifferenceException exception thrown when the neighborhood has too large development difference
     * @throws IsMortgagedException exception thrown when the property is already mortgaged
     * @throws InsufficientFundsException exception thrown when player lacks fund to upgrade development
     */
    public int buyHouse(Player bank) throws PropertyDevelopedException, LargeDevelopmentDifferenceException, IsMortgagedException, InsufficientFundsException {
        if (development == 5){
            throw new PropertyDevelopedException();
        }
        if (isMortgaged()){
            throw new IsMortgagedException();
        }
        int amountPaid;
        if (ownsNeighborhood()){
            if(developmentDifference() >= 1){
                throw new LargeDevelopmentDifferenceException();
            }
        }
        if (getOwner().getBalance() < developmentCost){
            int amountOustanding = developmentCost - getOwner().getBalance();
            throw new InsufficientFundsException();
        }
        development+= 1;
        amountPaid = getOwner().removeMoney(developmentCost);
        return amountPaid;
    }

    /**
     * decrease the development of a property and the owner gains money
     * @param bank the in-game bank
     * @throws LargeDevelopmentDifferenceException throw when the maximum development difference is too large
     * @throws NoDevelopmentException thrown when there is no house to sell
     */
    public void sellHouse(Player bank) throws LargeDevelopmentDifferenceException, NoDevelopmentException {
        if (development == 0){
            throw new NoDevelopmentException();
        }
        if (ownsNeighborhood()){
            if(developmentDifference() >= 1){
                throw new LargeDevelopmentDifferenceException();
            }
        }
        development-= 1;
        getOwner().addMoney(developmentCost);
    }

    /**
     * calculate the largest development difference in the neighborhood
     * @return Maximum dvelopment difference
     */
    private int developmentDifference(){
        int maxDiff = 0;
        for (TileProperty each: getNeighborhood()) {
            for (TileProperty other: getNeighborhood()) {
                int diff = ((TileBuilding) each).getDevelopment() - ((TileBuilding) other).getDevelopment();
                if (diff > maxDiff){
                    maxDiff = diff;
                }
            }
        }
        return maxDiff;
    }

    //set & get methods

    public void setHexColour(String hexColour) {
        this.hexColour = hexColour;
    }

    public String getHexColour() {
        return hexColour;
    }

    public ArrayList<Integer> getRent() {
        return rent;
    }

    public void setRent(ArrayList<Integer> rent) {
        this.rent = rent;
    }

    public int getDevelopmentCost() {
        return developmentCost;
    }

    public void setDevelopmentCost(int developmentCost) {
        this.developmentCost = developmentCost;
    }

    public int getDevelopment() {
        return development;
    }

    public void setDevelopment(int development) {
        this.development = development;
    }
}
