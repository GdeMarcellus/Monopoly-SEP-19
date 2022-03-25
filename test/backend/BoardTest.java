package backend;

import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.Tile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BoardTest {

    Board board;

    @Before
    public void init(){
        board = new Board();
    }

    /**
     * Test for the set and get method of the tiles variable in the board class
     */
    @Test
    public void testSetGetTiles() {
        Tile[] tiles = new Tile[30];
        board.setTiles(tiles);
        assertEquals(board.getTiles(), tiles);
    }
    /**
     * Test for the set and get method of the maxDoubles variable in the board class
     */
    @Test
    public void testSetGetMaxDoubles() {
        int maxDoubles = 10;
        board.setMaxDoubles(maxDoubles);
        assertEquals(board.getMaxDoubles(), maxDoubles);
    }
    /**
     * Test for the Get and Add method for player in the board class with a single player
     */
    @Test
    public void testGetAddPlayer() {
        Player player = new HumanPlayer();
        board.addPlayer(player);
        assertEquals(board.getPlayer(0), player);
    }
    /**
     * Test for the Get and Add method for player in the board class with multiple player
     */
    @Test
    public void testGetAddPlayer2() {
        Player player = new HumanPlayer();
        Player player2 = new HumanPlayer();
        board.addPlayer(player);
        board.addPlayer(player2);
        assertEquals(board.getPlayer(1), player2);
    }
}
