package backend.Tiles;

import backend.Board;
import backend.Card;

public class TileCard extends Tile{
    public enum Type {
        Opportunity,
        Luck
    }
    Type type;

    public TileCard(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @param board the board on which the tile is
     * @return a card from the deck
     */
    public Card pickCard(Board board){
        Card card = null;
        
        if (type == Type.Luck) {
            card = board.getPotLuckCard().poll();
        }
        else if (type == Type.Opportunity) {
            card = board.getOpportunityCard().poll();
        }
        else {
            throw new RuntimeException();
        }
        board.getOpportunityCard().add(card);
        return card;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
