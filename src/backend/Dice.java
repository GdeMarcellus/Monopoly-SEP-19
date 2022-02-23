package backend;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {
    int numDice; //Number of dice
    int diceMin; //smallest num on dice
    int diceMax; //largest num on dice

    int dice[]; //array of dice

    public Dice(int numDice, int diceMin, int diceMax){
        this.numDice = numDice;
        this.diceMin = diceMin;
        this.diceMax = diceMax;
    }

    /**
     * Sets each 'dice' in the array to a randInt between diceMin & diceMax (inclusive)
     * @param void
     * @return void
     */
    public void rollDice() {
        for (int i = 0; i < numDice; i++) {
            dice[i] = ThreadLocalRandom.current().nextInt(diceMin, diceMax + 1);
        }
    }

    /***
     * returns array of dice (intergers)
     * @param void
     * @return array of dice (intergers)
     */
    public int[] getDiceValues() {
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
