package backend.Tiles;

import backend.Player.Player;

public class TileFreeParking extends Tile{
    private int freeParkingFines;

    public TileFreeParking() {
        freeParkingFines = 0;
        this.name = "Free Parking";
    }

    /**
     * Pays all money held on free parking to a given player
     * @param player player money being paid to
     * @return amount paid to player
     */
    public int payToPlayer(Player player) {
        int amount = freeParkingFines;
        player.addMoney(freeParkingFines);
        freeParkingFines = 0;
        return amount;
    }

    //set/get methods

    public void payFine(int fine) {
        this.freeParkingFines += fine;
    }

    public int getFreeParkingFines() {
        return freeParkingFines;
    }
}
