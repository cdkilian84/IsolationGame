//Christopher Kilian
//CS 420 - Spring 2018
//Programming Project #3 - Isolation Game

package isolationgame;

//simple class to hold the information about a move (for use in minimax algorithm)

import java.util.Map;

//Just holds row and column coordinates of a move, and the associated player
public class GameMove {
    private int row;
    private int column;
    private BoardVals player;
    private Map<String, Integer> rowMap; //maps values A-H to their respective integer values representing the rows
    private Map<Integer, String> inverseRowMap; //does the opposite of rowMap (mainly for use in printing the board)
    
    public GameMove(int row, int column, BoardVals player){
        this.row = row;
        this.column = column;
        this.player = player;
    }
    
    //constructor to create new copies of another move
    public GameMove(GameMove moveToCopy){
        this.row = moveToCopy.getRow();
        this.column = moveToCopy.getColumn();
        this.player = moveToCopy.getPlayer();
    }
    
    //
    public GameMove(){
        
    }
    
    //getters for the data held in this object
    //getter for row
    public int getRow() {
        return row;
    }

    //getter for column
    public int getColumn() {
        return column;
    }

    //getter for player moved
    public BoardVals getPlayer() {
        return player;
    }

}
