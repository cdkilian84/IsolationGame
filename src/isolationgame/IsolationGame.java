//Christopher Kilian
//CS 420 - Spring 2018
//Programming Project #3 - Isolation Game

package isolationgame;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class IsolationGame {
    private GameBoard theBoard;
    private long timeLimit;
    private BoardVals playerTurn;
    private AIPlayer playerX; //defaults to player X being the AI player
    private List<String> playerXMoves;
    private List<String> playerOMoves;
    
    //A game must know who the first player is and what the time limit is (time limit in milliseconds)
    public IsolationGame(BoardVals whoFirst, long timeLimit){
        if(whoFirst == BoardVals.PLAYER_O){
            playerTurn = BoardVals.PLAYER_O;
        }else{
            playerTurn = BoardVals.PLAYER_X;
        }
        //default minimum value of 500 milliseconds for time limit
        if(timeLimit > 500){
            this.timeLimit = timeLimit;
        }else{
            this.timeLimit = 500;
        }
        
        theBoard = new GameBoard(playerTurn);
        playerX = new AIPlayer();
        playerXMoves = new ArrayList<>();
        playerOMoves = new ArrayList<>();
    }
    
    //Method to make a human turn in the game - assumes human playing as player O
    //Returns true if the move was successfully made, false otherwise. Only allows a move to be made if it's currently the human players turn.
    public boolean processHumanTurn(String row, int column){
        boolean turnSuccess = false;
        
        if(playerTurn == BoardVals.PLAYER_O){
            if(theBoard.attemptToMove(playerTurn, row, column)){
                turnSuccess = true;
                playerTurn = BoardVals.PLAYER_X; //since human turn was successful, set turn to AI player (player X)
                playerOMoves.add(row+column);
            }
        }
        
        return turnSuccess;
    }
    
    
    //Method to make the AI take their turn - only allows turn to occur if it's their turn (player X turn).
    //Returns true if the move was successfully made, false otherwise.
    public boolean processAITurn(){
        boolean turnSuccess = false;
        if(playerTurn == BoardVals.PLAYER_X){ //only allow AI turn if it's their turn
            GameMove aiMove = playerX.getAIMove(theBoard, playerTurn, timeLimit);

            if(playerX.makeAIMove(theBoard, aiMove)){
                turnSuccess = true;
                playerTurn = BoardVals.PLAYER_O; //since move was successful, change player turn to other player (Player O)
                int adjustedCol = aiMove.getColumn() + 1;
                playerXMoves.add(theBoard.getInverseRowMap().get(aiMove.getRow()) + adjustedCol);
            }else{
                System.out.println("There was an error with the AI in making its turn! Turn not completed successfully!");
            }
        }else{
            System.out.println("It is not Player X's turn!");
        }
        return turnSuccess;
    }
    
    //returns the value of the winner - if nobody has won yet, returns the default "NO_WINNER" value
    public BoardVals getGameWinner(){
        BoardVals winner = BoardVals.NO_WINNER;
        
        if(theBoard.checkIfPlayerLose(BoardVals.PLAYER_O)){
            winner = BoardVals.PLAYER_X;
        }else if(theBoard.checkIfPlayerLose(BoardVals.PLAYER_X)){
            winner = BoardVals.PLAYER_O;
        }
        
        return winner;
    }
    
    //getter for the game board
    public GameBoard getGameBoard(){
        return theBoard;
    }
    
    //getter for whose turn it is
    public BoardVals getPlayerTurn(){
        return playerTurn;
    }
    
    //getter for list of human-readable AI moves
    public List<String> getPlayerXMoves(){
        return playerXMoves;
    }
    
    //getter for list of human-readable player moves
    public List<String> getPlayerOMoves(){
        return playerOMoves;
    }
    
}
