package GUI;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerInformation {
    Color playerColor;
    Circle playerToken;
    String playerColor_String;
    int playerNumber;
    PlayerInformation(String color_string, Color color, int playerNo)
    {
       playerColor = color;
       playerNumber = playerNo;
       playerColor_String = color_string;
       playerToken = new Circle(20,playerColor);
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public String getPlayerColor_String()
    {
        return playerColor_String;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Circle getPlayerToken()
    {
        return playerToken;
    }
}

