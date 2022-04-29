package backend;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DiceTest {
    int numDice = 2; //Number of dice
    int diceMin = 1; //smallest num on dice
    int diceMax = 6; //largest num on dice

    private ArrayList<Dice> diceList = new ArrayList<>();

    @Test
    public void test1() {
        //create dice
        for (int i = 0; i < 10; i++){
            Dice newDice = new Dice(numDice, diceMin, diceMax);
            diceList.add(newDice);
        }
        //roll dice
        for (int i = 0; i < 10; i++){
            diceList.get(i).rollDice();
        }
        //get and test values
        for (int i = 0; i < 10; i++){
            ArrayList<Integer> a = diceList.get(i).getDiceValues();
            //numDice check
            assertTrue(a.size() == numDice);
            //Dice min check
            assertTrue(a.get(0) >= diceMin);
            assertTrue(a.get(1) >= diceMin);
            //Dice max check
            assertTrue(a.get(0) <= diceMax);
            assertTrue(a.get(1) <= diceMax);
        }
    }
}
