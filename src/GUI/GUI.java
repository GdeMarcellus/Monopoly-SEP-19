package GUI;

import backend.Board;
import backend.Card;
import backend.Dice;
import backend.Exception.*;
import backend.Loader.JsonLoader;
import backend.Player.AI.AIEvent;
import backend.Player.AI.AIPlayer;
import backend.Player.AI.AIReport;
import backend.Player.HumanPlayer;
import backend.Player.Player;
import backend.Tiles.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GUI extends Application {

    Integer time = 60;
    //Backend
    private HashMap<Integer, Color> player_Index;
    private GridPane board;

    PlayerInformation[] playerInformation;
    Button currPlayerIcon;

    //Dice Variables
    private final Dice dices = new Dice(2,1,6);
    private final Button dice1 = new Button();
    private final Button dice2 = new Button();
    private final ArrayList<Image> facePNG = getDiceFaces();

    private  final Stage gameBoard_GUI = new Stage();
    private Board gameBoard = new Board();
    private Text moneyOfPlayer;
    private Text playerTurnText;
    private ListView<Text> playerInfo = new ListView<>();
    private int playerTurn = 0;
    private boolean finishedTurn = false;
    private Stage auctionWindow;
    private boolean playerBoughtProperty = false;
    private final Button[] tiles = new Button[41];
    private Color[] colors_Color;
    private boolean abridged;
    private Text timeRemaining;
    private Timeline timeline;
    private TextArea aiLogBox;
    private Rectangle2D screenBounds;
    private HashMap<Integer, String> tokenPlayer;
    private AIPlayer aiPlayer;
    private int highestTurn;
    private ArrayList<TextField> auctionArray;
    private Stage mortgageStage;
    private Button newTurn;
    private int rolledDouble;
    private Stage tradeStage;

    public static void main(String []args)
    {
        launch();
    }

    public void start(Stage primaryStage)
    {
        primaryStage.setResizable(false);
        screenBounds = Screen.getPrimary().getBounds();
        start_screen();
    }

    /**
     * The start_screen method, represents the first screen the user will see
     * It contains a flashy title, and players are allowed to move to the next stage
     * By pressing the Enter button
     */
    private void start_screen()
    {
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        //Colors (Color) set up
        colors_Color = new Color[5];
        colors_Color[0] = Color.BLUE;
        colors_Color[1] = Color.ORANGE;
        colors_Color[2] = Color.RED;
        colors_Color[3] = Color.GREEN;
        colors_Color[4] = Color.PURPLE;

        //Setting up Stages
        Stage introduction = new Stage();
        introduction.setResizable(false);
        introduction.setFullScreen(false);
        gameBoard_GUI.setResizable(false);
        gameBoard_GUI.setResizable(false);

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
       
        introduction.setScene(buttonScene = new Scene(mainStage,screenBounds.getMaxX(), screenBounds.getMaxY()));
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
    private void gameBoard()
    {
        //Money Counter
        Label moneyCounter = new Label("Player Bank Account");
        moneyCounter.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
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
        playerTurnText = new Text("Player " + (playerTurn+1) + " Turn");
        playerTurnText.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
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
        Text players = createText("Player List!",40,Color.BLACK,"arial");

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
        board.setMaxHeight(620);
        board.setMaxWidth(910);
        main.setCenter(board);
        main.setLeft(playerList);

        StackPane final_main = new StackPane(main);
        for (PlayerInformation information : playerInformation)
        {
            final_main.getChildren().add(information.getPlayerToken());
        }

        Image image = new Image("GUI/Logo.png",300,300,false,false);
        ImageView imageView = new ImageView(image);
        final_main.getChildren().add(imageView);
        //Player Information in Right Table
        playerInfo = new ListView<>();
        for (int i = 0; i < gameBoard.getPlayers().size(); i++)
        {
            playerInfo.getItems().add(i,getPlayerInfo(i));
        }
        playerInfo.getItems().get(0).setStroke(playerInformation[0].playerColor);
        playerList.getChildren().add(playerInfo);
        BorderPane.setAlignment(bankSide,Pos.CENTER_RIGHT);

        //Creating AI logBox
        aiLogBox = new TextArea();
        aiLogBox.setEditable(false);
        aiLogBox.setMinHeight(100);
        aiLogBox.setMaxWidth(200);

        //Creating AI logBox title
        Text aiLogBoxTitle = createText("Agent Player LogBox",20,Color.BLACK,"arial");

        //Add AI logBox and Title into VBox
        playerList.getChildren().add(aiLogBoxTitle);
        playerList.getChildren().add(aiLogBox);


        //If Abridged add timer to stackPane
        if (abridged)
        {
            //Set time text
            timeRemaining = new Text("Time Remaining: " + time);
            timeRemaining.setStyle("-fx-font-size: 40; -fx-border-color: Red");

            //Create TimeLine
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            //Every Second do this
            timeline.getKeyFrames().add(new KeyFrame(Duration.minutes(1),
                    actionEvent -> {
                        time--;
                        if (time >= 0)
                        {
                            timeRemaining.setText("Time Remaining: " + time);
                            highestTurn = playerInformation[playerTurn].getPlayerTurn();
                        }
                        else timeRemaining.setText("Last Round!");

                        //If time = 0, end game
                        if (time <= 0)
                        {
                            if (allPlayerFinished(highestTurn))
                            {

                                Alert endGame = new Alert(AlertType.WARNING);
                                endGame.setOnCloseRequest(e -> endGameScreenAbridged());
                                endGame.setContentText("Game Finished");
                                endGame.show();
                                timeline.stop();
                            }
                        }
                    }));
            timeline.playFromStart();

            //Add TimeLine to StackPane and Place it top right
            StackPane.setAlignment(timeRemaining,Pos.TOP_RIGHT);
            final_main.getChildren().add(timeRemaining);
        }

        //Create and insert exit button into StackPane
        Button exit = new Button("Exit Game!");
        exit.setStyle("-fx-font-size: 30");
        exit.setOnAction(e -> System.exit(0));

        StackPane.setAlignment(exit,Pos.TOP_LEFT);
        final_main.getChildren().add(exit);

        AnchorPane idk = new AnchorPane(final_main);

        //Creating a new Scene
        Scene finalScene = new Scene(idk,screenBounds.getMaxX(),screenBounds.getMaxY());
        gameBoard_GUI.setScene(finalScene);
        gameBoard_GUI.show();

        //Prepare Player initial position
        for(int i = 0; i < gameBoard.getPlayers().size(); i++)
        {
            playerInformation[i].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(i).getPosition(),i));
            playerInformation[i].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(i).getPosition(),i));
        }
    }

    private boolean allPlayerFinished(int highestTurn)
    {
        for (int i = 0; i < gameBoard.getPlayers().size(); i++)
        {
            if (playerInformation[i].getPlayerTurn() != highestTurn)
            {
                return false;
            }
        }
        return true;
    }

    /**
     *
     *
     */
    private void endGameScreenAbridged()
    {
        Stage finalStageAbridged = new Stage();
        finalStageAbridged.setFullScreen(false);
        finalStageAbridged.setResizable(false);
        PriorityQueue<Integer> playerFinalValue = new PriorityQueue<>(Collections.reverseOrder());
        Text[] playerFinalValueText = new Text[gameBoard.getPlayers().size()];
        for (int i = 0; i < gameBoard.getPlayers().size(); i++)
        {
            playerFinalValue.add(gameBoard.getPlayer(i).getTotalWealth());
        }
        for (int i = 0; i < gameBoard.getPlayers().size(); i++)
        {
            if (i == 0)
            {
                playerFinalValueText[i] = createText("Player " + (i+1) + " has a Total Liquid Assets of " + playerFinalValue.poll(),50,Color.RED,"Arial");
            }
            else playerFinalValueText[i] = createText("Player " + (i+1) + " has a Total Liquid Assets of " + playerFinalValue.poll(),40,Color.BLACK,"Arial");
        }
        VBox mainVBox = new VBox();
        mainVBox.setAlignment(Pos.CENTER);
        for (Text text : playerFinalValueText) mainVBox.getChildren().add(text);
        finalStageAbridged.setScene(new Scene(mainVBox,screenBounds.getMaxX(),screenBounds.getMaxY()));
        finalStageAbridged.show();
        finalStageAbridged.setOnCloseRequest(e -> System.exit(0));
    }

    /**
     * Method used to create the Select Player Node
     *
     * @return Node (BorderPane) that contains all the functions for the selectPlayer
     */
    private Node selectPlayer()
    {
        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));
        ListView<String> playerBuildings = new ListView<>();
        for ( int i = 0; i < gameBoard.getPlayer(playerTurn).getProperties().size(); i++)
        {
            playerBuildings.getItems().add(gameBoard.getPlayer(playerTurn).getProperties().get(i).getName());
        }
        playerBuildings.setOnMouseClicked(e ->
        {
            TileProperty chosenBuilding = null;
            for (TileProperty property : gameBoard.getPlayer(playerTurn).getProperties())
            {
                if (Objects.equals(property.getName(), playerBuildings.getSelectionModel().getSelectedItem()))
                {
                    chosenBuilding = property;
                    break;
                }
            }
            int tileIndex = getTileIndex(chosenBuilding);
            try
            {
                if (chosenBuilding instanceof TileBuilding)
                {
                   if(((TileBuilding) chosenBuilding).getDevelopment()>1)
                   {
                       Alert sellHouse = new Alert(AlertType.CONFIRMATION);
                       sellHouse.setTitle("Would you like to sell a house on the Property " + chosenBuilding.getName() + "?");
                       sellHouse.showAndWait();
                       ButtonType yesButton = ButtonType.YES;
                       ButtonType noButton = ButtonType.NO;
                       sellHouse.getButtonTypes().setAll(yesButton,noButton);
                       TileBuilding finalChosenBuilding = (TileBuilding) chosenBuilding;
                       sellHouse.showAndWait().ifPresent(type ->
                       {
                           if (type == ButtonType.YES)
                           {
                               try {
                                   finalChosenBuilding.sellHouse(gameBoard.getBank());
                               }
                               catch (LargeDevelopmentDifferenceException | NoDevelopmentException ex)
                               {
                                   ex.printStackTrace();
                               }
                               moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                               moneyOfPlayer.setFill(Color.GREEN);
                               transition.playFromStart();

                               //Change Tile Color
                               int developmentPercentage = ((((TileBuilding) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getDevelopment()) * 10) + 30;
                               Color tileColour = Color.valueOf(((TileBuilding) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getHexColour());
                               tiles[gameBoard.getPlayer(playerTurn).getPosition()].setStyle("-fx-background-color: linear-gradient(to bottom, #" +
                                       tileColour.toString().substring(2) + " " + developmentPercentage + "%, white 0%);\n" + "-fx-background-radius: 0");
                           }
                       });
                   }
                }
                gameBoard.sellToBank(playerTurn,tileIndex);

                //Change GUI (Money Counter)
                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                moneyOfPlayer.setFill(Color.GREEN);
                transition.playFromStart();

                playerBuildings.getItems().remove(playerBuildings.getSelectionModel().getSelectedItem());
            }
            catch (OwnershipException ex)
            {
                Alert doesNotHaveOwnership = new Alert(AlertType.WARNING);
                doesNotHaveOwnership.setContentText("Not Owner");
                doesNotHaveOwnership.showAndWait();
            }
            catch (PropertyDevelopedException ex)
            {
                Alert alreadyDeveloped = new Alert(AlertType.WARNING);
                alreadyDeveloped.setContentText("Not Developed");
                alreadyDeveloped.showAndWait();
            }
        });
        BorderPane mainPane = new BorderPane();
        VBox playerToChoose = new VBox();
        Text title = createText("Choose building to trade with the bank!",30,Color.BLACK,"arial");
        BorderPane.setAlignment(title,Pos.CENTER);
        mainPane.setTop(title);
        Button playerIcon = new Button();
        playerIcon.setMinWidth(100);
        playerIcon.setMinHeight(100);
        playerIcon.setStyle(playerInformation[playerTurn].getPlayerToken().getStyle());
        playerIcon.setGraphic(playerInformation[playerTurn].getPlayerToken().getGraphic());
        playerIcon.setAlignment(Pos.CENTER);
        playerToChoose.getChildren().add(playerIcon);
        playerToChoose.setPadding(new Insets(100));
        playerBuildings.setMaxHeight(800);
        playerBuildings.setMaxWidth(400);
        playerToChoose.getChildren().add(playerBuildings);
        playerToChoose.setPadding(new Insets(50));

        //Set up of Exit Button
        Button exit = new Button("Exit");
        exit.setOnAction(e ->
                tradeStage.close()
        );
        exit.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(exit,Pos.CENTER);
        mainPane.setBottom(exit);
        mainPane.setCenter(playerToChoose);
        playerToChoose.setAlignment(Pos.CENTER);
        return mainPane;
    }

    /**
     * Method used to create the Select Player Node
     *
     * @return Node (BorderPane) that contains all the functions for the selectPlayer
     */
    private Node mortgageStage()
    {
        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));
        ListView<String> playerBuildings = new ListView<>();
        for ( int i = 0; i < gameBoard.getPlayer(playerTurn).getProperties().size(); i++)
        {
            playerBuildings.getItems().add(gameBoard.getPlayer(playerTurn).getProperties().get(i).getName());
        }
        playerBuildings.setOnMouseClicked(e ->
        {
            TileProperty chosenBuilding = null;
            for (TileProperty property : gameBoard.getPlayer(playerTurn).getProperties())
            {
                if (Objects.equals(property.getName(), playerBuildings.getSelectionModel().getSelectedItem()))
                {
                    chosenBuilding = property;
                    break;
                }
            }
            int tileIndex = getTileIndex(chosenBuilding);
            try
            {
                Alert mortgageYesNo = new Alert(AlertType.CONFIRMATION);
                assert chosenBuilding != null;
                if (chosenBuilding.isMortgaged()) mortgageYesNo.setContentText("Do you want to unmortgage " + chosenBuilding.getName() + "?");
                else mortgageYesNo.setContentText("Do you want to mortgage " + chosenBuilding.getName() + "?");
                mortgageYesNo.setOnCloseRequest(lambda ->
                {
                    try {
                        if (((TileProperty) gameBoard.getTile(tileIndex)).isMortgaged())
                        {
                            ((TileProperty) gameBoard.getTile(tileIndex)).mortgagedBuyBack();
                            //Change GUI (Money Counter)
                            moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                            moneyOfPlayer.setFill(Color.RED);
                            transition.playFromStart();
                        }
                        else
                        {
                            ((TileProperty) gameBoard.getTile(tileIndex)).mortgage();
                            //Change GUI (Money Counter)
                            moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                            moneyOfPlayer.setFill(Color.GREEN);
                            transition.playFromStart();
                        }
                    }
                    catch (IsMortgagedException ex)
                    {
                        Alert alreadyMortgaged = new Alert(AlertType.ERROR);
                        alreadyMortgaged.setContentText("Property Already Mortgaged!");
                        alreadyMortgaged.showAndWait();
                    }
                    catch (PropertyDevelopedException ex) {
                        Alert propertyAlreadyDeveloped = new Alert(AlertType.ERROR);
                        propertyAlreadyDeveloped.setContentText("Property Already Developed!");
                        propertyAlreadyDeveloped.showAndWait();
                    } catch (InsufficientFundsException ex) {
                        ex.printStackTrace();
                    }
                });
                mortgageYesNo.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        BorderPane mainPane = new BorderPane();
        VBox playerToChoose = new VBox();
        Text title = createText("Choose building to Mortgaged with the bank!",30,Color.BLACK,"arial");
        BorderPane.setAlignment(title,Pos.CENTER);
        mainPane.setTop(title);
        Button playerIcon = new Button();
        playerIcon.setMinWidth(100);
        playerIcon.setMinHeight(100);
        playerIcon.setStyle(playerInformation[playerTurn].getPlayerToken().getStyle());
        playerIcon.setGraphic(playerInformation[playerTurn].getPlayerToken().getGraphic());
        playerIcon.setAlignment(Pos.CENTER);
        playerToChoose.getChildren().add(playerIcon);
        playerToChoose.setPadding(new Insets(100));
        playerBuildings.setMaxHeight(800);
        playerBuildings.setMaxWidth(400);
        playerToChoose.getChildren().add(playerBuildings);
        playerToChoose.setPadding(new Insets(50));
        mainPane.setCenter(playerToChoose);

        //Set up of Exit Button
        Button exit = new Button("Exit");
        exit.setOnAction(e ->
                mortgageStage.close()
        );
        exit.setAlignment(Pos.CENTER);
        mainPane.setBottom(exit);
        BorderPane.setAlignment(exit,Pos.CENTER);
        playerToChoose.setAlignment(Pos.CENTER);
        return mainPane;
    }

    /**
     * getTileIndex() gets the index of the requested Tile
     *
     * @param chosenBuilding Property Object Selected
     *
     * @return tileIndex of chosenBuilding
     */
    private int getTileIndex(TileProperty chosenBuilding)
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


    /*
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
     */

    /**
     * Method used to create Node for the Auction Stage
     *
     * @return Node (BorderPane) containing all the functions of the auction
     * @param players current Players in the auction
     */
    private Node auctionNode(ArrayList<Player> players)
    {
        ListView<TextField> playerBid = new ListView<>();
        BorderPane mainAuctionPane = new BorderPane();
        Button auctionPropertyIcon = new Button();
        auctionPropertyIcon.setStyle((tiles[gameBoard.getPlayer(playerTurn).getPosition()]).getStyle());
        auctionPropertyIcon.setText(tiles[gameBoard.getPlayer(playerTurn).getPosition()].getText());
        auctionPropertyIcon.setMinHeight(150);
        auctionPropertyIcon.setMinWidth(150);
        GridPane playerAndText = new GridPane();
        Text titleAuction = createText("Auction House!",100,Color.BLACK,"arial");
        VBox titleAndProperty = new VBox(titleAuction,auctionPropertyIcon);
        mainAuctionPane.setTop(titleAndProperty);
        for (Player player : players)
        {
            Button playerIcon = new Button();
            playerIcon.setMinWidth(50);
            playerIcon.setMinHeight(60);
            playerIcon.setStyle( playerInformation[gameBoard.getPlayers().indexOf(player)].getPlayerToken().getStyle());
            playerAndText.add(playerIcon,gameBoard.getPlayers().indexOf(player),0);
        }
        for (int i = 0; i < players.size();i++)
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
            try
            {
                    gameBoard.auctionMakeBid(gameBoard.getPlayers().indexOf(players.get(playerBid.getSelectionModel().getSelectedIndex())), Integer.parseInt(playerBid.getItems().get(playerBid.getSelectionModel().getSelectedIndex()).getText()));
                    playerBid.getSelectionModel().getSelectedItem().setEditable(false);
                    auctionArray.add(playerBid.getSelectionModel().getSelectedItem());
                    playerBid.getSelectionModel().getSelectedItem().setText("");
                    playerBid.getSelectionModel().selectNext();
                    if (players.get(playerBid.getSelectionModel().getSelectedIndex()) instanceof AIPlayer)
                    {
                        playerBid.getSelectionModel().getSelectedItem().setEditable(false);
                        int aiBid = aiPlayer.makeBid(((TileProperty) gameBoard.getPlayerTile(playerTurn)),gameBoard);
                        playerBid.getSelectionModel().getSelectedItem().setText(String.valueOf(aiBid));
                        gameBoard.auctionMakeBid(gameBoard.getPlayerIndex(aiPlayer),aiBid);
                        playerBid.getSelectionModel().getSelectedItem().setEditable(false);
                        playerBid.getSelectionModel().selectNext();
                    }
                    else playerBid.getSelectionModel().getSelectedItem().setEditable(true);
            }
            catch (InsufficientFundsException ex)
            {
                Alert bidTooHigh = new Alert(AlertType.WARNING);
                bidTooHigh.setContentText("Please bid only what you own!");
                bidTooHigh.showAndWait();
                playerBid.getSelectionModel().getSelectedItem().setEditable(false);
                playerBid.getSelectionModel().selectPrevious();
                playerBid.getSelectionModel().getSelectedItem().setEditable(true);
            }
            if (playerBid.getSelectionModel().getSelectedIndex()+1 == gameBoard.getPlayers().size() && !Objects.equals(playerBid.getSelectionModel().getSelectedItem().getText(), ""))
            {
                int higherPlayer = gameBoard.auctionHighestBid()[2];
                int higherBid = gameBoard.auctionHighestBid()[0];
                if (gameBoard.auctionHighestBid()[1] != 1)
                {
                    ArrayList<Player> newPlayerList = new ArrayList<>();
                    for (Player player : players)
                    {
                        if(gameBoard.auctionGetBid(gameBoard.getPlayers().indexOf(player)) == higherBid)
                        {
                            newPlayerList.add(player);
                        }
                    }
                    auctionWindow.close();
                    auctionHouse(newPlayerList);
                }
                else
                {
                    //Give highest bid the property, and take the money
                    gameBoard.getPlayer(higherPlayer).addProperty((TileProperty) gameBoard.getTiles()[gameBoard.getPlayer(playerTurn).getPosition()]);
                    ((TileProperty) gameBoard.getTiles()[gameBoard.getPlayer(playerTurn).getPosition()]).setOwner(gameBoard.getPlayer(higherPlayer));
                    gameBoard.getPlayer(higherPlayer).removeMoney(higherBid);

                    //Remove Money and show Alert
                    moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(higherPlayer).getBalance()));
                    Alert auctionWinner = new Alert(AlertType.INFORMATION);
                    auctionWinner.setContentText("Winner of Auction is Player " + (playerInformation[higherPlayer].getPlayerNumber() + 1)
                                                                                + "!\nThey paid " + higherBid + " for the property!");
                    auctionWinner.showAndWait();
                    auctionWindow.close();
                    updatePriceAndEndTurn();
                }
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
     * Method used to ease up cluster code. It updates the showed price and resets variables for next turn!
     *
     */
    private void updatePriceAndEndTurn() {
        playerInformation[playerTurn].incrementPlayerTurn();
        playerInfo.getItems().get(playerTurn).setStroke(Color.BLACK);
        playerInfo.getItems().remove(playerTurn);
        playerInfo.getItems().add(playerTurn, getPlayerInfo(playerTurn));
        if (playerTurn + 1 >= gameBoard.getPlayers().size()) {
            playerTurn = 0;
        } else {
            playerTurn += 1;
        }
        playerInfo.getItems().get(playerTurn).setStroke(playerInformation[playerTurn].playerColor);
        playerInfo.refresh();
        playerTurnText.setText(("Player " + (playerTurn + 1) + " turn"));
        moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
        finishedTurn = false;
        playerBoughtProperty = false;
        rolledDouble = 0;
        currPlayerIcon.setStyle(playerInformation[playerTurn].getPlayerToken().getStyle());
        if(gameBoard.getPlayer(playerTurn).isInJail())
        {
            gameBoard.getPlayer(playerTurn).jailNewTurn();
            Alert playerInJail = new Alert(AlertType.WARNING);
            playerInJail.setContentText("Player " + (playerTurn+1) + " is in Jail\nPay 50 to get out of Jail?");
            updatePriceAndEndTurn();
        }
        else if (gameBoard.getPlayer(playerTurn) instanceof AIPlayer) agentPlayerTurn();
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
            //Roll before buying
            if (!finishedTurn)
            {
                Alert d = new Alert(AlertType.WARNING);
                d.setContentText("Roll dies and move before playing another action!");
                d.showAndWait();
            }
            else
            {
                //If building not owned by bank, building is not purchasable
                try {
                    if (((TileProperty) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getOwner() != gameBoard.getBank()) {
                        Alert alreadyOwned = new Alert(AlertType.WARNING);
                        alreadyOwned.setContentText("Property already owned!");
                        alreadyOwned.showAndWait();
                    }
                    else
                    {
                        //Pay Price
                        try
                        {
                            gameBoard.playerPurchase(playerTurn,gameBoard.getPlayer(playerTurn).getPosition(),0);
                        }

                        //Alert created because property is not purchasable
                        catch (NonPropertyTileException ex)
                        {
                            Alert notPropertyAlert = new Alert(AlertType.WARNING);
                            notPropertyAlert.setContentText("Property not buy-able");
                            notPropertyAlert.showAndWait();
                        }

                        //Alert created due to insufficient funds
                        catch (InsufficientFundsException ex)
                        {
                                Alert insufficientFundsNoMortgage = new Alert(AlertType.WARNING);
                                insufficientFundsNoMortgage.setContentText("No Funds Available ");
                                insufficientFundsNoMortgage.showAndWait();
                        }

                        //Set Owner of tile bought
                        ((TileProperty) gameBoard.getTiles()[gameBoard.getPlayer(playerTurn).getPosition()]).setOwner(gameBoard.getPlayer(playerTurn));

                        //Change GUI (Money Counter)
                        moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                        moneyOfPlayer.setFill(Color.RED);
                        transition.playFromStart();
                        playerBoughtProperty = true;
                    }
                }
                catch (ClassCastException x)
                {
                    Alert buildingCannotbeBought = new Alert(AlertType.WARNING);
                    buildingCannotbeBought.setContentText("Property Cannot be bought!");
                    buildingCannotbeBought.showAndWait();
                }
            }
        });

        //Setting up move button
        Button move = new Button("Move");
        move.setPrefSize(100,50);
        move.setOnAction(e ->
                {
                    if (!finishedTurn)
                    {
                        rollDices();
                        if (gameBoard.checkDouble(dices.getDiceValues()))
                        {
                            finishedTurn = false;
                            rolledDouble++;
                            playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                            playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                            //rolledDouble jail
                            if (rolledDouble == 3)
                            {
                                Alert goingToJail = new Alert(AlertType.WARNING);
                                goingToJail.setContentText("Player " + (playerTurn+1) + " has rolled double twice!\nPay £50 to stay out of jail?");
                                ButtonType yesButton = ButtonType.YES;
                                ButtonType noButton = ButtonType.NO;
                                goingToJail.getButtonTypes().setAll(yesButton,noButton);
                                goingToJail.showAndWait().ifPresent(type ->
                                {
                                    if (type == ButtonType.YES) {
                                        gameBoard.getPlayer(playerTurn).removeMoney(50);
                                        moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                        moneyOfPlayer.setFill(Color.RED);
                                        transition.playFromStart();
                                        finishedTurn = true;
                                        move.fire();

                                    } else {
                                        gameBoard.getPlayer(playerTurn).toJail();
                                        playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y', gameBoard.getPlayer(playerTurn).getPosition(), playerTurn));
                                        playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X', gameBoard.getPlayer(playerTurn).getPosition(), playerTurn));
                                    }
                                });
                            }
                        }
                        else
                        {
                            boolean passedGo = gameBoard.getPlayer(playerTurn).move(dices.getDiceValues());
                            playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                            playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                            if (passedGo)
                            {
                                //Change GUI (Money Counter)
                                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                moneyOfPlayer.setFill(Color.GREEN);
                                transition.playFromStart();
                            }
                            if(gameBoard.getPlayerTile(playerTurn) instanceof TileCard)
                            {
                                int currentBalance = gameBoard.getPlayer(playerTurn).getBalance();
                                Card newCard = ((TileCard) gameBoard.getPlayerTile(playerTurn)).pickCard(gameBoard);
                                Alert cardEffect = new Alert(AlertType.WARNING);
                                cardEffect.setContentText(newCard.getDescription());
                                cardEffect.showAndWait();
                                newCard.playCard(gameBoard.getPlayer(playerTurn),gameBoard);
                                playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                                playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                                //If current player loses money due to card
                                if (currentBalance > gameBoard.getPlayer(playerTurn).getBalance())
                                {
                                    moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                    moneyOfPlayer.setFill(Color.RED);
                                    transition.playFromStart();
                                    checkIfBankrupt((currentBalance-gameBoard.getPlayer(playerTurn).getBalance()),gameBoard.getBank());
                                }
                                if (currentBalance < gameBoard.getPlayer(playerTurn).getBalance())
                                {
                                    moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                    moneyOfPlayer.setFill(Color.GREEN);
                                    transition.playFromStart();
                                }
                            }
                            else if (gameBoard.getPlayerTile(playerTurn) instanceof TileGoToJail)
                            {
                                Alert goingToJail = new Alert(AlertType.WARNING);
                                goingToJail.setContentText("Player " + (playerTurn+1) + " has landed on Go to Jail Tile!\nPay £50 to stay out of jail?");
                                ButtonType yesButton = ButtonType.YES;
                                ButtonType noButton = ButtonType.NO;
                                goingToJail.getButtonTypes().setAll(yesButton,noButton);
                                goingToJail.showAndWait().ifPresent(type ->
                                {
                                    if (type == ButtonType.YES) {
                                        gameBoard.getPlayer(playerTurn).removeMoney(50);
                                        moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                        moneyOfPlayer.setFill(Color.RED);
                                        transition.playFromStart();
                                        finishedTurn = true;
                                        move.fire();

                                    } else {
                                        gameBoard.getPlayer(playerTurn).toJail();
                                        playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y', gameBoard.getPlayer(playerTurn).getPosition(), playerTurn));
                                        playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X', gameBoard.getPlayer(playerTurn).getPosition(), playerTurn));
                                    }
                                });
                            }
                            else if (gameBoard.getPlayerTile(playerTurn) instanceof TileTax)
                            {
                                int outstandingTax = ((TileTax) gameBoard.getPlayerTile(playerTurn)).payTax(gameBoard.getPlayer(playerTurn),((TileFreeParking) gameBoard.getTile(20)));
                                if (outstandingTax > 0) checkIfBankrupt(outstandingTax,gameBoard.getBank());
                                Alert payedTax = new Alert(AlertType.WARNING);
                                payedTax.setContentText("Player " + (playerTurn+1) + " has payed the parking tax!");
                                payedTax.showAndWait();
                                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                moneyOfPlayer.setFill(Color.RED);
                                transition.playFromStart();
                            }
                            else if (gameBoard.getPlayerTile(playerTurn) instanceof TileFreeParking)
                            {
                                int freeMoney = ((TileFreeParking) gameBoard.getPlayerTile(playerTurn)).getFreeParkingFines();
                                ((TileFreeParking) gameBoard.getPlayerTile(playerTurn)).payToPlayer(gameBoard.getPlayer(playerTurn));
                                Alert freeParkingMoney = new Alert(AlertType.INFORMATION);
                                freeParkingMoney.setContentText("Player " + (playerTurn+1) + " has landed on the free parking tile!\nThey obtain " + freeMoney);
                                freeParkingMoney.showAndWait();
                                moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                moneyOfPlayer.setFill(Color.GREEN);
                                transition.playFromStart();
                            }
                            finishedTurn = true;

                            //Pay Rent
                            try {
                                Player playerOwed = ((TileProperty) gameBoard.getPlayerTile(playerTurn)).getOwner();
                                if (playerOwed.isInJail())
                                {
                                    Alert inJail = new Alert(AlertType.WARNING);
                                    inJail.setContentText("Owner of property is in Jail!\nNo rent is paid!");
                                    inJail.showAndWait();
                                }
                                else
                                {
                                    if (playerOwed != gameBoard.getBank())
                                    {
                                        //Pay for Rent
                                        int rentPrice = gameBoard.payRent(playerTurn, gameBoard.getPlayer(playerTurn).getPosition(), dices.getDiceValues());

                                        if (rentPrice > 0) checkIfBankrupt(rentPrice, playerOwed);

                                        //Alert player that they paid for the rent
                                        Alert payedRent = new Alert(AlertType.WARNING);
                                        if (gameBoard.getPlayerTile(playerTurn) instanceof TileBuilding)
                                            payedRent.setContentText("Player " + (playerTurn + 1) + " has payed " + ((TileBuilding) gameBoard.getPlayerTile(playerTurn)).getRent().get(((TileBuilding) gameBoard.getPlayerTile(playerTurn)).getDevelopment()) + " for Rent!\nThe Rent has gone to Player " + (gameBoard.getPlayerIndex(playerOwed) + 1));
                                        else
                                            payedRent.setContentText("Player " + (playerTurn + 1) + " has payed " + ((TileStation) gameBoard.getPlayerTile(playerTurn)).getRent().get(((TileStation) gameBoard.getPlayerTile(playerTurn)).getOwnedStations(gameBoard.getPlayer(playerTurn))) + " for Rent!\nThe Rent has gone to Player " + (gameBoard.getPlayerIndex(playerOwed) + 1));
                                        payedRent.showAndWait();

                                        //Property Owned by Player
                                        playerBoughtProperty = true;

                                        //Change GUI (Money Counter)
                                        moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                                        moneyOfPlayer.setFill(Color.RED);
                                        transition.playFromStart();
                                    }
                                }
                            }
                            catch(NonPropertyTileException ex)
                            {
                                System.err.println("Not a property by player!");
                            }
                            catch(IsMortgagedException ex)
                            {
                                System.err.println("Building is Mortgaged, player does not need to pay!");
                            }
                            catch(ClassCastException ex)
                            {
                                //Not Important but necessary
                            }
                            catch(IsInJail ex)
                            {
                                Alert inJail = new Alert(AlertType.ERROR);
                                inJail.setContentText("Player " + playerTurn + " is in jail!");
                                inJail.showAndWait();
                            }
                        }
                    }

                }
        );

        //Setting up newTurn button
        newTurn = new Button("Next Turn");
        newTurn.setOnAction(e->
        {
            if(gameBoard.getPlayer(playerTurn).isInJail())
            {
                gameBoard.getPlayer(playerTurn).jailNewTurn();
                updatePriceAndEndTurn();
            }
            else if (!finishedTurn && rolledDouble == 0)
            {
                Alert d = new Alert(AlertType.WARNING);
                d.setContentText("Roll dies and move before playing another action!");
                d.showAndWait();
            }
            else {
                try
                {
                        if (!playerBoughtProperty && ((TileProperty) gameBoard.getPlayerTile(playerTurn)).getOwner() == gameBoard.getBank()) {
                            auctionHouse(gameBoard.getPlayers());
                        }
                        else
                        {
                            if (gameBoard.getPlayers().size() == 0) System.exit(0);
                            updatePriceAndEndTurn();
                        }
                }
                catch (ClassCastException ex)
                {
                    updatePriceAndEndTurn();
                }
            }
        });

        //Setting up Build button
        Button Build = new Button("Build");
        Build.setPrefSize(100,50);
        Build.setOnAction(e ->
        {
                if (gameBoard.getPlayer(playerTurn).getProperties().size() > 0)
                {
                    Stage build = new Stage();
                    build.setScene(new Scene((Parent) buildScene(),screenBounds.getMaxX()/2,screenBounds.getMaxY()/2));
                    build.showAndWait();
                }
                else
                {
                    Alert noPropertiesOwned = new Alert(AlertType.ERROR);
                    noPropertiesOwned.setContentText("You do not own any properties!");
                    noPropertiesOwned.showAndWait();
                }
        });

        Button trade = new Button("Trade");
        trade.setOnAction(e ->{
            if (finishedTurn || rolledDouble > 0)
            {
                tradeStage = new Stage();
                tradeStage.setResizable(false);
                tradeStage.setScene(new Scene((Parent) selectPlayer(), 800, 800));
                tradeStage.showAndWait();
            }
            else
            {
                Alert d = new Alert(AlertType.WARNING);
                d.setContentText("Roll dies and move before playing another action!");
                d.showAndWait();
            }
        });

        Button mortgage = new Button("Mortgage");
        mortgage.setOnAction(e ->{

            if (finishedTurn || rolledDouble > 0)
            {
                mortgageStage = new Stage();
                mortgageStage.setResizable(false);
                mortgageStage.setScene(new Scene((Parent) mortgageStage(), 800, 800));
                mortgageStage.showAndWait();
            }
            else
            {
                Alert d = new Alert(AlertType.WARNING);
                d.setContentText("Roll dies and move before playing another action!");
                d.showAndWait();
            }
        });

        //Setting up HBox to finalize the controls
        HBox final_control = new HBox(newTurn,move,Buy,Build, trade,mortgage);
        final_control.setAlignment(Pos.CENTER);
        final_control.setSpacing(100);
        return final_control;
    }

    private Node buildScene()
    {
        //Pause transition taken from https://stackoverflow.com/questions/55768170/temporarily-change-color-of-a-button-when-clicked

        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));

        BorderPane mainPane = new BorderPane();
        ListView<String> playerProperties = new ListView<>();
        for (TileProperty property : gameBoard.getPlayer(playerTurn).getProperties())
        {
            playerProperties.getItems().add(property.getName());
            if (property instanceof TileBuilding) playerProperties.setStyle("-fx-background-color:" + ((TileBuilding) property).getHexColour());
        }
        playerProperties.setOnMouseClicked(e ->
        {
            TileBuilding currBuilding = ownsAllProperties(playerProperties.getSelectionModel().getSelectedItem());
            if (currBuilding != null)
            {
                try {
                    ((TileBuilding) gameBoard.getPlayerTile(playerTurn)).buyHouse(gameBoard.getBank());

                    //Change GUI (Money Counter)
                    moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                    moneyOfPlayer.setFill(Color.RED);
                    transition.playFromStart();

                    //Change Tile
                    int developmentPercentage = ((((TileBuilding) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getDevelopment()) * 10) + 30;
                    Color tileColour = Color.valueOf(((TileBuilding) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getHexColour());
                    tiles[gameBoard.getPlayer(playerTurn).getPosition()].setStyle("-fx-background-color: linear-gradient(to bottom, #" +
                            tileColour.toString().substring(2) + " " + developmentPercentage + "%, white 0%);\n" + "-fx-background-radius: 0");

                }

                //Alert for properties with max development
                catch (PropertyDevelopedException ex) {
                    Alert exceptionBuild = new Alert(AlertType.WARNING);
                    exceptionBuild.setContentText("The Property has max development!");
                    exceptionBuild.showAndWait();
                    ex.printStackTrace();
                }

                //Alert for idk
                catch (LargeDevelopmentDifferenceException ex) {
                    Alert exceptionBuild = new Alert(AlertType.WARNING);
                    exceptionBuild.setContentText("idk");
                    exceptionBuild.showAndWait();
                    ex.printStackTrace();
                }

                //Alert isMortgaged
                catch (IsMortgagedException ex) {
                    Alert exceptionBuild = new Alert(AlertType.WARNING);
                    exceptionBuild.setContentText("Building is mortgaged!");
                    exceptionBuild.showAndWait();
                    ex.printStackTrace();
                }

                //Alert for insufficient funds
                catch (InsufficientFundsException ex) {
                    Alert exceptionBuild = new Alert(AlertType.WARNING);
                    exceptionBuild.setContentText("Insufficient Funds!");
                    exceptionBuild.showAndWait();
                    ex.printStackTrace();
                }
            } else {
                Alert doesNotOwn = new Alert(AlertType.WARNING);
                doesNotOwn.setContentText("You do not own all the properties in the neighbourhood!\nYou cannot build anything yet!");
                doesNotOwn.showAndWait();
            }
        });
        mainPane.setCenter(playerProperties);
        return mainPane;
    }
    private void auctionHouse(ArrayList<Player> players)
    {
        //Remove players in jail from the arrayList
        players.removeIf(Player::isInJail);

        //Initialise auction for the backend
        gameBoard.auctionInitialise();
        gameBoard.auctionStart();
        auctionWindow = new Stage();
        auctionWindow.setResizable(false);
        auctionArray = new ArrayList<>();
        auctionWindow.setScene(new Scene((Parent) auctionNode(players), 800, 800));
        auctionWindow.show();
        //https://stackoverflow.com/questions/17003906/prevent-cancel-closing-of-primary-stage-in-javafx-2-2
        auctionWindow.setOnCloseRequest(Event::consume);
    }

    private void checkIfBankrupt(int outstanding, Player playerOwed)
    {
        Stage bankrupt = new Stage();
        if (gameBoard.getPlayer(playerTurn).getBalance() <= 0)
        {
            //If player has properties, force to sell
            if (gameBoard.getPlayer(playerTurn).getProperties().size() != 0)
            {
                PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
                transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));
                ListView<String> playerBuildings = new ListView<>();
                for (int i = 0; i < gameBoard.getPlayer(playerTurn).getProperties().size(); i++)
                {
                    playerBuildings.getItems().add(gameBoard.getPlayer(playerTurn).getProperties().get(i).getName() + " : £" + gameBoard.getPlayer(playerTurn).getProperties().get(i).getPrice());
                }
                playerBuildings.setOnMouseClicked(e ->
                {
                    TileProperty chosenBuilding = null;
                    for (int i = 0; i < gameBoard.getTiles().length-1; i++)
                    {
                        if (playerBuildings.getSelectionModel().getSelectedItem().contains(gameBoard.getTiles()[i].getName()))
                        {
                            chosenBuilding = (TileProperty) gameBoard.getTiles()[i];
                        }
                    }
                    int tileIndex = getTileIndex(chosenBuilding);
                    try {
                        gameBoard.sellToBank(playerTurn, tileIndex);

                        //Change GUI (Money Counter)
                        moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
                        moneyOfPlayer.setFill(Color.GREEN);
                        transition.playFromStart();
                        playerBuildings.getItems().remove(playerBuildings.getSelectionModel().getSelectedItem());
                        if (gameBoard.getPlayer(playerTurn).getBalance() >= outstanding)
                        {
                            gameBoard.getPlayer(playerTurn).removeMoney(outstanding);
                            gameBoard.getPlayer(gameBoard.getPlayerIndex(playerOwed)).addMoney(outstanding);
                            Alert finishedPayingPlayer = new Alert(AlertType.INFORMATION);
                            finishedPayingPlayer.setContentText("Player " + (playerTurn+1) + " has finished paying Player " + (gameBoard.getPlayerIndex(playerOwed)+1));
                            finishedPayingPlayer.setOnCloseRequest(lam ->
                            {
                                finishedPayingPlayer.close();
                                bankrupt.close();
                            });
                            finishedPayingPlayer.showAndWait();
                        }
                    } catch (OwnershipException ex) {
                        Alert doesNotHaveOwnership = new Alert(AlertType.WARNING);
                        doesNotHaveOwnership.setContentText("Not Owner");
                        doesNotHaveOwnership.showAndWait();
                    } catch (PropertyDevelopedException ex) {
                        Alert alreadyDeveloped = new Alert(AlertType.WARNING);
                        alreadyDeveloped.setContentText("Not Developed");
                        alreadyDeveloped.showAndWait();
                    }

                });
                BorderPane mainPane = new BorderPane();
                VBox playerToChoose = new VBox();
                Text title = createText("Sell something to go out of bankruptcy!\nOutstanding amount: " + outstanding, 50, Color.BLACK, "arial");
                BorderPane.setAlignment(title, Pos.CENTER);
                mainPane.setTop(title);
                Button playerIcon = new Button();
                playerIcon.setMinWidth(100);
                playerIcon.setMinHeight(100);
                playerIcon.setStyle("-fx-background-color: #" + playerInformation[playerTurn].getPlayerColor_String());
                playerIcon.setAlignment(Pos.CENTER);
                playerToChoose.getChildren().add(playerIcon);
                playerToChoose.setPadding(new Insets(100));
                playerBuildings.setMaxHeight(800);
                playerBuildings.setMaxWidth(400);
                playerToChoose.getChildren().add(playerBuildings);
                playerToChoose.setPadding(new Insets(50));
                mainPane.setCenter(playerToChoose);
                playerToChoose.setAlignment(Pos.CENTER);
                bankrupt.setScene(new Scene(mainPane, screenBounds.getMaxX(), screenBounds.getMaxY()));
                bankrupt.showAndWait();
            }
            else
            {
                removePlayer();
            }
        }
    }

    private void removePlayer()
    {
        ArrayList<Player> newPlayerList = new ArrayList<>();
        ArrayList<PlayerInformation> newPlayerInfo = new ArrayList<>();
        for (Player player : gameBoard.getPlayers())
        {
            if (!(player == gameBoard.getPlayer(playerTurn)))
            {
                newPlayerList.add(player);
            }
        }
        for (int i = 0; i < gameBoard.getPlayers().size(); i++)
        {
            if (i != playerTurn)
            {
                newPlayerInfo.add(playerInformation[i]);
            }
        }
        Alert lost = new Alert(AlertType.WARNING);
        lost.setHeaderText("Player " + (playerTurn+1) + " has gone bankrupt!");
        lost.setOnCloseRequest(e ->
        {
            playerInformation[playerTurn].playerToken.setGraphic(null);
            playerInformation[playerTurn].playerToken.setStyle("-fx-background-color: transparent");
            PlayerInformation[] finalList = new PlayerInformation[newPlayerInfo.size()];
            for(int i = 0; i < finalList.length; i++)
            {
                finalList[i] = newPlayerInfo.get(i);
            }
            playerInformation = finalList;
            playerInfo.getItems().remove(playerTurn);
            gameBoard.setPlayers(newPlayerList);
            if (gameBoard.getPlayers().size() <= 1)
            {
                Button winnerPlayerToke = new Button();
                winnerPlayerToke.setStyle(playerInformation[0].getPlayerToken().getStyle());
                winnerPlayerToke.setGraphic(playerInformation[0].getPlayerToken().getGraphic());
                winnerPlayerToke.setPrefSize(100,100);
                Alert winner = new Alert(AlertType.INFORMATION);
                winner.setGraphic(winnerPlayerToke);
                winner.setContentText("You are the winner!");
                winner.setHeaderText("Congratulations!");
                winner.showAndWait();
                winner.setOnCloseRequest(lambda ->
                        System.exit(0));
            }
            else newTurn.fire();
        });
        lost.showAndWait();
    }

    /**
     * Method to check whether the current player owns all the houses in the neighbourhood, to see if the player
     * can build a house or not.
     *
     * @return Whether the player owns all properties in the neighbourhood
     * @param selectedItem selected Property
     */
    private TileBuilding ownsAllProperties(String selectedItem)
    {
        boolean ownsAll = true;
        TileProperty buildingSelected = null;
        for (Tile tile : gameBoard.getTiles())
        {
            if (Objects.equals(tile.getName(), selectedItem))
            {
                buildingSelected =((TileProperty) tile);
                break;
            }
        }

        if ( buildingSelected instanceof  TileStation)
        {
            Alert cannotBuildOnStation = new Alert(AlertType.WARNING);
            cannotBuildOnStation.setContentText("Cannot build on Station!");
            cannotBuildOnStation.showAndWait();
        }
        assert buildingSelected != null;
        for (TileProperty property : buildingSelected.getNeighborhood())
        {
            if (property.getOwner() != gameBoard.getPlayer(playerTurn))
            {
                ownsAll = false;
            }
        }
        if (ownsAll) {
            assert buildingSelected instanceof TileBuilding;
            return (TileBuilding) buildingSelected;
        }
        return null;
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
        return new Text("Player " + (playerNo + 1)  + "\n" +
                "Player Bank Deposit: " + gameBoard.getPlayer(playerNo).getBalance() + "\n" +
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
        mainMenu.setResizable(false);

        //Creating exit button
        Button exit = new Button("Exit");
        exit.setPrefSize(100,50);
        exit.setOnAction( e -> System.exit(0));

        //Main Text set up
        Text main_Text = createText("Main Menu",50,Color.BLACK,"arial");
        BorderPane.setAlignment(main_Text,Pos.CENTER);
        BorderPane main = new BorderPane();

        //Creating Resources Button
        Button references = new Button("References");
        references.setOnAction(e ->
        {
            Alert showReferences = new Alert(AlertType.INFORMATION);
            showReferences.setHeaderText("References");
            showReferences.setContentText("Dice Faces from pngwing.\n\nTokens Created by Zeno's Sister, Cosima de Angeli.\n\nProperty Tycoon Logo Created using BrandCrowd.");
            showReferences.showAndWait();
        });
        //Creating Buttons
        Button fullGameButton = new Button("Full Game");
        //Style of Button Gotten from:
        //http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
        fullGameButton.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
                -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                    -fx-font-weight: bold;
                    -fx-font-size: 2em;""");
        fullGameButton.setPrefWidth(300);
        fullGameButton.setPrefHeight(100);
        fullGameButton.setOnAction(e ->
        {
            mainMenu.close();
            playerSelection();
        });


        Button abridgedGameButton = new Button("Abridged Game");
        //Style of Button Gotten from:
        //http://fxexperience.com/2011/12/styling-fx-buttons-with-css/
        abridgedGameButton.setStyle("""
                -fx-padding: 8 15 15 15;
                    -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                    -fx-background-radius: 8;
                    -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                    -fx-font-weight: bold;
                    -fx-font-size: 2em;""");
        abridgedGameButton.setPrefWidth(300);
        abridgedGameButton.setPrefHeight(100);
        abridgedGameButton.setOnAction(e ->
        {
            abridged = true;
            Alert choseTime = new Alert(AlertType.WARNING);
            choseTime.setContentText("Default time is 20 minutes");
            choseTime.setHeaderText("Please insert the desired time (in minutes)");
            TextField timeDecided = new TextField();
            choseTime.setGraphic(timeDecided);
            choseTime.setOnCloseRequest( x->
                    {
                        if (!Objects.equals(timeDecided.getText(), ""))this.time = Integer.valueOf(timeDecided.getText());
                        mainMenu.close();
                        playerSelection();
                    }
            );
            choseTime.showAndWait();
        });

        //Creating and setting up settings button
        Button settings_button = new Button("Settings");
        settings_button.setPrefWidth(200);
        settings_button.setPrefHeight(50);
        settings_button.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        settings_button.setStyle("""
                -fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;
                -fx-background-radius: 8;
                -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );
                -fx-font-weight: bold;
                -fx-font-size: 2em;""".indent(4));
        settings_button.setOnAction(e ->
                {
                mainMenu.close();
                settings();
                });

        //Creating and Setting up HBox title
        HBox title =  new HBox(main_Text);
        title.setAlignment(Pos.TOP_CENTER);
        //Creating and Setting up VBox main_buttons
        VBox main_buttons = new VBox(fullGameButton, abridgedGameButton, settings_button);

        main_buttons.setAlignment(Pos.CENTER);
        main_buttons.setSpacing(50);

        //Setting up Main
        main.setCenter(main_buttons);
        main.setTop(title);

        StackPane finalPane = new StackPane(main,exit,references);
        StackPane.setAlignment(exit,Pos.TOP_RIGHT);
        StackPane.setAlignment(references,Pos.TOP_LEFT);
        mainMenu.setScene(new Scene(finalPane,screenBounds.getMaxX(),screenBounds.getMaxY()));
        mainMenu.show();
    }

    /**
     * Method used to create the Stage for the settings of the Property Tycoon Game
     *
     */
    private void settings()
    {
        Stage settings_Stage = new Stage();
        settings_Stage.setResizable(false);
        BorderPane mainPane = new BorderPane();
        mainPane.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //Setting up Text title for the Settings Menu
        Text mainTitle = createText("Settings Menu!",100,Color.BLACK,"arial");
        mainPane.setTop(mainTitle);
        BorderPane.setAlignment(mainTitle,Pos.CENTER);

        //
        VBox settingOptions = new VBox();
        settingOptions.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        settingOptions.setSpacing(50);
        settingOptions.setAlignment(Pos.CENTER);
        mainPane.setCenter(settingOptions);

        //Setting up change player colour Button
        Button changePlayerColor = new Button("Change Player Main Colors");
        changePlayerColor.setMinWidth(100);
        changePlayerColor.setMinHeight(80);
        changePlayerColor.setOnAction(e -> playerChangeColor());
        settingOptions.getChildren().add(changePlayerColor);

        //Setting up goBack button
        Button goBack = new Button("Back to Main Menu!");
        goBack.setOnAction(e ->
        {
            settings_Stage.close();
            mainMenu();
        });
        settingOptions.getChildren().add(goBack);

        //Finish Stage
        settings_Stage.setScene(new Scene(mainPane,screenBounds.getMaxX(),screenBounds.getMaxY()));
        settings_Stage.show();
    }

    /**
     *
     */
    private void playerChangeColor()
    {
        Stage changeColourStage = new Stage();
        changeColourStage.setResizable(false);
        BorderPane mainPane = new BorderPane();
        Button[] playerColors = new Button[5];
        for (int i = 0; i < 5; i++)
        {
            //Create playerIcon to show player
            Button playerIcon = new Button();
            int finalI = i;
            playerIcon.setOnAction(e ->
            {
                ColorPicker colorPicker = new ColorPicker();
                colorPicker.setValue(colors_Color[finalI]);
                colorPicker.setOnAction(t ->
                {
                    colors_Color[finalI] = colorPicker.getValue();
                    playerIcon.setStyle("-fx-background-color: #" + colors_Color[finalI].toString().substring(2));
                });
                Alert setColor = new Alert(AlertType.INFORMATION);
                setColor.setTitle("Please Select the new desired Color!");
                setColor.setHeaderText("Select Color ---->");
                setColor.setGraphic(colorPicker);
                setColor.showAndWait();
            });
            playerIcon.setMinWidth(100);
            playerIcon.setMinHeight(100);
            playerIcon.setStyle("-fx-background-color: #" + colors_Color[i].toString().substring(2));
            playerColors[i] = playerIcon;
        }
        Button exit = new Button("Go Back!");
        exit.setOnAction(e -> changeColourStage.close());
        VBox mainBox = new VBox();
        mainBox.setAlignment(Pos.CENTER);
        for (Button button : playerColors) mainBox.getChildren().add(button);
        Text menuTitle = createText("Change Player Colour Menu!",80,Color.BLACK,"arial");
        mainPane.setTop(menuTitle);
        BorderPane.setAlignment(menuTitle,Pos.CENTER);
        mainBox.getChildren().add(exit);
        mainPane.setCenter(mainBox);
        changeColourStage.setScene(new Scene(mainPane,screenBounds.getMaxX(),screenBounds.getMaxY()));
        changeColourStage.show();
    }

    /**
     * Creates a Stage for player to be able to select how many player will be playing, and their respective colours.
     *
     */
    private void playerSelection()
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
        playerSelection.setResizable(false);
        playerSelection.setScene(new Scene(mainPane,screenBounds.getMaxX(),screenBounds.getMaxY()));
        startGameButton.setOnAction(e ->
        {
            if(player_Index.size() == 1)
            {
                Alert agentPlayerOrTryAgain = new Alert(AlertType.CONFIRMATION);
                ButtonType yesButton = ButtonType.YES;
                ButtonType noButton = ButtonType.NO;
                agentPlayerOrTryAgain.setContentText("Continue to play alone with the an Agent Player!");
                agentPlayerOrTryAgain.getButtonTypes().setAll(yesButton,noButton);
                agentPlayerOrTryAgain.showAndWait().ifPresent(type ->
                {
                    if (type == ButtonType.YES)
                    {
                        playerSelection.close();
                        aiPlayer = new AIPlayer(gameBoard);
                        choosePlayerOrder();
                    }
                });
            }
            else
            {
                playerSelection.close();
                aiPlayer = new AIPlayer(gameBoard);
                choosePlayerOrder();
            }
        });
        playerSelection.show();
    }

    /**
     *
     * playerWindows() creates an HBox with windows for all the possible players
     *
     * @return return the HBox with the five different type of players
     */
    private HBox playerWindows()
    {
        player_Index = new HashMap<>();
        AtomicInteger playerNumber = new AtomicInteger();
        //Colors (String) set up


        //Creating selectedHumanPlayer boolean array
        boolean[] selectedHumanPlayer = new boolean[5];
        boolean[] selectedNPCPlayer = new boolean[5];

        HBox playerWindows = new HBox();
        Button[] playerWindowArray = new Button[5];
        for (int i = 0; i < 5; i++)
        {
            selectedHumanPlayer[i] = false;
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
                if (!selectedHumanPlayer[colorNum] && !selectedNPCPlayer[colorNum])
                {
                    playerWindow.setStyle("\n" +
                            "    -fx-background-color:transparent ;\n" +
                            "    -fx-background-radius:0;\n" +
                            "    -fx-border-color: #" + colors_Color[colorNum].toString().substring(2)+";\n" +
                            "    -fx-border-width: 10 10 10 10;\n");
                  selectedHumanPlayer[colorNum] = true;
                  player_Index.put(Integer.valueOf(String.valueOf(playerNumber)), colors_Color[colorNum]);
                  playerNumber.getAndIncrement();
                }

                //If player has already been selected, then the selected player will become a NPC
                else if (selectedHumanPlayer[colorNum] && !selectedNPCPlayer[colorNum])
                {
                    playerWindow.setStyle("""
                        -fx-background-color:transparent ;
                        -fx-background-radius:0;
                        -fx-border-color:black;
                        -fx-border-width: 10 10 10 10;
                    """);
                    selectedHumanPlayer[colorNum] = false;
                    selectedNPCPlayer[colorNum] = true;
                    player_Index.remove(Integer.valueOf(String.valueOf(playerNumber)), colors_Color[colorNum].invert());
                }
                else
                {
                    playerWindow.setStyle("""
                    
                        -fx-background-color:transparent ;
                        -fx-background-radius:0;
                        -fx-border-color:gray;
                        -fx-border-width: 10 10 10 10;
                    """);
                    selectedHumanPlayer[colorNum] = false;
                    selectedNPCPlayer[colorNum] = false;
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
     *
     */
    private void setupPlaySession()
    {
       JsonLoader createBoardJSON = new JsonLoader();
       gameBoard = createBoardJSON.startUp("Tiles.Json","OpportunityCard.Json","LuckCard.Json");
       for (Tile tiles : gameBoard.getTiles())
       {
           if (tiles instanceof TileProperty)
           {
               ((TileProperty) tiles).setOwner(gameBoard.getBank());
           }
       }
       if (player_Index.size() < 5) playerInformation = new PlayerInformation[player_Index.size()+1];
       else playerInformation = new PlayerInformation[player_Index.size()];

       //Initialising the players for the game session.
       for (int i = 0; i < player_Index.size(); i++)
       {
           int playerNum = i;
           gameBoard.addPlayer(new HumanPlayer());
           gameBoard.getPlayer(i).addMoney(1500);
           gameBoard.getPlayer(i).setPosition(0);
           playerInformation[i] = new PlayerInformation(player_Index.get(i).toString(),player_Index.get(i),i,tokenPlayer.get(i));
           playerInformation[i].getPlayerToken().setOnMouseClicked(e ->
           {
               Alert listOfBuildings = new Alert(AlertType.INFORMATION);
               listOfBuildings.setHeaderText("Property List of Player " + (playerInformation[playerNum].getPlayerNumber()+1));
               listOfBuildings.setContentText(getPropertyString(playerNum));
               listOfBuildings.showAndWait();
           });
       }

       //AI
        if (gameBoard.getPlayers().size() < 5)
        {
            gameBoard.addPlayer(aiPlayer);
            gameBoard.getPlayer(gameBoard.getPlayers().size() - 1).addMoney(1500);
            playerInformation[gameBoard.getPlayers().size() - 1] = new PlayerInformation("idk", Color.GRAY, gameBoard.getPlayers().size() - 1, "file:resources/Tokens/catto.png");
            playerInformation[gameBoard.getPlayers().size() - 1].getPlayerToken().setOnMouseClicked(e ->
            {
                Alert listOfBuildings = new Alert(AlertType.INFORMATION);
                listOfBuildings.setHeaderText("Property List of Player " + (playerInformation[gameBoard.getPlayers().size() - 1].getPlayerNumber() + 1));
                listOfBuildings.setContentText(getPropertyString(gameBoard.getPlayers().size() - 1));
                listOfBuildings.showAndWait();
            });
        }
        //Creating new player Icon Button
       currPlayerIcon = new Button();
       currPlayerIcon.setMinHeight(100);
       currPlayerIcon.setMinWidth(100);
       currPlayerIcon.setStyle(playerInformation[playerTurn].getPlayerToken().getStyle());
    }

    /**
     * Gets the list of properties
     *
     * @param playerNum Index of player calling the method
     *
     * @return String containing all the properties of the player
     */
    private String getPropertyString(int playerNum)
    {
        StringBuilder list_of_properties = new StringBuilder();
        for(int i = 0; i < gameBoard.getPlayer(playerNum).getProperties().size(); i++)
        {
            list_of_properties.append(i + 1).append(" : ").append(gameBoard.getPlayer(playerNum).getProperties().get(i).getName()).append("\n");
        }
        return String.valueOf(list_of_properties);
    }

    /**
     * The createBoard() method returns a gridPane containing a board with its tiles.
     * It also contains two boxes representing the dices
     *
     * @return Returns a grid pane, which represents the gaming board with all of its tiles.
     */
    private GridPane createBoard()
    {
        //count is used to keep track of the png in the Base folder
        int count = 0;
        //Creating grid pane to store the board
        GridPane gridPane = new GridPane();

        //Top side of the board (0,i)
        for(int i = 0; i < 11; i++)
        {
            count = tileCreation(count, gridPane, 0, i);
        }

        //Right side of the board (i,9)
        for(int i = 1; i < 10; i++)
        {
            //Setting up the Button for each tile
            count = tileCreation(count, gridPane, i, 10);
        }

        //Bottom side of the board
        for(int i = 10; i >= 0; i--)
        {
            //Setting up the Button for each tile
            count = tileCreation(count, gridPane, 10, i);

        }

        //Left side of the board
        for(int i = 9; i > 0; i--)
        {
            //Setting up the Button for each tile
            count = tileCreation(count, gridPane, i, 0);
        }
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
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
        return gridPane;
    }

    /**
     * This method is simply used to save some space. It creates the visualization of a Tile Object.
     *
     * @param count The index of the tile being created
     * @param gridPane the gridPane that will have the tile inserted into
     * @param i the height of the board
     * @param j the width of the board
     * @return the count index
     */
    private int tileCreation(int count, GridPane gridPane, int i, int j) {
        //Setting up the Button for each tile
        int cardNum = count;

        //Set Tile Name depending on Object Class
        Button tileButton = new Button(gameBoard.getTile(count).getName());

        //Set Tile Visuals

        tileButton.setPadding(new Insets(5));
        tileButton.setPrefWidth(150);
        tileButton.setPrefHeight(150);
        tileButton.setOnAction(e->
        {
                BorderPane mainPane = new BorderPane();
                VBox cardInfoIDK = new VBox();
                if (gameBoard.getTile(cardNum) instanceof  TileBuilding)
                {
                    cardInfoIDK.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                            ((TileBuilding) gameBoard.getTile(cardNum)).getHexColour() + " 20%, white 0%);" +
                            "-fx-border-color:black;");
                }
                else
                {
                    cardInfoIDK.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                             "gray 20%, white 0%);" +
                            "-fx-border-color:black;");
                }
                Text[] cardInfo = updateCardInfo(cardNum);
                VBox mainBox = new VBox();
                mainBox.setAlignment(Pos.CENTER);
                for(int x = 2; x < cardInfo.length; x++)
                {
                    mainBox.getChildren().add(cardInfo[x]);
                    BorderPane.setAlignment(cardInfo[x],Pos.CENTER);
                    System.out.println(4);
                }
                mainPane.setTop(cardInfo[0]);
                BorderPane.setAlignment(cardInfo[0],Pos.CENTER);
                mainPane.setCenter(mainBox);
                BorderPane.setAlignment(mainBox,Pos.CENTER);
                mainBox.setAlignment(Pos.BOTTOM_CENTER);
                cardInfoIDK.getChildren().add(mainPane);
                Alert cardStage = new Alert(AlertType.INFORMATION);
                cardStage.setHeaderText("");
                cardStage.setTitle("Tile Information");
                cardStage.setGraphic(cardInfoIDK);
                cardStage.setResizable(false);
                cardStage.show();
        });
        if (gameBoard.getTile(count) instanceof TileBuilding)
        {
            tileButton.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                    ((TileBuilding) gameBoard.getTile(cardNum)).getHexColour() + " 20%, white 0%);\n" +
                    "-fx-background-radius: 0;" + "-fx-border-color:black;");
            tileButton.setWrapText(true);
        }
        else
        {
            tileButton.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                    "gray 20%, white 0%);\n" +
                    "-fx-background-radius: 0;" + "-fx-border-color:black;");
            tileButton.setWrapText(true);
        }
        tiles[count] = tileButton;
        gridPane.add(tileButton,i,j);
        count++;
        return count;
    }

    /**
     * Simple method that creates and returns an HBox containing the dice faces (As Buttons)
     *
     * @return Returns an HBox containing Buttons dice1 and dice2
     */
    private HBox dices ()
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
    private Text[] updateCardInfo(int tileNum)
    {
        if (gameBoard.getTile(tileNum) instanceof TileProperty)
        {
            Text[] cardInformation = new Text[4];
            cardInformation[0] = createText("\n" + gameBoard.getTile(tileNum).getName(), 30, Color.WHITE, "arial");
            cardInformation[0].setStroke(Color.BLACK);
            cardInformation[0].setFill(Color.WHITE);

            if (gameBoard.getTile(tileNum) instanceof TileProperty)
            {
                if (gameBoard.getTile(tileNum) instanceof TileBuilding)
                {
                    String neighbours = "===Neighbourhood===\n";
                    for (TileProperty building : ((TileBuilding) gameBoard.getTile(tileNum)).getNeighborhood())
                    {
                        neighbours += building.getName() + "\n";
                    }
                    String rentPrice = "===Rent===\n";
                    for (int i = 0; i < ((TileBuilding) gameBoard.getTile(tileNum)).getRent().size(); i++)
                    {
                        rentPrice += i + " : " + ((TileBuilding) gameBoard.getTile(tileNum)).getRent().get(i) + "\n";
                    }
                    cardInformation[2] = createText("\nCurrent Development: " + ((TileBuilding) gameBoard.getTile(tileNum)).getDevelopment() + "\n" + "\n" + neighbours + "\n" + rentPrice, 15, Color.BLACK, "arial");
                }
                else cardInformation[2] = createText("\n\n\nNo Development", 20, Color.BLACK, "arial");

                cardInformation[3] = createText("Price: " + ((TileProperty) gameBoard.getTile(tileNum)).getPrice(), 20, Color.BLACK, "arial");
            }
            else
            {
                cardInformation[2] = createText("\n\n\n\n\nNo Development", 20, Color.BLACK, "arial");

                cardInformation[3] = createText("No Price", 20, Color.BLACK, "arial");
            }
            return cardInformation;
        }
            return new Text[]{createText("\n" + gameBoard.getTile(tileNum).getName(), 30, Color.WHITE, "arial")};
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
    private Text createText(String content, int size, Color col, String font)
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
    private double getCoordinates(char axis, int tileNum, int playerNumber)
    {
        double location = 0;
        if (axis == 'X')
        {
                location =  board.getChildren().get(tileNum).getLayoutX()-435+(playerNumber*3);
        }
        else if (axis == 'Y')
        {
                location =  board.getChildren().get(tileNum).getLayoutY()-300;
        }
        return location;
    }

    /**
     * choosePlayerOrder() is called before the start of the game. The method will allow players to choose the playing
     * order based on dice rolls. Everyone will be able to roll once, and the order will be from Descending of said value
     *
     */
    private void choosePlayerOrder()
    {
        //Initialise up Stage, Scene, BorderPane, VBox, ListView
        Stage playerOrderStage = new Stage();
        playerOrderStage.setResizable(false);
        BorderPane mainPane = new BorderPane();
        VBox mainControls = new VBox();
        ListView<TextField> playerRollVisuals = new ListView<>();

        //Preparing titleMenu of the scene
        Text mainTitle = createText("Roll Dices to Choose Player Order!",70,Color.BLACK,"arial");
        mainPane.setTop(mainTitle);
        BorderPane.setAlignment(mainTitle,Pos.CENTER);

        //Preparing the player icons and associated space to insert roll values
        GridPane playerInfo_RollVal = new GridPane();
        for (int i = 0; i < player_Index.size(); i++)
        {
            //Create text-field to insert roll value
            TextField playerRollTextField = new TextField();
            playerRollTextField.setEditable(false);
            playerRollVisuals.getItems().add(playerRollTextField);

            //Create playerIcon to show player
            Button playerIcon = new Button();
            playerIcon.setMinWidth(100);
            playerIcon.setMinHeight(100);
            playerIcon.setStyle("-fx-background-color: #" + player_Index.get(i).toString().substring(2));
            playerInfo_RollVal.add(playerIcon,0,i);
            playerInfo_RollVal.add(playerRollTextField,1,i);
        }
        playerRollVisuals.getSelectionModel().select(0);

        //Inserting gridPane into VBox
        mainControls.getChildren().add(playerInfo_RollVal);


        //Setup Dice Visuals
        HBox dicesIDK = dices();
        dicesIDK.setAlignment(Pos.CENTER);
        mainControls.getChildren().add(dicesIDK);

        //Preparing Roll Button
        Button roll = new Button("Roll");
        roll.setMinHeight(100);
        roll.setMinHeight(50);
        roll.setOnAction(e ->
        {
            //Roll Dices
            rollDices();
            boolean alreadyExists = false;
            String rollDicesValues = String.valueOf(dices.getDiceValues().get(0) + dices.getDiceValues().get(1));

            for (int i = 0; i < playerRollVisuals.getItems().size(); i++)
            {
                if (Objects.equals(playerRollVisuals.getItems().get(i).getText(), rollDicesValues))
                {
                    alreadyExists = true;
                }
            }
            if (!alreadyExists)
            {
                //Place the dice values into the selection Model
                playerRollVisuals.getSelectionModel().getSelectedItem().setText(rollDicesValues);
                //Select next player
                playerRollVisuals.getSelectionModel().selectNext();
                //If last player rolls
                if (playerRollVisuals.getSelectionModel().getSelectedIndex()+1 == player_Index.size() && !Objects.equals(playerRollVisuals.getSelectionModel().getSelectedItem().getText(), ""))
                {
                    //Create a hashmap to connect playerNumber to their roll value
                    HashMap<Integer, Integer> playerHashMap = new HashMap<>();
                    for (int i = 0; i < playerRollVisuals.getItems().size(); i++)
                    {
                        playerHashMap.put(Integer.valueOf(playerRollVisuals.getItems().get(i).getText()), i);
                    }

                    //Create Alert, showing the new ordering
                    Alert done = new Alert(AlertType.INFORMATION);
                    done.setHeaderText("The game is about to begin!");
                    done.setContentText("Player Selection Finished!\nPlease Select your desired token.");
                    getPlayerOrder(playerHashMap);

                    //Once closed, the game will begin
                    done.setOnCloseRequest(a ->
                    {
                        tokenPlayer = new HashMap<>();
                        ArrayList<String> tokenList = getTokens();
                        for (int i = 0; i < player_Index.size(); i++)
                        {
                            ListView<ImageView> tokenRemainign = new ListView<>();
                            for (String s : tokenList)
                            {
                                Image image = new Image(s);
                                ImageView finalToken = new ImageView(image);
                                finalToken.setFitWidth(80);
                                finalToken.setFitHeight(80);
                                tokenRemainign.setStyle("-fx-border-color: #" + player_Index.get(i).toString().substring(2));
                                tokenRemainign.getItems().add(finalToken);
                            }
                            int currPlayer = i;
                            Alert chooseToken = new Alert(AlertType.CONFIRMATION);
                            chooseToken.setHeaderText("Token List");
                            Text selectToken = createText("Player " + (i+1) + " select your Token",20,player_Index.get(i),"arial");
                            chooseToken.setContentText(selectToken.getText());
                            chooseToken.setGraphic(tokenRemainign);
                            chooseToken.setOnCloseRequest(lam ->
                            {
                                if (tokenRemainign.getSelectionModel().getSelectedItem() != null)
                                {
                                    tokenPlayer.put(currPlayer,tokenRemainign.getSelectionModel().getSelectedItem().getImage().getUrl());
                                }
                                else
                                {
                                    lam.consume();
                                }
                            });
                            chooseToken.showAndWait();
                        }
                        playerOrderStage.close();
                        setupPlaySession();
                        gameBoard();
                    });
                    done.showAndWait();
                }
            }
            else
            {
                Alert sameValueAlert = new Alert(AlertType.WARNING);
                sameValueAlert.setContentText("Roll Value of " + rollDicesValues + " already obtained by another player!\nTry Again!");
                sameValueAlert.showAndWait();
            }
        });
        //Adding nodes to VBox
        playerInfo_RollVal.setAlignment(Pos.CENTER);
        mainControls.getChildren().add(roll);
        mainControls.setAlignment(Pos.CENTER);
        mainPane.setCenter(mainControls);

        //Preparing stage and scene
        playerOrderStage.setScene(new Scene(mainPane,screenBounds.getMaxX(),screenBounds.getMaxY()));
        playerOrderStage.show();
    }

    /**
     * Simple rollDice() method that roll the dice object, and changes the dice1 and dice2 faces.
     *
     */
    private void rollDices() {
        //Roll the actual Dice Object
        dices.rollDice();

        //Setup dice1
        ImageView first = new ImageView(facePNG.get(dices.getDiceValues().get(0)-1));
        first.setFitWidth(60);
        first.setFitHeight(60);
        dice1.setGraphic(first);

        //Setup dice2
        ImageView second = new ImageView(facePNG.get(dices.getDiceValues().get(1)-1));
        second.setFitWidth(60);
        second.setFitHeight(60);
        dice2.setGraphic(second);
    }

    /**
     * getPlayerOrder() returns the order (from the highest dice roll values to lowest) in String form
     * The String return value is temporary
     *
     * @param playerRolls HashMap<Integer,Integer> Where the first value is the dice roll and the second is the player index
     *
     */
    private void getPlayerOrder(HashMap<Integer,Integer> playerRolls)
    {
        //Placeholder
        PriorityQueue<Integer> playerOrderInt = new PriorityQueue<>(Collections.reverseOrder());

        //Get the keys of the hashmap (roll values) and insert into priority queue
        for (Map.Entry<Integer,Integer> entry: playerRolls.entrySet())
        {
            playerOrderInt.add(entry.getKey());
        }
        int count = 0;
        HashMap<Integer,Color> newPlayerIndex = new HashMap<>();
        while (playerOrderInt.peek() != null)
        {
            int currPlayer = playerRolls.get(playerOrderInt.poll());
            newPlayerIndex.put(count,player_Index.get(currPlayer));
            count++;
        }
        player_Index = newPlayerIndex;
        //Loop and get the player corresponding to the priority queue
    }

    /**
     *
     */
    private void agentPlayerTurn()
    {
        String aiMessage = "";
        PauseTransition transition = new PauseTransition(Duration.seconds(0.5));
        transition.setOnFinished(event -> moneyOfPlayer.setFill(Color.BLACK));
        int initialMoney = aiPlayer.getBalance();
        //start here
        AIReport report = aiPlayer.takeTurn(gameBoard.getPlayers().size()-1,dices, gameBoard);
        boolean turnOngoing = true;
        while (turnOngoing) {
            AIEvent event = report.getNextEvent();
            if (event != null) {
                aiMessage += event.getDescription() + "\n";
                switch (event.getEvent()) {
                    case DiceRoll -> {
                        //update dice
                    }
                    case DiceRollDouble -> {
                        //update dice, call AIPlayer.takeTurn again after finished processing his turn
                        rolledDouble++;
                        if (rolledDouble == 3)
                        {
                            Alert goingToJail = new Alert(AlertType.WARNING);
                            goingToJail.setContentText("Player " + (playerTurn+1) + " has rolled double twice!\nGoing to Jail!");
                            gameBoard.getPlayer(playerTurn).toJail();
                            playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                            playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                        }
                        else agentPlayerTurn();
                    }
                    case Move, GoneToJail -> {
                        //update token
                        playerInformation[playerTurn].getPlayerToken().setTranslateY(getCoordinates('Y',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                        playerInformation[playerTurn].getPlayerToken().setTranslateX(getCoordinates('X',gameBoard.getPlayer(playerTurn).getPosition(),playerTurn));
                    }
                    case PaidTax, PaidRent -> {
                        //should be handled by AI
                    }
                    //not implemented yet
                    case FreeParking -> {
                        //handled by AI
                    }
                    case OwnProperty -> {
                        //handled by AI
                    }
                    case PropertyPurchase -> {
                        //update property, actual purchase handled by AI
                    }
                    case HousePurchase, HouseSell -> {
                        //Change Tile Color
                        int developmentPercentage = ((((TileBuilding) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getDevelopment()) * 10) + 30;
                        Color tileColour = Color.valueOf(((TileBuilding) gameBoard.getTile(gameBoard.getPlayer(playerTurn).getPosition())).getHexColour());
                        tiles[gameBoard.getPlayer(playerTurn).getPosition()].setStyle("-fx-background-color: linear-gradient(to bottom, #" +
                                tileColour.toString().substring(2) + " " + developmentPercentage + "%, white 0%);\n" + "-fx-background-radius: 0");
                    }
                    case PropertySell -> {
                        //update property, actual sale handled by AI
                    }
                    case Bankrupt -> {
                        turnOngoing = false;
                    }
                }
            }
            else {
                turnOngoing = false;
            }
        }
        if (aiPlayer.getBalance() > initialMoney)
        {
            moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
            moneyOfPlayer.setFill(Color.GREEN);
            transition.playFromStart();
        }
        else if (aiPlayer.getBalance() < initialMoney)
        {
            moneyOfPlayer.setText(String.valueOf(gameBoard.getPlayer(playerTurn).getBalance()));
            moneyOfPlayer.setFill(Color.RED);
            transition.playFromStart();
        }
        aiLogBox.setText(aiMessage);
        playerInformation[playerTurn].incrementPlayerTurn();
        finishedTurn = true;
        newTurn.fire();
    }

    /**
     * Simple method that gets all the tokens from the token resource folder, and places them in an ArrayList
     *
     * @return ArrayList of the URL of the tokens
     */
    private ArrayList<String> getTokens()
    {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(0, "file:resources/Tokens/catto.png");
        tokens.add(1, "file:resources/Tokens/armando.png");
        tokens.add(2, "file:resources/Tokens/fe.png");
        tokens.add(3, "file:resources/Tokens/ponyo.png");
        tokens.add(4, "file:resources/Tokens/woody.png");
        tokens.add(5, "file:resources/Tokens/2000s.png");
        return tokens;
    }
}