package GUI;

import backend.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Objects;
//TODO: Implement Board Class
//TODO: Implement Player Class
//TODO: Implement Dice Class
//TODO: Implement Tile Class
//TODO: Implement Utility Class
public class GUI extends Application {

    //Backend
    private final Tile[] tiles_for_board = new Tile[41];
    private int playerTileNum;
    private int player_Index;

    private StackPane final_main;
    private final Dice dices = new Dice(2,0,5);
    private final Button dice1 = new Button();
    private boolean inspectWindow = false;
    private final ArrayList<Button> tiles = new ArrayList<>();
    private final Button dice2 = new Button();
    private final Label inspect = new Label();
    private final ArrayList<Image> facePNG = dice();
    private final Circle playerTest = new Circle();
    private  final Stage gameBoard_GUI = new Stage();
    private Board gameBoard = new Board();
    private Text moneyUser;
    private Text playerTurnText;
    private ListView<String> playerInfo = new ListView<>();
    private int playerTurn = 0;
    private final GridPane board = createBoard();
    private StackPane boardAndPlayer;
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
        moneyUser = new Text(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
        moneyUser.setStyle("""
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
        moneyUser.setTextAlignment(TextAlignment.CENTER);

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
        playerTest.setRadius(20);
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
        playerInfo = getPlayerInfo(player_Index);
        //Start selection with the first element in the list
        playerInfo.getSelectionModel().select(0);
        playerInfo.setMouseTransparent(true);

        VBox playerList = new VBox(players,playerInfo);
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
        VBox bankSide = new VBox(idk, playerTurnText,moneyCounter,moneyUser);
        bankSide.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        bankSide.setAlignment(Pos.CENTER);
        bankSide.setSpacing(30);

        System.out.println(playerInfo.getItems());
        //Setting up the main BorderPane of the scene
        BorderPane main = new BorderPane();
        main.setRight(bankSide);
        main.setBottom(controls);
        main.setTop(top);
        board.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        main.setCenter(board);
        main.setLeft(playerList);
        final_main = new StackPane(main,playerTest);
        BorderPane.setAlignment(bankSide,Pos.CENTER_RIGHT);

        Scene finalScene = new Scene(final_main,1500,1000);
        //Creating a new Scene
        gameBoard_GUI.setScene(finalScene);
        gameBoard_GUI.showAndWait();
    }

    /**
     * controlButtons is used to create an HBox containing all the controls the user is going to have
     * @return Returning the HBox containing all the control buttons for the player
     */
    private HBox controlButtons()
    {
        //Pause transition taken from https://stackoverflow.com/questions/55768170/temporarily-change-color-of-a-button-when-clicked

        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyUser.setFill(Color.BLACK));

        //Money up button (for Testing)
        Button moneyUp = new Button("Money Up");
        moneyUp.setOnAction(e ->
        {
            //Start of animation
            e.consume();
            moneyUser.setFill(Color.GREEN);
            transition.playFromStart();
            gameBoard.getPlayer(playerTurn).addMoney(100);
            moneyUser.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));

        });

        //Money up button (for Testing)
        Button moneyDown = new Button("Money Down");
        moneyDown.setOnAction(e ->
        {
            //Start of animation
            e.consume();
            moneyUser.setFill(Color.RED);
            transition.playFromStart();

            //Adding money and updating text
            gameBoard.getPlayer(playerTurn).removeMoney(100);
            moneyUser.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
        });

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
                int moneyGiventoBank = gameBoard.purchase(player_Index,gameBoard.getPlayer(playerTurn).getPosition());
                moneyUser.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
            }
        });
        //Setting up move button
        Button move = new Button("Move");
        move.setPrefSize(100,50);
        move.setOnAction(e ->
                {
                    if (!finishedTurn) {
                    dices.rollDice();
                    ImageView first = new ImageView(facePNG.get(dices.getDiceValues().get(0)));
                    first.setFitWidth(60);
                    first.setFitHeight(60);
                    dice1.setGraphic(first);
                    ImageView second = new ImageView(facePNG.get(dices.getDiceValues().get(1)));
                    second.setFitWidth(60);
                    second.setFitHeight(60);
                    dice2.setGraphic(second);
                    if (Objects.equals(dices.getDiceValues().get(0), dices.getDiceValues().get(1)))
                    {
                        finishedTurn = false;
                    }
                    else
                    {
                        int dieValues = dices.getDiceValues().get(0) + dices.getDiceValues().get(1);
                        gameBoard.getPlayer(playerTurn).setPosition(dieValues);
                        System.out.println(gameBoard.getPlayer(playerTurn).getPosition());
                        finishedTurn = true;
                        playerTest.setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition()));
                        playerTest.setTranslateY(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition()));
                    }
                    }
                }
        );

        //Setting up newTurn button
        Button newTurn = new Button("Next Turn");
        newTurn.setOnAction(e->
        {
            if (playerTurn+1 >= playerInfo.getItems().size())
            {
                playerTurn = 0;
            }
            else
            {
                playerTurn += 1;
            }
            playerInfo.getSelectionModel().select(playerTurn);
            playerInfo = getPlayerInfo(player_Index);
            playerTurnText.setText(("Player Turn:  " +(playerTurn + 1)));
            moneyUser.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
            finishedTurn = false;
        });

        //Setting up Skip button
        Button Skip = new Button("Skip");
        Skip.setPrefSize(100,50);

        //Setting up Build button
        Button Build = new Button("Build");
        Build.setPrefSize(100,50);

        //Setting up HBox to finalize the controls
        HBox final_control = new HBox(newTurn,move,Buy,Skip,Build,moneyUp,moneyDown);
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
    private ListView<String> getPlayerInfo(int playerNo)
    {
        ListView<String> player = new ListView<>();
        for (int j = 0; j<playerNo; j++)
            {
                String playerInfos = "Player Number: " + (j+1) + "\n" +
                        "Player Deposit: " + gameBoard.getPlayer(j).getBalance() + "\n" +
                        "Number of properties owned: " + gameBoard.getPlayer(j).getProperties().size() + "\n";
                player.getItems().add(playerInfos);
            }
        return player;
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
        String[] colors = new String[5];
        boolean[] selected = new boolean[5];
        colors[0] = "blue";
        colors[1] = "orange";
        colors[2] = "red";
        colors[3] = "green";
        colors[4] = "purple";
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
                            "    -fx-border-color:" + colors[colorNum] +";\n" +
                            "    -fx-border-width: 10 10 10 10;\n");
                  selected[colorNum] = true;
                  player_Index++;
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
                    player_Index--;
                }
            });
            playerWindowArray[i] = playerWindow;
            playerWindows.getChildren().add(playerWindowArray[i]);
        }
        return playerWindows;
    }
    public void setupPlaySession()
    {
       gameBoard = new Board();
       playerTileNum = 0;
       for (int i = 0; i < player_Index; i++)
       {
           gameBoard.addPlayer(new HumanPlayer());
           gameBoard.getPlayer(i).addMoney(1000);
       }
    }
    /**
     * The createBoard() method returns a gridPane containing a board with its tiles.
     * It also contains two boxes representing the dices
     *
     * @return Returns a grid pane, which represents the gaming board with all of its tiles.
     */
    public GridPane createBoard()
    {
        Tile[] gameTiles = new Tile[41];
        //count is used to keep track of the png in the Base folder
        int count = 1;
        //Creating grid pane to store the board
        GridPane gridPane = new GridPane();
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                if (i == 0 && j == 0 || i == 9 && j == 0 ||i == 0 && j == 9 ||i == 9 && j == 9)
                {
                    gameTiles[count] = new Tile();
                    //Store image in Image tile
                    Image tile = new Image("file:resources/Base/" + count + ".png");
                    //String location used to test button functionality
                    String location = i + "  " + j + "\nName: " + tile.getUrl();
                    ImageView set = new ImageView(tile);
                    set.setFitHeight(80);
                    set.setFitWidth(80);
                    //Setting up the Button for each tile
                    count = getCount(count, gridPane, i, j, tile, location, set);
                }
               //The following if statement represents the border of the board
               else if(i == 0 || j == 0 || i == 9 || j == 9)
               {
                   gameTiles[count] = new Tile();
                   //Store image in Image tile
                   Image tile = new Image("file:resources/Base/" + count + ".png");
                   //String location used to test button functionality
                   String location = i + "  " + j + "\nName: " + tile.getUrl();
                   ImageView set = new ImageView(tile);
                   set.setFitHeight(80);
                   set.setFitWidth(80);
                   //Setting up the Button for each tile
                   count = getCount(count, gridPane, i, j, tile, location, set);
               }
               // The following if statement represents two card slots
               else if (i == 2 && j == 4)
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
        Button insert = new Button();
        insert.setOnAction(e->
        {
            if (!inspectWindow)
            {
            inspectWindow = true;
            Rectangle cardInfo = new Rectangle(0,0,500,1000);
            Text card = new Text();
            StackPane cardStack = new StackPane(cardInfo,card);
            cardInfo.setFill(new ImagePattern(tile));
            Scene cardInfoScene = new Scene(cardStack,300,400);
            Stage cardStage = new Stage();
            cardStage.setScene(cardInfoScene);
            cardStage.show();
            cardStack.setOnMouseClicked(a ->
            {
                    cardInfo.setFill(Color.WHITE);
                    card.setText("Tile Name: ???" + '\n' + "Owner: ???" + '\n' + "Building Level: ???" +
                                 '\n' + location);
                    card.setWrappingWidth(100);
                    cardStack.setOnMouseClicked(b ->
                    {
                        inspectWindow = false;
                        cardStage.close();
                    });
            });
            inspect.setWrapText(true);
            inspect.setText("Tile Name: ???" + '\n' + "Owner: ???" + '\n' + "Building Level: ???"+
                    '\n' + location);
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

    /**
     * The dice() function is used to return an arrayList containing all the dice faces
     *
     * @return ArrayList that returns 6 dice faces as Image variable
     */
     public ArrayList<Image> dice()
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
    public double getCoordinates(char axis, int tileNum)
    {
        Button currTile = tiles.get(tileNum);
        System.out.println(currTile.getLayoutX());
        System.out.println(currTile.getLayoutY());
        double location = 0;
        Bounds screenBounds = final_main.localToScreen(board.localToScreen(currTile.getBoundsInLocal()));
        if (axis == 'X')
        {
           location =  screenBounds.getMinX();
        }
        else if (axis == 'Y')
        {
            location = screenBounds.getMinY();
        }
        return location;
    }
}