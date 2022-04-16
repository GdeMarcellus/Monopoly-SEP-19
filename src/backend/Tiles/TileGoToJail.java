package backend.Tiles;

import backend.Player.Player;

public class TileGoToJail extends Tile{

    /**
     * @param player
     */
    public void putInJail(Player player){
        player.toJail();
    }

}
