package GUI;

import backend.Board;
import backend.Dice;
import backend.Exception.*;
import backend.Player.HumanPlayer;
import backend.Tiles.Tile;
import backend.Tiles.TileBuilding;
import backend.Tiles.TileProperty;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    PlayerInformation[] playerInformation;
    Button currPlayerIcon;

    //Dice Variables
    private final Dice dices = new Dice(2,1,6);
    private final Button dice1 = new Button();
    private final Button dice2 = new Button();
    private final ArrayList<Image> facePNG = getDiceFaces();

    private boolean inspectWindow = false;
    private  final Stage gameBoard_GUI = new Stage();
    private Board gameBoard = new Board();
    private Text moneyOfPlayer;
    private Text playerTurnText;
    private ListView<Text> playerInfo = new ListView<>();
    private int playerTurn = 0;
    private boolean finishedTurn = false;
    private String[] colors_String;
    private Stage auctionWindow;
    private boolean playerBoughtProperty = false;

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
        gameBoard.setTiles(createTiles());
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
        Text players = createText("LeaderBoard",40,Color.BLACK,"arial");

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
        HBox dice = dices();
        dice.setAlignment(Pos.CENTER);
        VBox bankSide = new VBox(dice,currPlayerIcon, playerTurnText,moneyCounter, moneyOfPlayer);
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
        //Player
        StackPane final_main = new StackPane(main);
        for (PlayerInformation information : playerInformation)
        {
            final_main.getChildren().add(information.getPlayerToken());
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
     *
     * @return
     */
    public Node selectPlayer()
    {
        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));
        ListView<TileBuilding> playerBuildings = new ListView<>();
        playerBuildings.setOnMouseClicked(e ->
        {
            TileBuilding chosenBuilding = playerBuildings.getSelectionModel().getSelectedItem();
            int tileIndex = getTileIndex(chosenBuilding);
            try
            {
                gameBoard.sellToBank(playerTurn,tileIndex);

                //Change GUI (Money Counter)
                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                moneyOfPlayer.setFill(Color.GREEN);
                transition.playFromStart();
                playerBoughtProperty = true;
            }
            catch (OwnershipException ex)
            {
                Alert doesNotHaveOwnership = new Alert(AlertType.WARNING);
                doesNotHaveOwnership.setContentText("Not Owner");
                doesNotHaveOwnership.show();
            }
            catch (PropertyDevelopedException ex)
            {
                Alert alreadyDeveloped = new Alert(AlertType.WARNING);
                alreadyDeveloped.setContentText("Not Developed");
                alreadyDeveloped.show();
            }
        });
        for (int i = 0; i < gameBoard.getPlayer(playerTurn).getProperties().size();i++)
        {
            playerBuildings.getItems().add((TileBuilding) gameBoard.getPlayer(playerTurn).getProperties().get(i));
        }
        BorderPane mainPane = new BorderPane();
        GridPane playerToChoose = new GridPane();
        Text title = createText("Choose building to trade with the bank!",50,Color.BLACK,"arial");
        BorderPane.setAlignment(title,Pos.CENTER);
        mainPane.setTop(title);
        for (int i = 0; i < gameBoard.getPlayerNum(); i++)
        {
            if ((playerTurn == i))
            {
                Button playerIcon = new Button();
                playerIcon.setMinWidth(100);
                playerIcon.setMinHeight(100);
                playerIcon.setStyle("" +
                        "-fx-background-color: " + playerInformation[i].getPlayerColor_String());
                playerIcon.setAlignment(Pos.CENTER);
                playerToChoose.add(playerIcon,i,0);
            }
        }
        playerToChoose.setPadding(new Insets(100));
        playerToChoose.add(playerBuildings,playerTurn,1);
        playerToChoose.setPadding(new Insets(50));
        mainPane.setCenter(playerToChoose);
        playerToChoose.setAlignment(Pos.CENTER);
        return mainPane;
    }

    /**
     *
     * @param chosenBuilding
     * @return
     */
    private int getTileIndex(TileBuilding chosenBuilding)
    {
        Tile[] tiles = gameBoard.getTiles();
        for (int i = 0; i < tiles.length; i++)
        {
            if (tiles[i] == chosenBuilding)
            {
                return i;
            }
        }
        return -1;
    }


    public Node tradeP2P(int playerToTradeWith)
    {
        BorderPane mainPane = new BorderPane();
        GridPane playerAndProperties = new GridPane();
        Button playerIcon = new Button();
        playerIcon.setOnAction(e ->
        {
        });
        playerIcon.setMinWidth(50);
        playerIcon.setStyle("" +
                "-fx-background-color: " + playerInformation[playerToTradeWith].playerColor_String);
        ListView<String> playerIconProperties = new ListView<>();
        for (int i = 0; i < gameBoard.getPlayer(playerToTradeWith).getProperties().size(); i++)
        {
            playerIconProperties.getItems().add(gameBoard.getPlayer(playerToTradeWith).getProperties().get(i).toString());
        }


        Button playerIcon2 = new Button();
        playerIcon2.setOnAction(e ->
        {
        });
        playerIcon2.setMinWidth(50);
        playerIcon2.setStyle("" +
                "-fx-background-color: " + playerInformation[playerTurn].playerColor_String);

        ListView<String> playerIcon2Properties = new ListView<>();
        for (int i = 0; i < gameBoard.getPlayer(playerTurn).getProperties().size(); i++)
        {
            playerIcon2Properties.getItems().add(gameBoard.getPlayer(playerTurn).getProperties().get(i).toString());
        }

        playerAndProperties.add(playerIcon,0,0);
        playerAndProperties.add(playerIconProperties,0,1);
        playerAndProperties.add(playerIcon2,1,0);
        playerAndProperties.add(playerIcon2Properties,1,1);
        mainPane.setCenter(playerAndProperties);
        return mainPane;
    }


    public Node  auctionNode()
    {
        ListView<TextField> playerBid = new ListView<>();
        BorderPane mainAuctionPane = new BorderPane();
        Image tile = new Image("file:resources/Base/" + gameBoard.getPlayer(playerTurn).getPosition() + ".png");
        ImageView auctionPropertyIcon = new ImageView(tile);
        GridPane playerAndText = new GridPane();
        Text titleAuction = createText("Auction House!",100,Color.BLACK,"arial");
        VBox titleAndProperty = new VBox(titleAuction,auctionPropertyIcon);
        mainAuctionPane.setTop(titleAndProperty);
        for (int i = 0; i < gameBoard.getPlayerNum();i++)
        {
            Button playerIcon = new Button();
            playerIcon.setMinWidth(50);
            playerIcon.setStyle("" +
                    "-fx-background-color: " + playerInformation[i].getPlayerColor_String());
            playerAndText.add(playerIcon,i,0);
        }
        for (int i = 0; i < gameBoard.getPlayerNum();i++)
        {
            TextField answer = new TextField();
            answer.setEditable(false);
            playerBid.getItems().add(answer);
            playerAndText.add(answer,i,1);
        }
        playerBid.getSelectionModel().select(0);
        playerBid.getSelectionModel().getSelectedItem().setEditable(true);
        HBox controlsAuction = new HBox();
        Button nextPlayer =  new Button("Next Player!");
        nextPlayer.setOnAction(e ->
        {
            //Check if player has enough funds to place bid
            try {
                gameBoard.auctionMakeBid(playerBid.getSelectionModel().getSelectedIndex(),Integer.parseInt(playerBid.getItems().get(playerBid.getSelectionModel().getSelectedIndex()).getText()));
                playerBid.getSelectionModel().getSelectedItem().setEditable(false);
                playerBid.getSelectionModel().selectNext();
                playerBid.getSelectionModel().getSelectedItem().setEditable(true);
            } catch (InsufficientFundsException ex) {
                Alert bidTooHigh = new Alert(AlertType.WARNING);
                bidTooHigh.setContentText("Please bid only what you own!");
                bidTooHigh.show();
                playerBid.getSelectionModel().getSelectedItem().setEditable(false);
                playerBid.getSelectionModel().selectPrevious();
                playerBid.getSelectionModel().getSelectedItem().setEditable(true);
            }
            if (playerBid.getSelectionModel().getSelectedIndex()+1 == gameBoard.getPlayerNum() && !Objects.equals(playerBid.getSelectionModel().getSelectedItem().getText(), ""))
            {
                int higherPlayer = gameBoard.auctionHighestBid()[2];
                int higherBid = gameBoard.auctionHighestBid()[0];
                gameBoard.getPlayer(higherPlayer).addProperty((TileProperty) gameBoard.getTiles()[gameBoard.getPlayer(playerTurn).getPosition()]);
                gameBoard.getPlayer(higherPlayer).removeMoney(higherBid);
                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(higherPlayer).getBalance()));
                Alert auctionWinner = new Alert(AlertType.INFORMATION);
                auctionWinner.setContentText("Winner of Auction is: " + (playerInformation[higherPlayer].getPlayerNumber() + 1));
                auctionWinner.show();
                auctionWindow.close();
                }
        });
        controlsAuction.getChildren().add(nextPlayer);
        mainAuctionPane.setBottom(controlsAuction);
        titleAuction.setTextAlignment(TextAlignment.CENTER);
        titleAndProperty.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(titleAndProperty,Pos.CENTER);
        mainAuctionPane.setCenter(playerAndText);
        controlsAuction.setAlignment(Pos.CENTER);
        playerAndText.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(playerAndText, Pos.CENTER);
        return mainAuctionPane;
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
                    Stage mortgage = new Stage();
                    Scene mortgageScene = createmortgageScene();
                    mortgage.setScene(mortgageScene);
                    mortgage.show();
                }
                else if (((TileBuilding)gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getOwner() != gameBoard.getBank())
                {
                    Alert alreadyOwned = new Alert(AlertType.WARNING);
                    alreadyOwned.setContentText("The Bu");
                }
                else
                {
                    //Pay Price
                    gameBoard.purchase(gameBoard.getPlayer(playerTurn), gameBoard.getBank(),
                                      (TileProperty) gameBoard.getPlayerTile(playerTurn),100);

                    //Set Owner of tile bought
                    ((TileProperty) gameBoard.getTiles()[gameBoard.getPlayer(playerTurn).getPosition()]).setOwner(gameBoard.getPlayer(playerTurn));

                    //Change GUI (Money Counter)
                    moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                    moneyOfPlayer.setFill(Color.RED);
                    transition.playFromStart();
                    playerBoughtProperty = true;
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
            if(!playerBoughtProperty)
            {
                gameBoard.auctionInitialise();
                gameBoard.auctionStart();
                auctionWindow = new Stage();
                auctionWindow.setScene(new Scene((Parent) auctionNode(),800,800));
                auctionWindow.show();
                //https://stackoverflow.com/questions/17003906/prevent-cancel-closing-of-primary-stage-in-javafx-2-2
                auctionWindow.setOnCloseRequest(Event::consume);
            }
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
                playerBoughtProperty = false;
                currPlayerIcon.setStyle("-fx-background-color: " + playerInformation[playerTurn].getPlayerColor_String());
            }
        });

        //Setting up Build button
        Button Build = new Button("Build");
        Build.setPrefSize(100,50);
        Build.setOnAction(e ->
        {
            if(finishedTurn)
            {
                if (gameBoard.getPlayer(playerTurn).getProperties().contains(gameBoard.getPlayerTile(playerTurn)))
                {
                    try {
                        ((TileBuilding) gameBoard.getPlayerTile(playerTurn)).buyHouse(gameBoard.getBank());
                    } catch (PropertyDevelopedException ex) {
                        Alert exceptionBuild = new Alert(AlertType.WARNING);
                        exceptionBuild.setContentText("The Property has max development!");
                        exceptionBuild.show();
                        ex.printStackTrace();
                    } catch (LargeDevelopmentDifferenceException ex) {
                        Alert exceptionBuild = new Alert(AlertType.WARNING);
                        exceptionBuild.setContentText("idk");
                        exceptionBuild.show();
                        ex.printStackTrace();
                    } catch (IsMortgagedException ex) {
                        Alert exceptionBuild = new Alert(AlertType.WARNING);
                        exceptionBuild.setContentText("Building is mortgaged!");
                        exceptionBuild.show();
                        ex.printStackTrace();
                    } catch (InsufficientFundsException ex) {
                        Alert exceptionBuild = new Alert(AlertType.WARNING);
                        exceptionBuild.setContentText("Insufficient Funds!");
                        exceptionBuild.show();
                        ex.printStackTrace();
                    }
                }
            }
        });

        Button trade = new Button("Trade");
        trade.setOnAction(e ->{
            Stage tradeStage = new Stage();
            tradeStage.setScene(new Scene((Parent) selectPlayer(), 800,800));
            tradeStage.show();
        });
        //Setting up HBox to finalize the controls
        HBox final_control = new HBox(newTurn,move,Buy,Build, trade);
        final_control.setAlignment(Pos.CENTER);
        final_control.setSpacing(100);
        return final_control;
    }

    private Scene createmortgageScene()
    {
        BorderPane mainPane = new BorderPane();
        Text idk = new Text("main text");
        mainPane.setCenter(idk);
        return new Scene(mainPane,800,800);
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
        return new Text("Player Number: " + (playerNo + 1)  + "\n" +
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
                    -fx-font-size: 5em;""");
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
                -fx-font-size: 5em;""".indent(4));
        settings_button.setOnAction(e ->
                {
                mainMenu.close();
                settings();
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

    public void settings()
    {
        Stage settings_Stage = new Stage();
        BorderPane mainPane = new BorderPane();
        VBox settingOptions = new VBox();
        settingOptions.setAlignment(Pos.CENTER);
        mainPane.setCenter(settingOptions);
        Button goBack = new Button("Back!");
        goBack.setOnAction(e ->
        {
            settings_Stage.close();
            mainMenu();
        });
        settingOptions.getChildren().add(goBack);
        settings_Stage.setScene(new Scene(mainPane,1500,1000));
        settings_Stage.show();
    }

    public void playerSelection()
    {
        //Creating mainPane for the Stage
        BorderPane mainPane = new BorderPane();

        //Title Set up
        Text title = createText("Select your Player!",40,Color.RED,"arial");
        BorderPane.setAlignment(title,Pos.CENTER);
        mainPane.setTop(title);

        //PlayerWindow Set up
        HBox playerWindow = playerWindows();
        playerWindow.setPadding(new Insets(50));
        playerWindow.setAlignment(Pos.CENTER);
        mainPane.setCenter(playerWindow);

        //Start Game button set up
        Button startGameButton = new Button("Start Game!");
        startGameButton.setPrefSize(100,90);
        BorderPane.setMargin(startGameButton,new Insets(100));
        BorderPane.setAlignment(startGameButton,Pos.CENTER);
        mainPane.setBottom(startGameButton);

        //Creating Stage and setting Scene (Also start game button functionality)
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

    /**
     *
     * playerWindows() creates an HBox with windows for all the possible players
     *
     * @return return the HBox with the five different type of players
     */
    public HBox playerWindows()
    {
        player_Index = new HashMap<>();
        AtomicInteger playerNumber = new AtomicInteger();
        //Colors (String) set up
        colors_String = new String[5];
        colors_String[0] = "blue";
        colors_String[1] = "orange";
        colors_String[2] = "red";
        colors_String[3] = "green";
        colors_String[4] = "purple";

        //Colors (Color) set up
        Color[] colors_Color = new Color[5];
        colors_Color[0] = Color.BLUE;
        colors_Color[1] = Color.ORANGE;
        colors_Color[2] = Color.RED;
        colors_Color[3] = Color.GREEN;
        colors_Color[4] = Color.PURPLE;

        //Creating selected boolean array
        boolean[] selected = new boolean[5];
        HBox playerWindows = new HBox();
        Button[] playerWindowArray = new Button[5];
        for (int i = 0; i < 5; i++)
        {
            selected[i] = false;
            Button playerWindow = new Button();
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
     * setupPlaySession() will run before the game begins. This allows the user to choose the number
     * of player before the start of the game.
     */
    public void setupPlaySession()
    {
       gameBoard = new Board();
       playerInformation = new PlayerInformation[player_Index.size()];

       //Initialising the players for the game session.
       for (int i = 0; i < player_Index.size(); i++)
       {
           int playerNum = i;
           gameBoard.addPlayer(new HumanPlayer());
           gameBoard.getPlayer(i).addMoney(1000);
           playerInformation[i] = new PlayerInformation(player_Index.get(i).toString(),player_Index.get(i),i);
           playerInformation[i].getPlayerToken().setOnMouseClicked(e ->
           {
               Alert listOfBuildings = new Alert(AlertType.INFORMATION);
               listOfBuildings.setHeaderText("Property List Of: " + (playerInformation[playerNum].getPlayerNumber()+1));
               listOfBuildings.setContentText(getPropertyString(playerNum));
               listOfBuildings.show();
           });
       }
       currPlayerIcon = new Button();
       currPlayerIcon.setMinHeight(100);
       currPlayerIcon.setMinWidth(100);
       currPlayerIcon.setStyle("-fx-background-color: " + playerInformation[playerTurn].getPlayerColor_String());
    }

    /**
     *
     * @param playerNum
     * @return
     */
    public String getPropertyString(int playerNum)
    {

        StringBuilder list_of_properties = new StringBuilder();
        for(int i = 0; i < gameBoard.getPlayer(playerNum).getProperties().size(); i++)
        {
            list_of_properties.append(i + 1).append(" : ").append(gameBoard.getPlayer(playerNum).getProperties().get(i)).append("\n");
        }
        return String.valueOf(list_of_properties);
    }

    /**
     *
     * @return
     */
    public Tile[] createTiles()
    {
        Tile[] gameTiles = new Tile[41];
        for (int i = 0; i < 41; i ++)
        {
            gameTiles[i] = new TileBuilding("#0000FF",new ArrayList<>(), 100,1000,100,"idk", gameBoard.getBank(), new ArrayList<>(),false);
        }
        return gameTiles;
    }

    /**
     * The createBoard() method returns a gridPane containing a board with its tiles.
     * It also contains two boxes representing the dices
     *
     * @return Returns a grid pane, which represents the gaming board with all of its tiles.
     */
    public GridPane createBoard()
    {
        cardInfo_Text = new Text[41];
        //count is used to keep track of the png in the Base folder
        int count = 0;
        //Creating grid pane to store the board
        GridPane gridPane = new GridPane();

        //Top side of the board (0,i)
        for(int i = 0; i < 11; i++)
        {
            count = tileCreation(count, gridPane, 0, i,"TileBuilding",50);
        }

        //Right side of the board (i,9)
        for(int i = 1; i < 10; i++)
        {
            //Setting up the Button for each tile
            count = tileCreation(count, gridPane, i, 10,"TileBuilding",50);
        }

        //Bottom side of the board
        for(int i = 10; i >= 0; i--)
        {
            //Setting up the Button for each tile
            count = tileCreation(count, gridPane, 10, i,"TileBuilding",50);

        }

        //Left side of the board
        for(int i = 9; i > 0; i--)
        {
            //Setting up the Button for each tile
            count = tileCreation(count, gridPane, i, 0,"TileBuilding",50);
        }
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                /*
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

                 */
               // The following if statement represents the first dice
               //The else statement represents the blank spots in the middle of the board
               {
                   Button empty = new Button();
                   empty.setStyle("-fx-background-color: #9a3f3f");
                   empty.setPadding(Insets.EMPTY);
                   gridPane.add(empty,i,j);
               }
            }
        }
        //no Jail Slot
        tileCreation(40,gridPane,9,9, "TileFreeParking",50);
        return gridPane;
    }

    /**
     * This method is simply used to save some space.
     */
    private int tileCreation(int count, GridPane gridPane, int i, int j, String tileType,int size) {
        cardInfo_Text[count] = new Text("r");
        //Store image in Image tile
        Image tile = new Image("file:resources/Base/" + count + ".png");
        //String location used to test button functionality
        ImageView set = new ImageView(tile);
        set.setFitHeight(150);
        set.setFitWidth(150);
        //Setting up the Button for each tile
        int cardNum = count;
        Button insert = new Button(((TileProperty)gameBoard.getTile(count)).getName());
        insert.setMinWidth(size);
        insert.setMinHeight(size);
        insert.setOnAction(e->
        {
            if (!inspectWindow)
            {
                VBox cardInfoIDK = new VBox();
                cardInfoIDK.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                        ((TileBuilding) gameBoard.getTile(cardNum)).getHexColour() + " 30%, white 0%);");
                Text cardInfo = updateCardInfo(cardNum);
                cardInfoIDK.getChildren().add(cardInfo);
                inspectWindow = true;
                Scene cardInfoScene = new Scene(cardInfoIDK, 300, 400);
                Stage cardStage = new Stage();
                cardStage.setScene(cardInfoScene);
                cardStage.show();
                cardInfoIDK.setOnMouseClicked(a ->
                {
                    inspectWindow = false;
                    cardStage.close();
                });
            }
        });
        insert.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                ((TileBuilding) gameBoard.getTile(cardNum)).getHexColour() + " 20%, white 0%);\n" +
                "-fx-background-radius: 0");
        gridPane.add(insert,i,j);
        count++;
        return count;
    }

    /**
     *
     */
    public HBox dices ()
    {
        //Preparing dice1
        ImageView face1 = new ImageView(facePNG.get(0));
        dice1.setGraphic(face1);
        face1.setFitHeight(60);
        face1.setFitWidth(60);
        dice1.setStyle("-fx-background-color: Transparent");
        dice1.setPadding(Insets.EMPTY);

        //Preparing dice2
        ImageView face = new ImageView(facePNG.get(0));
        dice2.setGraphic(face);
        face.setFitHeight(60);
        face.setFitWidth(60);
        dice2.setStyle("-fx-background-color: Transparent");
        dice2.setPadding(Insets.EMPTY);

        //return finished HBox
        return new HBox(dice1,dice2);
    }

    /**
     * updateCardInfo() is a method that will update the info of a tile (such as a different owner etc...)
     *
     * @param tileNum the tile needed to be updated
     */
    private Text updateCardInfo(int tileNum)
    {
        cardInfo_Text[tileNum].setText("Property Name:"  + ((TileProperty) gameBoard.getTiles()[tileNum]).getName()  +
                '\n' + "Price of Property: " + ((TileProperty) gameBoard.getTiles()[tileNum]).getPrice() +
                '\n' + "Owner: " + ((TileProperty) gameBoard.getTiles()[tileNum]).getOwner() + '\n' );
        cardInfo_Text[tileNum].setWrappingWidth(100);
        return cardInfo_Text[tileNum];
    }

    /**
     * The dice() function is used to return an arrayList containing all the dice faces
     *
     * @return ArrayList that returns 6 dice faces as Image variable
     */
     private ArrayList<Image> getDiceFaces()
     {
         //ArrayList used to save all the faces of the dice
         ArrayList<Image> diceFaces = new ArrayList<>();
         for (int i = 0; i<6; i++)
         {
             diceFaces.add(new Image("file:resources/dice/" + (i+1) + ".png"));
             System.out.println(i);
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
                location =  board.getChildren().get(tileNum).getLayoutX() - 240 + (playerNumber*10);
        }
        else if (axis == 'Y')
        {
                location =  board.getChildren().get(tileNum).getLayoutY() - 240+ (playerNumber*10);
        }
        return location;
    }
}