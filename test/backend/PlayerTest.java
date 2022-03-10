package backend;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

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
}
