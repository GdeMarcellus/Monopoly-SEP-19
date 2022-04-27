package backend.Tiles;

import backend.Board;
import backend.Card;

public class TileCard extends Tile{
    public enum Type {
        Opportunity,
        Luck
    }
    Type type;

    /**
     * @param board the board on which the tile is
     * @return a card from the deck (doers not put the card back at the bottom of the pack)
     */
    public Card pickCard(Board board){
        Card card = null;
        
        if (type == Type.Luck)
        {
            card = board.getPotLuckCard().poll();
        }
        else if (type == Type.Opportunity)
        {
            card = board.getOpportunityCard().poll();
        }
        else{
            throw new RuntimeException();
        }
        System.out.println(card);
        return card;
    }



    public TileCard(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }



}
