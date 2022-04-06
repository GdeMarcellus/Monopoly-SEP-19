package backend;

import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.TileProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class    PlayerTest {

    Player player;

    @Before
    public void init(){
        player = new HumanPlayer();
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
        TileProperty property = new TileProperty();
        player.addProperty(property);
        assertTrue(player.getProperties().contains(property));
    }
    @Test
    public void TestGetRemoveProperty(){
        TileProperty property = new TileProperty();
        player.addProperty(property);
        player.removeProperty(property);
        assertFalse(player.getProperties().contains(property));

    }
    @Test
    public void hasProperty(){
        TileProperty property = new TileProperty();
        player.addProperty(property);
        assertTrue(player.hasProperty(property));
    }
    @Test
    public void hasProperty2(){
        TileProperty property = new TileProperty();
        assertFalse(player.hasProperty(property));
    }
}
