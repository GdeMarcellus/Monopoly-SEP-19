package backend.Player.AI;

import backend.Board;
import backend.Dice;
import backend.Exception.*;
import backend.Player.Player;
import backend.Tiles.*;

import java.util.concurrent.ThreadLocalRandom;

public class AIPlayer extends Player {
    AIReport report;

    public AIPlayer(Board board) {
        report = new AIReport(board);
    }

    //TODO add double roll, jail functionality
    /**
     * Handles a turn
     * @param myIndex index of this AI player object in Board.players list
     * @param dice dice objects
     * @param board board object
     */
    public AIReport takeTurn(int myIndex, Dice dice, Board board) {
        boolean bankrupt = false;
        report.newTurn();
        //MOVE
        //roll dice
        dice.rollDice();
        // move
        move(dice.getDiceValues());
        Tile currentTile = board.getPlayerTile(myIndex);

        //add move to report
        report.setDiceRoll(dice.getDiceValues());
        if (dice.getDiceValues().get(0).equals(dice.getDiceValues().get(1))) {
            report.addEvent(Event.DiceRollDouble);
        }
        else {
            report.addEvent(Event.DiceRoll);
        }
        report.setLandedTile(currentTile);
        report.addEvent(Event.Move);

        //find out tile type and take appropriate actions
        if (currentTile instanceof TileProperty) {
            bankrupt = tileProperty((TileProperty) currentTile, board, myIndex, (dice.getDiceValues().get(0) + dice.getDiceValues().get(1)));

        }
        else if (currentTile instanceof TileFreeParking) {
            tileFreeParking((TileFreeParking) currentTile);

        }
        else if (currentTile instanceof  TileTax) {
            bankrupt = tileTax((TileTax) currentTile, board, myIndex);

        }
        else if (currentTile instanceof  TileGoToJail) {
            tileGoToJail();

        }
        if (currentTile instanceof TileGo || currentTile instanceof TileJail) {
            //TODO
        }

        if (!bankrupt) {
            //Make improvments
            if (properties.size() != 0) {
                for (TileProperty property : properties) {
                    //if can be improved
                    if (property instanceof TileBuilding) {
                        //if owns neighborhood, can afford and develpment diff < 1
                        if (property.ownsNeighborhood() && ((TileBuilding) property).getDevelopmentCost() < balance && ((TileBuilding) property).developmentDifference() < 1) {
                            ((TileBuilding) property).setDevelopment(((TileBuilding) property).getDevelopment() + 1);
                            this.removeMoney(((TileBuilding) property).getDevelopmentCost());

                            //add to report
                            report.addImprovedProperty((TileBuilding) property);
                            report.addEvent(Event.HousePurchase);
                        }
                    }
                }
            }
        }
        return report;
    }

    //tile actions

    /**
     * Handles landing on a Property tile
     * @param currentTile Tile AI player is on
     * @param board The game board
     * @param myIndex AI player objects index in board playerlist
     * @param diceRoll total of dice roll
     */
    private boolean tileProperty(TileProperty currentTile, Board board, int myIndex, int diceRoll) {
        boolean debt = false;
        //get owner
        Player tileOwner = currentTile.getOwner();
        //purchase?
        if (tileOwner == board.getBank()) {
            //purchase
            if (currentTile.getPrice() < balance) {
                try {
                    board.playerPurchase(myIndex, this.getPosition(), 0);
                } catch (NonPropertyTileException | InsufficientFundsException e) {
                    e.printStackTrace();
                }

                //add to report
                report.setPurchasedProperty(currentTile);
                report.addEvent(Event.PropertyPurchase);
            }
        }
        //pay rent
        else if (tileOwner != this) {
            int moneyOwed = 0;
            int balanceTemp = balance;
            try {
                moneyOwed = currentTile.payRent(this, diceRoll);
            } catch (IsMortgagedException e) {
                //continue
            }

            //check debt
            if (moneyOwed > 0) {
                //TODO loop needed?
                debt = inDebt(moneyOwed, board, myIndex);
                repayDebtToPlayer(currentTile.getOwner(), moneyOwed);
            }

            //add to report
            report.setRentProperty(currentTile);
            report.setRentPaid(balanceTemp - balance);
            report.addEvent(Event.PaidRent);
            if (debt) {
                report.setBankrupt(true);
                report.addEvent(Event.Bankrupt);
            }
        }
        //else own property
        else {
            //landed tile already set
            report.addEvent(Event.OwnProperty);
        }
        return debt;
    }

    /**
     * Handles actions from landing on Free Parking tile
     * @param currentTile Tile AI player currently on
     */
    private void tileFreeParking(TileFreeParking currentTile) {
        //pick up money on tile
        int money = currentTile.payToPlayer(this);
        this.addMoney(money);

        //add report
        report.setFreeParkingMoney(money);
        report.addEvent(Event.FreeParking);
    }


    /**
     * Handles landing on a tax tile
     * @param currentTile tile AI player is currently on
     * @param board the game board
     */
    private boolean tileTax(TileTax currentTile, Board board, int myIndex) {
        boolean debt = false;
        int outstanding = currentTile.payTax(this, (TileFreeParking) board.getTile(board.getFreeParkingPos()));

        //add to report
        if (outstanding > 0) {
            //TODO clean up reporting
            debt = inDebt(outstanding, board, myIndex);
            if (!debt) {
                //put outstanding on free parking
                ((TileFreeParking) board.getTile(board.getFreeParkingPos())).payFine(currentTile.getTax() + this.removeMoney(outstanding));

                report.setTaxMoney(currentTile.getTax());
                report.addEvent(Event.PaidTax);
            }
            else {
                report.setTaxMoney(currentTile.getTax() - outstanding);
                report.addEvent(Event.PaidTax);
                report.setBankrupt(true);
                report.addEvent(Event.Bankrupt);
            }
        }
        else {
            report.setTaxMoney(currentTile.getTax());
            report.addEvent(Event.PaidTax);
        }
        return debt;
    }

    private void tileGoToJail() {
        //TODO report: turnLog.add("I have been sent to jail :(");
        this.toJail();
        //TODO implement jail
    }

    //Debt handling

    /**
     * Repays a debt to a player
     * @param player player debt owed too
     * @param amountOwed amount owed to player
     */
    public void repayDebtToPlayer(Player player, int amountOwed) {
        this.removeMoney(amountOwed);
        player.addMoney(amountOwed);
        //TODO log
    }

    /**
     * Raises funds to attempt to repay debt
     * @param amountOwed size of debt
     * @param board board object
     * @param myIndex index of this AI player object in Board.players list
     * @return whether still in debt
     */
    private boolean inDebt(int amountOwed, Board board, int myIndex) {
        boolean stillInDebt = true;

        if (this.properties.size() != 0) {
            //TODO: algorithm that finds least cost-ly way of setteling debt
            while(stillInDebt) {
                for (TileProperty property : properties) {
                    //try to sell houses
                    if (property instanceof TileBuilding) {
                        if (((TileBuilding) property).getDevelopment() > 0) {
                            try {
                                ((TileBuilding) property).sellHouse(board.getBank());
                                report.addSoldHouse((TileBuilding) property);
                                report.addEvent(Event.HouseSell);
                            } catch (LargeDevelopmentDifferenceException | NoDevelopmentException e) {
                                continue;
                            }
                            if (balance >= amountOwed) {
                                stillInDebt = false;
                                break;
                            }
                        }
                    }
                    //try to sell property
                    try {
                        board.sellToBank(myIndex, board.getTileIndex(property));

                        //add to report
                        report.addSoldProperty((TileProperty) board.getTile(board.getTileIndex(property)));
                        report.addEvent(Event.PropertySell);
                    } catch (OwnershipException | PropertyDevelopedException e) {
                        throw new RuntimeException(e);
                    }
                    if (balance >= amountOwed) {
                        stillInDebt = false;
                        break;
                    }
                }
            }
        }

        return stillInDebt;
    }

    /**
     * Returns an auction bid
     * @param property Property being bid on
     * @return bid
     */
    public int makeBid(TileProperty property, Board board) {
        if (board.auctionHighestBid()[0] < property.getPrice() && board.auctionHighestBid()[0] != -1) {
            return board.auctionHighestBid()[0] + 1;
        }
        return ThreadLocalRandom.current().nextInt((property.getPrice() / 2), (int) (property.getPrice() * 1.1));
    }
}
