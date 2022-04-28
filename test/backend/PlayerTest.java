package backend;

import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileJail;
import backend.Tiles.TileProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class    PlayerTest {

    Player player;
    TileProperty property;

    @Before
    public void init(){
        player = new HumanPlayer();
        String hexColour = "#ffffff";
        ArrayList<Integer> rent = new ArrayList<Integer>();
        rent.add(10);
        rent.add(20);
        rent.add(30);
        rent.add(40);
        rent.add(50);
        int developmentCost = 20;
        String name = "1";
        ArrayList<TileProperty> neighborhood = new ArrayList<TileProperty>();
        property = new TileBuilding(hexColour,rent, developmentCost,0, 10 ,name, null, neighborhood, false);
    }

    /**
     * Test for the set and get method of the position variable in the player class
     */
    @Test
    public void testSetGetPosition() {
        int position = 23;
        player.setPosition(position);
        assertEquals(player.getPosition(),position);
    }
    /**
     * Test for the set and get method of the balance variable in the player class
     */
    @Test
    public void testSetGetBalance() {
        int balance = 1500;
        player.setBalance(balance);
        assertEquals(player.getBalance(),balance);
    }
    /**
     * Test for the set and get method of the numDoubles variable in the player class
     */
    @Test
    public void testSetGetNumDoubles() {
        int numDoubles = 1500;
        player.setNumDoubles(numDoubles);
        assertEquals(player.getNumDoubles(),numDoubles);
    }
    /**
     * Test for the toJail() method in the player class (expected to change its position to 41)
     */
    @Test
    public void testToJail() {
        player.toJail();
        assertEquals(player.getPosition(),41);
    }

    @Test
    public void testAddGetMoney(){
        player.setBalance(0);
        player.addMoney(100);
        assertEquals(player.getMoney(), 100);
    }
    @Test
    public void TestRemoveMoney(){
        player.setBalance(100);
        player.removeMoney(10);
        assertEquals(player.getMoney(), 90);

    }
    @Test
    public void TestRemoveMoney2(){
        player.setBalance(100);
        assertEquals(100,player.removeMoney(110));
    }

    @Test
    public void TestGetAddProperty() {
        player.addProperty(property);
        assertTrue(player.getProperties().contains(property));
    }
    @Test
    public void TestGetRemoveProperty(){
        player.addProperty(property);
        player.removeProperty(property);
        assertFalse(player.getProperties().contains(property));

    }
    @Test
    public void hasProperty(){
        player.addProperty(property);
        assertTrue(player.hasProperty(property));
    }
    @Test
    public void hasProperty2(){
        assertFalse(player.hasProperty(property));
    }

    @Test
    public void addGOJFTest(){
        player.addGOJFCard();
        assertEquals(1,player.getNoGOJF());
    }
    @Test
    public void RemoveGOJFest(){
        player.addGOJFCard();
        player.addGOJFCard();
        player.addGOJFCard();
        player.removeGOJFCard();
        assertEquals(2,player.getNoGOJF());
    }

    @Test
    public void getOutOfJailTestSetTurnInJailSet(){
        player.setTurnsInJail(10);
        Board board = new Board();
        player.getOuOfJail(board);
        assertEquals(0,player.getTurnsInJail());
    }
    @Test
    public void getOutOfJailTestSetPositionTo0(){
        Board board = new Board();
        player.getOuOfJail(board);
        assertEquals(0,player.getOuOfJail(board););
    }
    @Test
    public void getOutOfJailTestSetPositionToProperPosition(){
        Board board = new Board();
        Tile[] tiles = new Tile[41];
        tiles[11] = new TileJail();
        board.setTiles(tiles);

        assertEquals(11,player.getOuOfJail(board););
    }

}
