package GUI;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerInformation {
    Color playerColor;
    Circle playerToken;
    int playerNumber;
    PlayerInformation(Color color, int playerNo)
    {
       playerColor = color;
       playerNumber = playerNo;
       playerToken = new Circle(20,playerColor);
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Circle getPlayerToken()
    {
        return playerToken;
    }
}

