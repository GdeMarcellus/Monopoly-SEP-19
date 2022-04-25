package GUI;

import javafx.beans.binding.Bindings;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;



public class PlayerInformation {
    Color playerColor;
    Button playerToken;
    String playerColor_String;
    int playerNumber;
    PlayerInformation(String color_string, Color color, int playerNo, String tokenLocation)
    {
       playerColor = color;
       playerNumber = playerNo;
       playerColor_String = color_string;
       playerToken = new Button();
       Image image = new Image(tokenLocation);
       ImageView finalToken = new ImageView(image);
       finalToken.setFitWidth(80);
       finalToken.setFitHeight(80);

       //https://stackoverflow.com/questions/35866168/javafx-css-button-graphic-resize
       playerToken.setStyle("-fx-background-radius: 50;" +
               "    -fx-background-image: url("+tokenLocation+");\n" +
               "    -fx-background-size: 64px 64px;\n" +
               "    -fx-background-repeat: no-repeat;\n" +
               "    -fx-background-position: center;" +
               "    -fx-background-color: #" + playerColor.toString().substring(2));
       playerToken.setMaxHeight(60);
       playerToken.setMaxWidth(60);
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public String getPlayerColor_String()
    {
        return playerColor.toString().substring(2);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Button getPlayerToken()
    {
        return playerToken;
    }
}

