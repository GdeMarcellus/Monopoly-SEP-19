package backend.Tiles;

import backend.Exception.IsMortgagedException;
import backend.Exception.PropertyDevelopedException;
import backend.Player.Player;

import java.util.ArrayList;

public class TileUtility extends TileProperty {

    public TileUtility(int price, String name, Player owner, ArrayList<TileProperty> neighborhood, boolean mortgaged) {
        super(price, name, owner, neighborhood, mortgaged);
    }

    /**
     * called when  player need to pay rent when landing on a property
     * @param player  player which need to pay the rent (AKA Incoming player)
     * @param diceRoll the dice roll made by the player (as a total)
     * @return amount still to be paid by the player
     * @throws IsMortgagedException thrown if the property has a mortgage on it
     */
    @Override
    public int payRent(Player player, int diceRoll) throws IsMortgagedException {
        if (!isMortgaged()) {
            int amountOutstanding = 0;
            int rentValue = 0;
            if (ownsNeighborhood()){
                rentValue = 10 * diceRoll;
            }
            else {
                rentValue = 4 * diceRoll;
            }
            int playerPayed = player.removeMoney(rentValue);
            if (playerPayed < rentValue) {
                amountOutstanding = rentValue - playerPayed;
            }
            getOwner().addMoney(rentValue - amountOutstanding);
            return amountOutstanding;
        }
        else {
            throw new IsMortgagedException();
        }
    }

    @Override
    public int mortgaged(Player bank) throws IsMortgagedException, PropertyDevelopedException {
        return super.mortgaged(bank);
    }
}
