package backend;

import java.util.ArrayList;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {
    int numDice; //Number of dice
    int diceMin; //smallest num on dice
    int diceMax; //largest num on dice

    ArrayList<Integer> dice = new ArrayList<>(); //array of dice

    public Dice(int numDice, int diceMin, int diceMax){
        this.numDice = numDice;
        this.diceMin = diceMin;
        this.diceMax = diceMax;
        for (int i = 0; i < numDice; i++) {
            dice.add(0);
        }
    }

    /**
     * Sets each 'dice' in the array to a randInt between diceMin & diceMax (inclusive)
     */
    public void rollDice() {
        for (int i = 0; i < numDice; i++) {
            dice.set(ThreadLocalRandom.current().nextInt(diceMin, diceMax + 1), i);
        }
    }

    /***
     * returns array of dice (intergers)
     * @return array of dice (intergers)
     */
    public ArrayList<Integer> getDiceValues() {
        return dice;
    }

    public void setNumDice(int numDice) {
        this.numDice = numDice;
    }

    public void setDiceMax(int diceMax) {
        this.diceMax = diceMax;
    }

    public void setDiceMin(int diceMin) {
        this.diceMin = diceMin;
    }
}