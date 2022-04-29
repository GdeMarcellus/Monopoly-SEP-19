package backend.Tiles;

import backend.Exception.IsInJail;
import backend.Exception.IsMortgagedException;
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
    public int payRent(Player player, int diceRoll) throws IsMortgagedException, IsInJail {
        if(getOwner().isInJail()){
            throw new IsInJail();
        }

        if (!isMortgaged()) {
            int amountOutstanding;
            if (ownsNeighborhood()){
                amountOutstanding = (10 * diceRoll) - player.removeMoney(10 * diceRoll);
                getOwner().addMoney((10 * diceRoll) - amountOutstanding);
            }
            else {
                amountOutstanding = (4 * diceRoll) - player.removeMoney(4 * diceRoll);
                getOwner().addMoney((4 * diceRoll) - amountOutstanding);
            }
            return amountOutstanding;
        }
        else {
            throw new IsMortgagedException();
        }
    }
}
