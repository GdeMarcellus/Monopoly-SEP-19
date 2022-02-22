import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class GUI extends Application {
    public static void main(String []args)
    {
        launch();
    }
    public void start(Stage primaryStage)
    {
        start_screen();
    }
    public void start_screen()
    {
        //Setting up Stages
        Stage introduction = new Stage();
        //Setting up Title Text
        Text title = createText("Property Tycoon", 100, Color.BLACK,"arial");
        title.setStroke(Color.BLACK);
        //FillTransition for Title Text
        FillTransition title_animation = new FillTransition(Duration.millis(3000),title,Color.WHITE, Color.RED);
        title_animation.setCycleCount(4);
        title_animation.setAutoReverse(true);
        title_animation.play();
        Text enterCont = createText("Press Enter to Continue!",100,Color.BLACK,"arial");
        //Following FadeTransition technique taken from this source:
        //https://stackoverflow.com/questions/43084698/flashing-label-in-javafx-gui
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), enterCont);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.play();
        //Preparing BorderPane for introduction page
        BorderPane mainStage = new BorderPane();
        mainStage.setBottom(enterCont);
        mainStage.setCenter(title);
        BorderPane.setAlignment(enterCont, Pos.CENTER);
        Scene lmao;
        introduction.setScene(lmao = new Scene(mainStage,1500, 1000));
        lmao.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                introduction.close();
                mainMenu();
            }
        });
        introduction.showAndWait();
    }
    public void gameBoard()
    {
        Stage gameboard = new Stage();
        //Creating and setting up the game board (using a GridPane)
        GridPane board = createBoard();
        board.setAlignment(Pos.CENTER);
        //Creating a title for the scene
        Text title = createText("Property Tycoon", 50, Color.BLACK,"arial");
        title.setStroke(Color.BLACK);
        BorderPane.setAlignment(title,Pos.CENTER);
        //FillTransition for Title Text
        FillTransition title_mainAnimation = new FillTransition(Duration.millis(1000),title,Color.WHITE, Color.RED);
        title_mainAnimation.setCycleCount(4);
        title_mainAnimation.setAutoReverse(true);
        title_mainAnimation.play();
        //Creating text for the player section of the board
        Text players = createText("Players",40,Color.BLACK,"arial");
        //Creating a VBox to store and show player info
        VBox playerList = new VBox(players,getPlayerInfo());
        playerList.setAlignment(Pos.CENTER);
        //Creating HBox to store and show the player controls
        HBox controls = new HBox(controlButtons());
        controls.setAlignment(Pos.CENTER);
        controls.setMinHeight(100);
        Text idk = createText("IDK",40,Color.BLACK,"arial");
        VBox left = new VBox(idk,getPlayerInfo());
        left.setAlignment(Pos.CENTER);
        //Setting up the main BorderPane of the scene
        BorderPane main = new BorderPane();
        main.setRight(left);
        main.setBottom(controls);
        main.setTop(title);
        main.setCenter(board);
        main.setLeft(playerList);
        //Creating a new Scene
        gameboard.setScene(new Scene(main,1500,1000));
        gameboard.showAndWait();
    }
    private HBox controlButtons()
    {
        //Setting up Buy button
        Button Buy = new Button("Buy");
        Buy.setPrefSize(100,50);
        //Setting up Skip button
        Button Skip = new Button("Skip");
        Skip.setPrefSize(100,50);
        //Setting up Build button
        Button Build = new Button("Build");
        Build.setPrefSize(100,50);
        //Setting up HBox to finalize the controls
        HBox final_control = new HBox(Buy,Skip,Build);
        final_control.setAlignment(Pos.CENTER);
        final_control.setSpacing(100);
        return final_control;
    }
    private ListView<String> getPlayerInfo()
    {
        //Temporary ListView, String type will change later
        ListView<String> player = new ListView<>();
        for (int i = 0; i < 3; i++)
        {
            player.getItems().add(String.valueOf(i));
        }
        return player;
    }

    public void mainMenu()
    {
        //Creating stage for the mainMenu
        Stage mainMenu = new Stage();
        //Creating exit button
        Button exit = new Button("Exit");
        exit.setOnAction( e -> {System.exit(0);});
        //Main Text set up
        Text main_Text = createText("Main Menu",50,Color.BLACK,"arial");
        BorderPane.setAlignment(main_Text,Pos.CENTER);
        BorderPane main = new BorderPane();
        //Creating Buttons
        Button quick_game_button = new Button("Quick Game");
        //Style of Button Gotten from:
        //http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
        quick_game_button.setStyle("-fx-padding: 8 15 15 15;\n" +
                "    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;\n" +
                "    -fx-background-radius: 8;\n" +
                "    -fx-background-color: \n" +
                "        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),\n" +
                "        #9d4024,\n" +
                "        #d86e3a,\n" +
                "        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);\n" +
                "    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-font-size: 8em;");

        quick_game_button.setPrefWidth(500);
        quick_game_button.setPrefHeight(200);
        quick_game_button.setOnAction(e ->
        {
            mainMenu.close();
            gameBoard();
        });
        Button settings_button = new Button("Settings");
        settings_button.setPrefWidth(500);
        settings_button.setPrefHeight(200);
        settings_button.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        settings_button.setStyle(""+
                "    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;\n" +
                "    -fx-background-radius: 8;\n" +
                "    -fx-background-color: \n" +
                "        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),\n" +
                "        #9d4024,\n" +
                "        #d86e3a,\n" +
                "        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);\n" +
                "    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-font-size: 8em;");
        settings_button.setOnAction(e ->
                {
                System.out.println("Second Works");
                mainMenu.close();
                });
        HBox title =  new HBox(main_Text, exit);
        title.setAlignment(Pos.TOP_CENTER);
        VBox main_buttons = new VBox(quick_game_button,settings_button);
        main_buttons.setAlignment(Pos.CENTER);
        main_buttons.setSpacing(50);
        main.setCenter(main_buttons);
        main.setTop(title);
        mainMenu.setScene(new Scene(main,1500,1000));
        mainMenu.showAndWait();
    }
    public GridPane createBoard()
    {
        //count is used to keep track of the png in the Base folder
        int count = 1;
        //Creating gridpane to store the board
        GridPane gridPane = new GridPane();
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
               if(i == 0 || j == 0 || i == 9 || j == 9)
               {
                   //Store image in Image tile
                   Image tile = new Image("Base/" + count + ".png");
                   //String location used to test button functionality
                   String location = i + "  " + j;
                   ImageView set = new ImageView(tile);
                   set.setFitHeight(60);
                   set.setFitWidth(60);
                   //Setting up the Button for each tile
                   Button insert = new Button();
                   insert.setOnAction(e->{
                       Image bank = new Image("src/bank.gif");
                       ImageView bankAnimation = new ImageView(bank);
                       Alert image = new Alert(Alert.AlertType.CONFIRMATION);
                       System.out.println(bankAnimation);
                   });
                   insert.setStyle("-fx-background-color: Transparent");
                   insert.setGraphic(set);
                   insert.setPadding(Insets.EMPTY);
                   gridPane.add(insert,i,j);
                   count++;
               }
               else
               {
                   //Creating an empty button (for any tile that is not used)
                   Button empty = new Button();
                   empty.setStyle("-fx-background-color: Transparent");
                   gridPane.add(empty,i,j);
               }
            }
        }
        return gridPane;
    }
    public Text createText(String content, int size, Color col, String font)
    {
        //createText is used to simplify the creation of any text
        Text returnText = new Text(content);
        returnText.setStyle(String.format("-fx-font: %d %s",size,font));
        returnText.setSelectionFill(col);
        return returnText;
    }
}
