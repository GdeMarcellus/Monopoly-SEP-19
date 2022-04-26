package backend.Tiles;

import backend.Player.Player;

public class TileGo extends Tile{
    private int goMoney = 200;

    public TileGo() {
        this.name = "Go";
    }

    /**
     * Gives money to player for passing Go
     * @param player players who passed Go
     */
    public void passedGo(Player player) {
        player.addMoney(goMoney);
    }

    //Get/set methods
    public int getGoMoney() {
        return goMoney;
    }

    public void setGoMoney(int goMoney) {
        this.goMoney = goMoney;
    }
}
