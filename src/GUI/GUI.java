package GUI;

import backend.Board;
import backend.Dice;
import backend.HumanPlayer;
import backend.Property;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class GUI extends Application {

    //Backend
    private HashMap<Integer, Color> player_Index;
    private Text[] cardInfo_Text;
    private GridPane board;

    //Player
    private StackPane final_main;
    PlayerInformation[] playerInformation;

    //Dice Variables
    private final Dice dices = new Dice(2,1,6);
    private final Button dice1 = new Button();
    private final Button dice2 = new Button();
    private final ArrayList<Image> facePNG = getDiceFaces();

    private boolean inspectWindow = false;
    private final ArrayList<Button> tiles = new ArrayList<>();
    private  final Stage gameBoard_GUI = new Stage();
    private Board gameBoard = new Board();
    private Text moneyOfPlayer;
    private Text playerTurnText;
    private ListView<Text> playerInfo = new ListView<>();
    private int playerTurn = 0;
    private boolean finishedTurn = false;

    public static void main(String []args)
    {
        launch();
    }

    public void start(Stage primaryStage)
    {
        start_screen();
    }

    /**
     * The start_screen method, represents the first screen the user will see
     * It contains a flashy title, and players are allowed to move to the next stage
     * By pressing the Enter button
     */
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
        Scene buttonScene;
        introduction.setScene(buttonScene = new Scene(mainStage,1500, 1000));
        buttonScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                introduction.close();
                mainMenu();
            }
        });
        introduction.show();
    }

    /**
     * the gameBoard method is used to create the GUI of the main boardGame for the user
     */
    public void gameBoard()
    {
        //Money Counter
        Label moneyCounter = new Label("Money Amount");
        moneyCounter.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
                    -fx-background-color:\s
                        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),
                        #9d4024,
                        #d86e3a,
                        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);
                    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                    -fx-font-weight: bold;
                    -fx-font-size: 30;""");

        //Name of the user
        moneyOfPlayer = new Text(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
        moneyOfPlayer.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
                    -fx-background-color:\s
                        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),
                        #9d4024,
                        #d86e3a,
                        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);
                    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                    -fx-font-weight: bold;
                    -fx-font-size: 50;""");
        moneyOfPlayer.setTextAlignment(TextAlignment.CENTER);

        //Next
        playerTurnText = new Text("Player Turn:  " +(playerTurn+1));
        playerTurnText.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
                    -fx-background-color:\s
                        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),
                        #9d4024,
                        #d86e3a,
                        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);
                    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                    -fx-font-weight: bold;
                    -fx-font-size: 30;""");

        //Creating and setting up the game board (using a GridPane)
        board = createBoard();
        board.setAlignment(Pos.CENTER);

        //Creating a title for the scene
        Text title = createText("Property Tycoon", 50, Color.BLACK,"arial");
        HBox top= new HBox(title);
        title.setStroke(Color.BLACK);
        BorderPane.setAlignment(top,Pos.CENTER);
        top.setAlignment(Pos.CENTER);
        top.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //FillTransition for Title Text
        FillTransition title_mainAnimation = new FillTransition(Duration.millis(3000),title,Color.WHITE, Color.RED);
        title_mainAnimation.setCycleCount(100);
        title_mainAnimation.setAutoReverse(true);
        title_mainAnimation.play();

        //Creating text for the player section of the board
        Text players = createText("Players",40,Color.BLACK,"arial");

        //Start selection with the first element in the list
        playerInfo.getSelectionModel().select(0);
        playerInfo.setMouseTransparent(true);
        VBox playerList = new VBox(players);
        playerList.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        playerList.setAlignment(Pos.CENTER);

        //Left side of board
        //Creating HBox to store and show the player controls
        HBox controls = new HBox(controlButtons());
        controls.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        controls.setAlignment(Pos.CENTER);
        controls.setMinHeight(100);

        //Right side of board
        //Working on the right side of the game-board
        Text idk = createText("Bank",40,Color.BLACK,"arial");
        VBox bankSide = new VBox(idk, playerTurnText,moneyCounter, moneyOfPlayer);
        bankSide.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        bankSide.setAlignment(Pos.CENTER);
        bankSide.setSpacing(30);

        //Setting up the main BorderPane of the scene
        BorderPane main = new BorderPane();
        main.setRight(bankSide);
        main.setBottom(controls);
        main.setTop(top);
        board.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        main.setCenter(board);
        main.setLeft(playerList);
         final_main = new StackPane(main);
        for(int i =0 ; i < playerInformation.length; i++)
        {
            final_main.getChildren().add(playerInformation[i].getPlayerToken());
            playerInformation[i].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(i).getPosition(),i));
            playerInformation[i].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(i).getPosition(),i));
        }

        playerInfo = new ListView<>();
        for (int i = 0; i < gameBoard.getPlayerNum(); i++)
        {
            playerInfo.getItems().add(i,getPlayerInfo(i));
        }
        playerInfo.getItems().get(0).setStroke(playerInformation[0].playerColor);
        playerList.getChildren().add(playerInfo);
        BorderPane.setAlignment(bankSide,Pos.CENTER_RIGHT);

        Scene finalScene = new Scene(final_main,1500,1000);
        //Creating a new Scene
        gameBoard_GUI.setScene(finalScene);
        gameBoard_GUI.show();
    }

    /**
     * controlButtons is used to create an HBox containing all the controls the user is going to have
     * @return Returning the HBox containing all the control buttons for the player
     */
    private HBox controlButtons()
    {
        //Pause transition taken from https://stackoverflow.com/questions/55768170/temporarily-change-color-of-a-button-when-clicked

        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));

        //Setting up Buy button
        Button Buy = new Button("Buy");
        Buy.setPrefSize(100,50);
        Buy.setOnAction(e->
        {
            if (!finishedTurn)
            {
                Alert d = new Alert(AlertType.WARNING);
                d.setContentText("Roll dies and move before playing another action!");
                d.show();
            }
            else
            {
                if(gameBoard.remainingBalance(playerTurn,gameBoard.getPlayer(playerTurn).getPosition()) < 0)
                {
                    Alert d = new Alert(AlertType.WARNING);
                    d.setContentText("Can't Afford");
                    d.show();
                }
                else
                {
                    //Pay Price
                    gameBoard.purchase(playerTurn,gameBoard.getPlayer(playerTurn).getPosition());
                    //Set Owner of tile bought
                    ((Property) gameBoard.getTiles()[gameBoard.getPlayer(playerTurn).getPosition()]).setOwner(gameBoard.getPlayer(playerTurn));

                    //Change GUI
                    moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                    moneyOfPlayer.setFill(Color.RED);
                    transition.playFromStart();
                }
            }
        });
        //Setting up move button
        Button move = new Button("Move");
        move.setPrefSize(100,50);
        move.setOnAction(e ->
                {
                    if (!finishedTurn) {
                    dices.rollDice();
                    ImageView first = new ImageView(facePNG.get(dices.getDiceValues().get(0)-1));
                    first.setFitWidth(60);
                    first.setFitHeight(60);
                    dice1.setGraphic(first);
                    ImageView second = new ImageView(facePNG.get(dices.getDiceValues().get(1)-1));
                    second.setFitWidth(60);
                    second.setFitHeight(60);
                    dice2.setGraphic(second);
                    int dieValues = dices.getDiceValues().get(0) + dices.getDiceValues().get(1);
                    if (Objects.equals(dices.getDiceValues().get(0), dices.getDiceValues().get(1)))
                    {
                        gameBoard.getPlayer(playerTurn).setPosition(dieValues + gameBoard.getPlayer(playerTurn).getPosition());
                        playerInformation[playerTurn].getPlayerToken().setLayoutY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                        playerInformation[playerTurn].getPlayerToken().setLayoutX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                        finishedTurn = false;
                    }
                    else
                    {
                        System.out.println(tiles.get(playerTurn).getBoundsInLocal());
                        System.out.println(tiles.get(playerTurn).getLayoutBounds());
                        System.out.println(tiles.get(playerTurn).getBoundsInParent());
                        gameBoard.getPlayer(playerTurn).setPosition(dieValues + gameBoard.getPlayer(playerTurn).getPosition());
                        finishedTurn = true;
                        playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                        playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                    }
                    }
                }
        );

        //Setting up newTurn button
        Button newTurn = new Button("Next Turn");
        newTurn.setOnAction(e->
        {
            if (!finishedTurn)
            {
                Alert d = new Alert(AlertType.WARNING);
                d.setContentText("Roll dies and move before playing another action!");
                d.show();
            }
            else
            {
                playerInfo.getItems().get(playerTurn).setStroke(Color.BLACK);
                playerInfo.getItems().remove(playerTurn);
                playerInfo.getItems().add(playerTurn,getPlayerInfo(playerTurn));
                if (playerTurn+1 >= gameBoard.getPlayerNum())
                {
                    playerTurn = 0;
                }
                else
                {
                    playerTurn += 1;
                }
                playerInfo.getItems().get(playerTurn).setStroke(playerInformation[playerTurn].playerColor);
                playerInfo.refresh();
                playerTurnText.setText(("Player Turn:  " +(playerTurn + 1)));
                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                finishedTurn = false;
            }
        });

        //Setting up Build button
        Button Build = new Button("Build");
        Build.setPrefSize(100,50);
        Build.setOnAction(e ->
        {
            if(finishedTurn)
            {
                //TODO: Sprint 3 Buy Building
            }
        });

        //Setting up HBox to finalize the controls
        HBox final_control = new HBox(newTurn,move,Buy,Build);
        final_control.setAlignment(Pos.CENTER);
        final_control.setSpacing(100);
        return final_control;
    }

    /**
     * The function getPlayerInfo is used to return all the information that may be interesting about the player, such as:
     * - Amount of Money
     * - Amount of buildings owned
     * - Name of the user
     * - If the User is a CPU or Not
     * @return Returns a ListView for all the player information
     */
    private Text getPlayerInfo(int playerNo)
    {
        return new Text("Player Number: " + playerNo  + "\n" +
                "Player Deposit: " + gameBoard.getPlayer(playerNo).getBalance() + "\n" +
                "Number of properties owned: " + gameBoard.getPlayer(playerNo).getProperties().size() + "\n");
    }

    /**
     * the function mainMenu() is used to create a stage\scene that acts as main Menu of Property Tycoon
     * so far the menu has:
     *  3 buttons
     *  One Animated Title
     */
    public void mainMenu()
    {
        //Creating stage for the mainMenu
        Stage mainMenu = new Stage();
        //Creating exit button
        Button exit = new Button("Exit");
        exit.setOnAction( e -> System.exit(0));
        //Main Text set up
        Text main_Text = createText("Main Menu",50,Color.BLACK,"arial");
        BorderPane.setAlignment(main_Text,Pos.CENTER);
        BorderPane main = new BorderPane();
        //Creating Buttons
        Button quick_game_button = new Button("Quick Game");
        //Style of Button Gotten from:
        //http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
        quick_game_button.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
                    -fx-background-color:\s
                        linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),
                        #9d4024,
                        #d86e3a,
                        radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);
                    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                    -fx-font-weight: bold;
                    -fx-font-size: 8em;""");
        quick_game_button.setPrefWidth(500);
        quick_game_button.setPrefHeight(200);
        quick_game_button.setOnAction(e ->
        {
            mainMenu.close();
            playerSelection();
        });
        Button settings_button = new Button("Settings");
        settings_button.setPrefWidth(500);
        settings_button.setPrefHeight(200);
        settings_button.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        settings_button.setStyle("""
                -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                -fx-background-radius: 8;
                -fx-background-color:\s
                    linear-gradient(from 0% 93% to 0% 100%, #a34313 0%, #903b12 100%),
                    #9d4024,
                    #d86e3a,
                    radial-gradient(center 50% 50%, radius 100%, #d86e3a, #c54e2c);
                -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                -fx-font-weight: bold;
                -fx-font-size: 8em;""".indent(4));
        settings_button.setOnAction(e ->
                {
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
        mainMenu.show();
    }

    public void playerSelection()
    {
        BorderPane mainPane = new BorderPane();
        Text title = createText("Select your Player!",40,Color.RED,"arial");
        BorderPane.setAlignment(title,Pos.CENTER);
        mainPane.setTop(title);
        HBox playerWindow = playerWindows();
        playerWindow.setPadding(new Insets(50));
        playerWindow.setAlignment(Pos.CENTER);
        mainPane.setCenter(playerWindow);
        Button startGameButton = new Button("Start Game!");
        startGameButton.setPrefSize(100,90);
        BorderPane.setMargin(startGameButton,new Insets(100));
        BorderPane.setAlignment(startGameButton,Pos.CENTER);
        mainPane.setBottom(startGameButton);
        Stage playerSelection = new Stage();
        playerSelection.setScene(new Scene(mainPane,1500,1000));
        startGameButton.setOnAction(e ->
        {
          playerSelection.close();
          setupPlaySession();
          gameBoard();
        });
        playerSelection.show();
    }

    public HBox playerWindows()
    {
        player_Index = new HashMap<>();
        AtomicInteger playerNumber = new AtomicInteger();
        String[] colors_String = new String[5];
        colors_String[0] = "blue";
        colors_String[1] = "orange";
        colors_String[2] = "red";
        colors_String[3] = "green";
        colors_String[4] = "purple";
        Color[] colors_Color = new Color[5];
        colors_Color[0] = Color.BLUE;
        colors_Color[1] = Color.ORANGE;
        colors_Color[2] = Color.RED;
        colors_Color[3] = Color.GREEN;
        colors_Color[4] = Color.PURPLE;
        boolean[] selected = new boolean[5];
        HBox playerWindows = new HBox();
        Button[] playerWindowArray = new Button[5];
        for (int i = 0; i < 5; i++)
        {
            selected[i] = false;
            Button playerWindow = new Button(String.valueOf(i));
            playerWindow.setPrefSize(100,100);
            playerWindow.setPadding(new Insets(30));
            playerWindow.setStyle("""
                    
                        -fx-background-color:transparent ;
                        -fx-background-radius:0;
                        -fx-border-color:gray;
                        -fx-border-width: 10 10 10 10;
                    """);
            int colorNum = i;
            playerWindow.setOnAction(e ->
            {
                if (!selected[colorNum])
                {
                    playerWindow.setStyle("\n" +
                            "    -fx-background-color:transparent ;\n" +
                            "    -fx-background-radius:0;\n" +
                            "    -fx-border-color:" + colors_String[colorNum] +";\n" +
                            "    -fx-border-width: 10 10 10 10;\n");
                  selected[colorNum] = true;
                  player_Index.put(Integer.valueOf(String.valueOf(playerNumber)), colors_Color[colorNum]);
                  playerNumber.getAndIncrement();
                }
                else
                {
                    playerWindow.setStyle("""
                    
                        -fx-background-color:transparent ;
                        -fx-background-radius:0;
                        -fx-border-color:gray;
                        -fx-border-width: 10 10 10 10;
                    """);
                    selected[colorNum] = false;
                    player_Index.remove(Integer.valueOf(String.valueOf(playerNumber)));
                    playerNumber.getAndDecrement();
                }
            });
            playerWindowArray[i] = playerWindow;
            playerWindows.getChildren().add(playerWindowArray[i]);
        }
        return playerWindows;
    }

    /**
     *
     */
    public void setupPlaySession()
    {
       gameBoard = new Board();
       playerInformation = new PlayerInformation[player_Index.size()];
       for (int i = 0; i < player_Index.size(); i++)
       {
           int playerNum = i;
           gameBoard.addPlayer(new HumanPlayer());
           gameBoard.getPlayer(i).addMoney(1000);
           playerInformation[i] = new PlayerInformation(player_Index.get(i),i);
           playerInformation[i].getPlayerToken().setOnMouseClicked(e ->
           {
               Alert listOfBuildings = new Alert(AlertType.INFORMATION);
               listOfBuildings.setHeaderText("Property List Of: " + (playerInformation[playerNum].getPlayerNumber()+1));
               listOfBuildings.setContentText(getPropertyString(playerNum));
               listOfBuildings.show();
           });
       }
    }

    public String getPropertyString(int playerNum)
    {

        StringBuilder list_of_properties = new StringBuilder();
        for(int i = 0; i < gameBoard.getPlayer(playerNum).getProperties().size(); i++)
        {
            list_of_properties.append((i+1) + " : " + gameBoard.getPlayer(playerNum).getProperties().get(i) + "\n");
        }
        return String.valueOf(list_of_properties);
    }

    /**
     * The createBoard() method returns a gridPane containing a board with its tiles.
     * It also contains two boxes representing the dices
     *
     * @return Returns a grid pane, which represents the gaming board with all of its tiles.
     */
    public GridPane createBoard()
    {
        HumanPlayer bank = new HumanPlayer();
        cardInfo_Text = new Text[41];
        Property[] gameTiles = new Property[41];
        //count is used to keep track of the png in the Base folder
        int count = 1;
        //Creating grid pane to store the board
        GridPane gridPane = new GridPane();

        //Top side of the board (0,i)
        for(int i = 0; i < 11; i++)
        {
            gameTiles[count-1] = new Property();
            gameTiles[count-1].setPrice(100);
            gameTiles[count-1].setOwner(bank);
            //Store image in Image tile
            Image tile = new Image("file:resources/Base/" + count + ".png");
            //String location used to test button functionality
            String location = 0 + "  " + i + "\nName: " + tile.getUrl();
            ImageView set = new ImageView(tile);
            set.setFitHeight(80);
            set.setFitWidth(80);
            //Setting up the Button for each tile
            count = getCount(count, gridPane, 0, i, tile, location, set);
        }

        //Right side of the board (i,9)
        for(int i = 1; i < 10; i++)
        {

            gameTiles[count-1] = new Property();
            gameTiles[count-1].setPrice(100);
            gameTiles[count-1].setOwner(bank);
            //Store image in Image tile
            Image tile = new Image("file:resources/Base/" + count + ".png");
            //String location used to test button functionality
            String location = i + "  " + 9 + "\nName: " + tile.getUrl();
            ImageView set = new ImageView(tile);
            set.setFitHeight(80);
            set.setFitWidth(80);
            //Setting up the Button for each tile
            count = getCount(count, gridPane, i, 10, tile, location, set);
        }

        //Bottom side of the board
        for(int i = 10; i >= 0; i--)
        {
            gameTiles[count-1] = new Property();
            gameTiles[count-1].setPrice(100);
            gameTiles[count-1].setOwner(bank);
            //Store image in Image tile
            Image tile = new Image("file:resources/Base/" + count + ".png");
            //String location used to test button functionality
            String location = 9 + "  " + i + "\nName: " + tile.getUrl();
            ImageView set = new ImageView(tile);
            set.setFitHeight(80);
            set.setFitWidth(80);
            //Setting up the Button for each tile
            count = getCount(count, gridPane, 10, i, tile, location, set);

        }

        //Left side of the board
        for(int i = 9; i > 0; i--)
        {
            gameTiles[count-1] = new Property();
            gameTiles[count-1].setOwner(bank);
            gameTiles[count-1].setPrice(100);
            //Store image in Image tile
            Image tile = new Image("file:resources/Base/" + count + ".png");
            //String location used to test button functionality
            String location = i + "  " + 0 + "\nName: " + tile.getUrl();
            ImageView set = new ImageView(tile);
            set.setFitHeight(80);
            set.setFitWidth(80);
            //Setting up the Button for each tile
            count = getCount(count, gridPane, i, 0, tile, location, set);
        }
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
               // The following if statement represents two card slots
                if (i == 2 && j == 4)
               {
                   Button fake = new Button("Card Slot");
                   fake.setOnAction(e ->
                   {
                       Alert a = new Alert(AlertType.CONFIRMATION);
                       a.show();
                   });
                   fake.setPrefSize(70,100);
                   gridPane.add(fake,2,4);
               }
               // The following if statement represents the second card slot in the middle
               // of the board
               else if (i == 7 && j == 4)
               {
                   Button fake = new Button("Card Slot 2");
                   fake.setPrefSize(70,100);
                   fake.setOnAction(e ->
                   {
                       Alert a = new Alert(AlertType.CONFIRMATION);
                       a.show();
                   });
                   gridPane.add(fake,7,4);
               }

               // The following if statement represents the first dice
               else if (i == 3 && j == 8)
               {
                   ImageView face1 = new ImageView(facePNG.get(dices.getDiceValues().get(0)));
                   dice1.setGraphic(face1);
                   face1.setFitHeight(60);
                   face1.setFitWidth(60);
                   dice1.setStyle("-fx-background-color: Transparent");
                   dice1.setPadding(Insets.EMPTY);
                   gridPane.add(dice1,3,8);
               }

               // The following if statement represents the second dice
               else if (i == 6 && j == 8)
               {
                   ImageView face = new ImageView(facePNG.get(dices.getDiceValues().get(1)));
                   dice2.setGraphic(face);
                   face.setFitHeight(60);
                   face.setFitWidth(60);
                   dice2.setStyle("-fx-background-color: Transparent");
                   dice2.setPadding(Insets.EMPTY);
                   gridPane.add(dice2,6,8);
               }

               //The else statement represents the blank spots in the middle of the board
               else
               {
                   Button empty = new Button();
                   empty.setStyle("-fx-background-color: #9a3f3f");
                   empty.setPadding(Insets.EMPTY);
                   gridPane.add(empty,i,j);
               }
            }
        }
        gameBoard.setTiles(gameTiles);
        return gridPane;
    }

    /**
     * This method is simply used to save some space.
     * @param count
     * @param gridPane
     * @param i
     * @param j
     * @param tile
     * @param location
     * @param set
     * @return
     */
    private int getCount(int count, GridPane gridPane, int i, int j, Image tile, String location, ImageView set) {
        int cardNum = count;
        Button insert = new Button();
        insert.setOnAction(e->
        {
            if (!inspectWindow)
            {
            inspectWindow = true;
            Rectangle cardInfo = new Rectangle(0,0,500,1000);
            cardInfo_Text[cardNum] = new Text();
            StackPane cardStack = new StackPane(cardInfo,cardInfo_Text[cardNum]);
            cardInfo.setFill(new ImagePattern(tile));
            Scene cardInfoScene = new Scene(cardStack,300,400);
            Stage cardStage = new Stage();
            cardStage.setScene(cardInfoScene);
            cardStage.show();
            cardStack.setOnMouseClicked(a ->
            {
                    cardInfo.setFill(Color.WHITE);
                    updateCardInfo(cardNum);
                    cardStack.setOnMouseClicked(b ->
                    {
                        inspectWindow = false;
                        cardStage.close();
                    });
            });
            }
        });
        insert.setStyle("-fx-background-color: Transparent");
        insert.setGraphic(set);
        insert.setPadding(Insets.EMPTY);
        gridPane.add(insert,i,j);
        tiles.add(insert);
        count++;
        return count;
    }

    public void updateCardInfo(int cardNum)
    {
        cardInfo_Text[cardNum].setText("Property Name:"  + ((Property) gameBoard.getTiles()[cardNum]).getName()  +
                '\n' + "Price of Property: " + ((Property) gameBoard.getTiles()[cardNum]).getPrice() +
                '\n' + "Owner: " + ((Property) gameBoard.getTiles()[cardNum]).getOwner() + '\n' );
        cardInfo_Text[cardNum].setWrappingWidth(100);
    }
    /**
     * The dice() function is used to return an arrayList containing all the dice faces
     *
     * @return ArrayList that returns 6 dice faces as Image variable
     */
     public ArrayList<Image> getDiceFaces()
     {
         //ArrayList used to save all the faces of the dice
         ArrayList<Image> diceFaces = new ArrayList<>();
         for (int i = 0; i<6; i++)
         {
             diceFaces.add(new Image("file:resources/dice/" + (i+1) + ".png"));
         }
         return diceFaces;
     }

    /**
     * The function createText() is used to simplify the creation of text in the program
     * @param content String of the new Text
     * @param size Size of the font
     * @param col colour of the font
     * @param font font name for the new Text
     * @return Returns Text with all the correct attributes
     */
    public Text createText(String content, int size, Color col, String font)
    {
        //createText is used to simplify the creation of any text
        Text returnText = new Text(content);
        returnText.setStyle(String.format("-fx-font: %d %s",size,font));
        returnText.setSelectionFill(col);
        return returnText;
    }

    /**
     * getCoordinates() is used to get the coordinates of the desired tile. It is useful to easily move the player
     * to the desired location. (currently not working as intended)
     *
     * @param axis The axis needed (X or Y)
     * @param tileNum The number of the tile
     * @return Returns the X or Y coordinate
     */
    public double getCoordinates(char axis, int tileNum, int playerNumber)
    {
        double location = 0;
        if (axis == 'X')
        {
                location =  board.getChildren().get(tileNum).getLayoutX() - 440 + (playerNumber*10);
        }
        else if (axis == 'Y')
        {
                location =  board.getChildren().get(tileNum).getLayoutY() - 440+ (playerNumber*10);
        }
        return location;
    }
}