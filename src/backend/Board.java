package backend;

public class Board {

    private Tile[] tiles;
    private Player[] players;
    private int currentTurn;
    private int maxDoubles = 3;

    /**
     * Board constructor
     * initiate the board with 40 tiles and 2 players
     */
    public Board() {
        this.tiles = new Tile[40] ;
        for (Tile current: tiles) {
            current = new Tile();
        }
        this.players = new Player[2];
        for (Player current: players) {
            current = new HumanPlayer();
        }

    }

    /***
     * takes the turn for the player
     * moves the player by the total of the dices
     * check if the player has a double if he has a double keep it his turn
     * otherwise make it the next player turns
     */
    public void turn(Player currentPlayer, int[] dices) {
        boolean rolledDouble;
        rolledDouble = check_double(currentPlayer,dices);
        int playerPosition = currentPlayer.getPosition();

        currentPlayer.setPosition(playerPosition + dices[1] + dices[2]);

        if (!rolledDouble){
            next_turn();
        }
    }

    /**
     * increment the current_turn value with wrap around
     * reset all double to 0
     */
    private void next_turn(){
        currentTurn += 1;
        if (currentTurn > players.length){
            currentTurn = 0;
        }
        for (Player player: players) {
            player.setNumDoubles(0);
        }
    }

    /**
     * returns array of dice (intergers)
     * check if the dice are a double and record it to the player
     * @param player the player who rolled the dice
     * @param dices the size 2 array containing the dice
     * @return boolean true if both dice are the same
     */
    private boolean check_double(Player player, int[] dices){
        boolean returnValue = false;
        if (dices[0] == dices[1]){
            returnValue = true;
            int playerNumDoubles =  player.getNumDoubles();
            player.setNumDoubles(playerNumDoubles+1);

        }
        return returnValue;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getMaxDoubles() {
        return maxDoubles;
    }

    public void setMaxDoubles(int maxDoubles) {
        this.maxDoubles = maxDoubles;
    }


}
