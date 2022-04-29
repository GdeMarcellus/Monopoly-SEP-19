package backend;

import backend.Exception.IsInJailException;
import backend.Exception.IsMortgagedException;
import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.TileProperty;
import backend.Tiles.TileStation;
import backend.Tiles.TileUtility;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TileUtilityTest {


    Player owner = new HumanPlayer();
    Player secondPlayer = new HumanPlayer();

    TileUtility utility1;
    TileUtility utility2;


    @Before
    public void setUp(){
        secondPlayer.setBalance(10000);
        owner.setBalance(10000);
        ArrayList<TileProperty> neighborhood = new ArrayList<TileProperty>();
        utility1 = new TileUtility(10,"utility1",owner,neighborhood,false);
        neighborhood.add(utility1);
        utility2 = new TileUtility(10,"utility2",owner,neighborhood,false);
        neighborhood.add(utility2);
        owner.addProperty(utility1);
    }

    /**
     * test to verify if the .payrent() method works correctly in normal condition with a dice roll of 0
     */
    @Test
    public void TestPayRentRoll0() {
        int diceRoll = 0;
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 4 * diceRoll;

        try {
            utility1.payRent(secondPlayer, diceRoll);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        } catch (IsInJailException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method works correctly in normal condition with a dice roll of 5
     */
    @Test
    public void TestPayRentRoll5() {
        int diceRoll = 5;
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 4 * diceRoll;

        try {
            utility1.payRent(secondPlayer, diceRoll);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        } catch (IsInJailException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method works correctly in normal condition with a dice roll of 0 and both utility
     */
    @Test
    public void TestPayRentOwns2Roll0() {
        owner.addProperty(utility2);
        int diceRoll = 0;
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 10 * diceRoll;

        try {
            utility1.payRent(secondPlayer, diceRoll);
        } catch (IsMortgagedException | IsInJailException e) {
            e.printStackTrace();
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method works correctly in normal condition with a dice roll of 4 and both utility
     */
    @Test
    public void TestPayRentOwns2Roll4() {
        owner.addProperty(utility2);
        int diceRoll = 4;
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 10 * diceRoll;

        try {
            utility1.payRent(secondPlayer, diceRoll);
        } catch (IsMortgagedException | IsInJailException e) {
            e.printStackTrace();
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method throw IsMortgagedException properly
     */
    @Test
    public void TestPayRentIsMortgagedException() {
        Object expected = IsMortgagedException.class;
        Object actual = null;
        utility1.setMortgaged(true);
        try {
            utility1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            actual = IsMortgagedException.class;
        } catch (IsInJailException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected,actual);
    }

    /**
     * test to verify if the .payrent() method correctly increase the owners money
     */
    @Test
    public void TestPayRentIncreaseOwnersMoney() {
        int diceRoll = 5;
        int ownersWealth = owner.getBalance();
        int expected = ownersWealth + 4 * diceRoll;

        try {
            utility1.payRent(secondPlayer, diceRoll);
        } catch (IsMortgagedException | IsInJailException e) {
            e.printStackTrace();
        }
        assertEquals(expected,owner.getBalance());
    }
}
