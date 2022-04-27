package backend;

import backend.Exception.IsMortgagedException;
import backend.Exception.LargeDevelopmentDifferenceException;
import backend.Exception.NoDevelopmentException;
import backend.Exception.PropertyDevelopedException;
import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.TileProperty;
import backend.Tiles.TileUtility;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TilePropertyTest {

    Player owner = new HumanPlayer();
    Player bank = new HumanPlayer();
    TileProperty property;
    TileProperty property2;

    @Before
    public void setUp(){
        bank.addMoney(1000);
        ArrayList<TileProperty> neighborhood = new ArrayList<TileProperty>();
        property2 = new TileUtility(10,"prop2",null,neighborhood, false);
        property = new TileUtility(10,"prop1",owner,neighborhood, false);
        neighborhood.add(property2);
        neighborhood.add(property);
        owner.addProperty(property);
    }

    /**
     * test if .ownsNeighborhood() method works properly when neighborhood is owned
     */
    @Test
    public void TestOwnsNeighborhoodTrue() {
        owner.addProperty(property2);
        assertTrue(property.ownsNeighborhood());
    }
    /**
     * test if .ownsNeighborhood() method works properly when neighborhood is not owned
     */
    @Test
    public void TestOwnsNeighborhoodFalse() {
        assertFalse(property.ownsNeighborhood());
    }


    @Test
    public void TestMortgaged(){

    }
    @Test
    public void TestMortgagedIsMortgagedException() {
        Object expected = IsMortgagedException.class;
        Object actual = null;
        property.setMortgaged(true);
        try {
            property.mortgage();
        } catch (IsMortgagedException e) {
           actual = IsMortgagedException.class;
        } catch (PropertyDevelopedException e) {
            fail();
        }
        assertEquals(expected,actual);

    }

    @Test
    public void TestMortgagedBuyBack() {

    }

}
