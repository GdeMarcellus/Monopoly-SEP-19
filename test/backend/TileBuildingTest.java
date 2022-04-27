package backend;

import backend.Exception.*;
import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TileBuildingTest {

    Player owner = new HumanPlayer();
    Player secondPlayer = new HumanPlayer();

    TileBuilding tileBuilding;
    TileBuilding property2;
    TileBuilding property3;


    @Before
    public void setUp(){
        secondPlayer.setBalance(10000);
        owner.setBalance(10000);
        String hexColour = "#ffffff";
        ArrayList<Integer> rent = new ArrayList<Integer>();
        rent.add(10);
        rent.add(20);
        rent.add(30);
        rent.add(40);
        rent.add(50);
        int developmentCost = 20;
        String name = "p1";
        ArrayList<TileProperty> neighborhood = new ArrayList<TileProperty>();
        property2 = new TileBuilding(hexColour,rent,developmentCost,0,10 ,
                "p2", owner, neighborhood, false);
        neighborhood.add(property2);
        property3 = new TileBuilding(hexColour,rent,developmentCost,0,10 ,
                "p3", owner, neighborhood, false);
        neighborhood.add(property3);
        tileBuilding = new TileBuilding(hexColour,rent, developmentCost,0, 10 ,name, owner, neighborhood, false);
        neighborhood.add(tileBuilding);
        owner.addProperty(tileBuilding);
    }

    /**
     * test to verify if the .payrent() method correctly when the property is mortgaged
     */
    @Test
    public void TestPayRentIfMortgaged() {
        tileBuilding.setMortgaged(true);
        Object expected = IsMortgagedException.class;
        Object actual = null;
        try {
            tileBuilding.payRent(secondPlayer, 5);
        } catch (IsMortgagedException e) {
            actual = IsMortgagedException.class;
        } catch (IsInJail e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .payrent() method works correctly in normal condition
     */
    @Test
    public void TestPayRent() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        int expected = secoundPlayerWealth - 10;
        try {
            tileBuilding.payRent(secondPlayer,5);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        } catch (IsInJail e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() the owner does indeed get the money from the rent
     */
    @Test
    public void TestPayRentPlayerGotMoney() {
        int ownersWealth = owner.getBalance();
        int expected = ownersWealth + 10;
        System.out.println(secondPlayer.getBalance());
        try {
            tileBuilding.payRent(secondPlayer,5);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        } catch (IsInJail e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected,owner.getBalance());
    }

    /**
     * test to verify if the .payrent() method correctly when the owner owns the all neighborhood
     */
    @Test
    public void TestPayRentOwnsNeighborhood() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        owner.addProperty(property2);
        owner.addProperty(property3);
        int expected = secoundPlayerWealth - 20;
        try {
            tileBuilding.payRent(secondPlayer,5);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        } catch (IsInJail e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected,secondPlayer.getBalance());
    }

    /**
     * test to verify if the .payrent() method correctly when the property has a development level
     */
    @Test
    public void TestPayRentNonZeroDevelopment() {
        int secoundPlayerWealth = secondPlayer.getBalance();
        tileBuilding.setDevelopment(1);
        int expected = secoundPlayerWealth - 20;
        try {
            tileBuilding.payRent(secondPlayer,5);
        } catch (IsMortgagedException e) {
            e.printStackTrace();
        } catch (IsInJail e) {
            throw new RuntimeException(e);
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
        tileBuilding.setMortgaged(true);
        try {
            tileBuilding.payRent(secondPlayer, 0);
        } catch (IsMortgagedException e) {
            actual = IsMortgagedException.class;
        } catch (IsInJail e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected,actual);
    }

    /**
     * test to verify if the .mortgaged() method correctly in normal condition
     */
    @Test
    public void Testmortgaged() {
        int expectedValue = 5;
        int checkValue = 0;
        try {
            checkValue = tileBuilding.mortgage();
        }
        catch (IsMortgagedException | PropertyDevelopedException e) {
            fail();
        }
        assertEquals(expectedValue, checkValue);

    }

    /**
     * test to verify if the .mortgaged() method correctly when a IsMortgagedException should be thrown
     */
    @Test
    public void TestmortgagedIsMortgagedException() {
        Object expected = IsMortgagedException.class;
        Object actual = null;
        tileBuilding.setMortgaged(true);
        try {
            tileBuilding.mortgage();
        } catch (IsMortgagedException e) {
            actual = IsMortgagedException.class;

        } catch (PropertyDevelopedException e) {
            fail();
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .mortgaged() method correctly when a PropertyDevelopedException should be thrown
     */
    @Test
    public void TestmortgagedPropertyDevelopedException() {
        Object expected = PropertyDevelopedException.class;
        Object actual = null;
        tileBuilding.setDevelopment(2);
        try {
            tileBuilding.mortgage();
        } catch (IsMortgagedException e) {
            fail();

        } catch (PropertyDevelopedException e) {
            actual = PropertyDevelopedException.class;
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .buyHouse() method correctly under normal condition
     */
    @Test
    public void TestBuyHouse() {
        int expected = 1;
        try {
            tileBuilding.buyHouse(secondPlayer);
        } catch (PropertyDevelopedException |
                LargeDevelopmentDifferenceException |
                IsMortgagedException |
                InsufficientFundsException e) {
            fail();
        }
        int actual = tileBuilding.getDevelopment();
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .buyHouse() method correctly when a PropertyDevelopedException should be thrown
     */
    @Test
    public void TestBuyHousePropertyDevelopedException() {
        tileBuilding.setDevelopment(5);
        Object expected = PropertyDevelopedException.class;
        Object actual = null;
        try {
            tileBuilding.buyHouse(secondPlayer);
        } catch (LargeDevelopmentDifferenceException |
                IsMortgagedException |
                InsufficientFundsException e) {
            fail();
        } catch (PropertyDevelopedException e){
            actual = PropertyDevelopedException.class;
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .buyHouse() method correctly when a LargeDevelopmentDifferenceException should be thrown
     */
    @Test
    public void TestBuyHousePropertyLargeDevelopmentDifferenceException() {
        tileBuilding.setDevelopment(3);
        owner.addProperty(property2);
        owner.addProperty(property3);
        Object expected = LargeDevelopmentDifferenceException.class;
        Object actual = null;
        try {
            tileBuilding.buyHouse(secondPlayer);
        } catch (PropertyDevelopedException |
                IsMortgagedException |
                InsufficientFundsException e) {
            fail();
        } catch (LargeDevelopmentDifferenceException e){
            actual = LargeDevelopmentDifferenceException.class;
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .buyHouse() method correctly when a IsMortgagedException should be thrown
     */
    @Test
    public void TestBuyHouseIsMortgagedException() {
        tileBuilding.setMortgaged(true);
        Object expected = IsMortgagedException.class;
        Object actual = null;
        try {
            tileBuilding.buyHouse(secondPlayer);
        } catch (PropertyDevelopedException |
                LargeDevelopmentDifferenceException |
                InsufficientFundsException e) {
            fail();
        } catch (IsMortgagedException e){
            actual = IsMortgagedException.class;
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .buyHouse() method correctly when a InsufficientFundsException should be thrown
     */
    @Test
    public void TestBuyHouseInsufficientFundsException() {
        owner.removeMoney(10000);
        Object expected = InsufficientFundsException.class;
        Object actual = null;
        try {
            tileBuilding.buyHouse(secondPlayer);
        } catch (PropertyDevelopedException |
                LargeDevelopmentDifferenceException |
                IsMortgagedException e) {
            fail();
        } catch (InsufficientFundsException e){
            actual = InsufficientFundsException.class;
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .sellHouse() method correctly under normal circumstance
     */
    @Test
    public void TestSellHouse() {
        tileBuilding.setDevelopment(2);
        int expected = 1;
        try {
            tileBuilding.sellHouse(secondPlayer);
        } catch (LargeDevelopmentDifferenceException | NoDevelopmentException e) {
           fail();
        }

        int actual = tileBuilding.getDevelopment();
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .sellHouse() method works correctly when a LargeDevelopmentDifferenceException should be thrown
     */
    @Test
    public void TestSellHouseLargeDevelopmentDifferenceException() {
        owner.addProperty(property2);
        owner.addProperty(property3);
        property3.setDevelopment(5);
        tileBuilding.setDevelopment(1);
        Object expected = LargeDevelopmentDifferenceException.class;
        Object actual = null;
        try {
            tileBuilding.sellHouse(secondPlayer);
        } catch (LargeDevelopmentDifferenceException e) {
            actual = LargeDevelopmentDifferenceException.class;
        } catch (NoDevelopmentException e) {
            fail();
        }
        assertEquals(expected,actual);

    }

    /**
     * test to verify if the .sellHouse() method correctly when a NoDevelopmentException should be thrown
     */
    @Test
    public void TestSellHouseNoDevelopmentException() {
        tileBuilding.setDevelopment(0);
        Object expected = NoDevelopmentException.class;
        Object actual = null;
        try {
            tileBuilding.sellHouse(secondPlayer);
        } catch (NoDevelopmentException e) {
            actual = NoDevelopmentException.class;
        } catch (LargeDevelopmentDifferenceException e) {
            fail();
        }
        assertEquals(expected,actual);

    }

    /**
    * test to verify wether or not the .developmentDifference() method works correctly with a difference of 2
     */
    @Test
    public void TestDevelopmentDifference1() {
        int expected = 2;
        tileBuilding.setDevelopment(2);
        int actual = tileBuilding.developmentDifference();
        assertEquals(expected,actual);
    }
    /**
     * test to verify wether or not the .developmentDifference() method works correctly with a difference of 0
     */
    @Test
    public void TestDevelopmentDifference2() {
        int expected = 0;
        tileBuilding.setDevelopment(0);
        int actual = tileBuilding.developmentDifference();
        assertEquals(expected,actual);
    }



}
