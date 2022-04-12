package backend;

import backend.Exception.IsMortgagedException;
import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileProperty;
import backend.Tiles.TileStation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TileStationTest {

    Player owner = new HumanPlayer();
    Player secondPlayer = new HumanPlayer();

    TileStation station1;
    TileStation station2;
    TileStation station3;
    TileStation station4;

    @Before
    public void setUp(){
        secondPlayer.setBalance(10000);
        owner.setBalance(10000);
        ArrayList<Integer> rent = new ArrayList<Integer>();
        rent.add(10);
        rent.add(20);
        rent.add(30);
        rent.add(40);
        ArrayList<TileProperty> neighborhood = new ArrayList<TileProperty>();
        station1 = new TileStation(rent,10,"station1", owner, neighborhood,false );
        neighborhood.add(station1);
        station2 = new TileStation(rent,10,"station2", null, neighborhood,false );
        neighborhood.add(station2);
        station3 = new TileStation(rent,10,"station3", null, neighborhood,false );
        neighborhood.add(station3);
        station4 = new TileStation(rent,10,"station4", null, neighborhood,false );
        neighborhood.add(station4);

    }

    /**
     * test to verify if the .payrent() method works correctly when player owns 1 station
     */
    @Test
    public void TestPayRent1Station() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 10;

        try {
            station1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method works correctly when player owns 2 station
     */
    @Test
    public void TestPayRent2Station() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 20;
        owner.addProperty(station2);
        try {
            station1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method works correctly when player owns 3 station
     */
    @Test
    public void TestPayRent3Station() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 30;
        owner.addProperty(station2);
        owner.addProperty(station3);
        try {
            station1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        }

        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method works correctly when player owns 4 station
     */
    @Test
    public void TestPayRent4Station() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 40;
        owner.addProperty(station2);
        owner.addProperty(station3);
        owner.addProperty(station4);
        try {
            station1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
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
        station1.setMortgaged(true);
        try {
            station1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            actual = IsMortgagedException.class;
        }

        assertEquals(expected,actual);
    }

    /**
     * test to verify if the .payrent() method correctly increase the owners money
     */
    @Test
    public void TestPayRentIncreaseOwnersMoney() {
        int ownersWealth = owner.getBalance();
        int expected = ownersWealth + 10;

        try {
            station1.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        }
        assertEquals(expected,owner.getBalance());
    }
}
