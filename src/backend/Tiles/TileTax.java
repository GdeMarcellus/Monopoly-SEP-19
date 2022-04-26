package backend.Tiles;

import backend.Exception.InsufficientFundsException;
import backend.Player.Player;

public class TileTax extends Tile{

    int tax;

    /**
     * @param tax
     */
    public TileTax(int tax, String name) {
        this.tax = tax;
        this.name = name;
    }

    /**
     * Fines player
     * @param player Player being fined
     * @param tileFreeParking
     * @return amount outstanding
     */
    public int payTax(Player player, TileFreeParking tileFreeParking){
        //fine player
        int paid = player.removeMoney(tax);

        //place fine on free parking
        tileFreeParking.payFine(paid);

        return (tax - paid);
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getTax() {
        return tax;
    }
}
