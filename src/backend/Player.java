package backend;

public abstract class Player {

    private int position;
    private int balance;
    private int numDoubles;

    public Player() {
        this.position = 0;
        this.balance = 0;
        this.numDoubles = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        //check position not beyond board limits
        if (this.position + position > 40) {
            this.position = position % 40;
        }
        else {
            this.position = position;
        }
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getNumDoubles() {
        return numDoubles;
    }

    public void setNumDoubles(int numDoubles) {
        this.numDoubles = numDoubles;
    }
}
