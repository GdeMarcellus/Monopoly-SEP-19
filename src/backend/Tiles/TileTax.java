package backend.Tiles;

import backend.Exception.InsufficientFundsException;
import backend.Player.Player;

public class TileTax extends Tile{

    int tax;

    /**
     * @param tax
     */
    public TileTax(int tax) {
        this.tax = tax;
    }

    /**
     * @param player
     * @param tileFreeParking
     * @throws InsufficientFundsException
     */
    public void payTax(Player player, TileFreeParking tileFreeParking) throws InsufficientFundsException {
        if (player.getMoney() >= tax){
            player.removeMoney(tax);
            tileFreeParking.payFine(tax);
        }
        else {
            throw new InsufficientFundsException();
        }
    }
}
