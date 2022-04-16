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
        if (playerColor == Color.RED)
        {
            return "Red";
        }
        else if (playerColor == Color.BLUE)
        {
            return "Blue";
        }
        else if (playerColor == Color.GREEN)
        {
            return "Green";
        }
        else if (playerColor == Color.PURPLE)
        {
            return "Purple";
        }
        else if (playerColor == Color.ORANGE)
        {
            return "Orange";
        }
        return null;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Circle getPlayerToken()
    {
        return playerToken;
    }
}

