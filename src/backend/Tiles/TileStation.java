package backend.Tiles;

import backend.Exception.IsMortgagedException;
import backend.Exception.PropertyDevelopedException;
import backend.Player.Player;

import java.util.ArrayList;

public class TileStation extends TileProperty{

    private ArrayList<Integer> rent;

    public TileStation(ArrayList<Integer> rent,
                       int price, String name, Player owner, ArrayList<TileProperty> neighborhood, boolean mortgaged) {
        super(price, name, owner,neighborhood,mortgaged);
        this.rent = rent;
    }

    /**
     * called when  player need to pay rent when landing on a property
     * @param player   player which need to pay the rent (AKA Incoming player)
     * @param diceRoll the dice roll made by the player (as a total)
     * @return return the amount still to be paid by player
     * @throws IsMortgagedException exception thrown when the property is already mortgaged
     */
    @Override
    public int payRent(Player player, int diceRoll) throws IsMortgagedException {
        if (!isMortgaged()) {
            int amountOutstanding = 0;
            int ownedStation = 0;
            for (TileProperty each: getOwner().getProperties()) {
                if (each instanceof TileStation){
                    ownedStation+= 1;
                }
            }
            amountOutstanding = player.removeMoney(rent.get(ownedStation));
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
