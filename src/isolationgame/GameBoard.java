//Christopher Kilian
//CS 420 - Spring 2018
//Programming Project #3 - Isolation Game

package isolationgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//Class which implements the game board state - this includes the representation of the game board itself as a 2D array, and the various methods that can
//act to change the game board state (moving a player, checking the move coordinates, etc). It also includes the evaluator method to evaluate how good a current
//player position is (for use by the minimax algorithm).
public class GameBoard {
    private final int BOARD_DIMENSION = 8;
    private int boardFreeSpaces;
    private Map<String, Integer> rowMap; //maps values A-H to their respective integer values representing the rows
    private Map<Integer, String> inverseRowMap; //does the opposite of rowMap (mainly for use in printing the board)
    private int[] playerX; //holds (row,column) coordinates for each player (for ease of access)
    private int[] playerO;
    private BoardVals[][] theBoard;
    
    
    //Constructor - takes the starting player so it knows where to position the players (starting player is always upper-left)
    public GameBoard(BoardVals startingPlayer){
        theBoard = new BoardVals[BOARD_DIMENSION][BOARD_DIMENSION];
        initMaps();
        playerX = new int[2];
        playerO = new int[2];
        //coin flip to see if player X or player O starts
        if(startingPlayer == BoardVals.PLAYER_O){
            initBlankBoard(BoardVals.PLAYER_O);
        }else{
            initBlankBoard(BoardVals.PLAYER_X);
        }
        boardFreeSpaces = (BOARD_DIMENSION * BOARD_DIMENSION) - 2; //all spaces are free except the two initial points occupied by the players
    }
    
    
    //Constructor which builds a new gameboard based on an existing game board, a designated player, and a move coordinate (used to generate new game states
    //for minimax algorithm)
    //NOTE: To avoid extra uncessesary processing time when traversing a tree, moveToCoords are NOT CHECKED HERE - validate coordinates BEFORE invoking this
    //constructor
    public GameBoard(GameBoard currentBoard, BoardVals playerToMove, GameMove moveToCoords){
        deepCopyBoard(currentBoard);
        this.rowMap = currentBoard.getRowMap();
        this.inverseRowMap = currentBoard.getInverseRowMap();
        setPlayerPos(playerToMove, moveToCoords.getRow(), moveToCoords.getColumn());
    }
    
    
    //Method for deep copying of a specified game board so board arrays are not referencing a different board object
    private void deepCopyBoard(GameBoard boardToCopy){
        BoardVals[][] toCopy = boardToCopy.getTheBoard();
        int[] copyingPlayerX = boardToCopy.getPlayerX();
        int[] copyingPlayerO = boardToCopy.getPlayerO();
        this.theBoard = new BoardVals[BOARD_DIMENSION][BOARD_DIMENSION];
        this.playerO = new int[2];
        this.playerX = new int[2];
        
        for(int i = 0; i < BOARD_DIMENSION; i++){
            for(int j = 0; j < BOARD_DIMENSION; j++){
                this.theBoard[i][j] = toCopy[i][j];
            }
        }
        
        //playerX and playerO have same length - copy both in single loop
        for(int i = 0; i < boardToCopy.getPlayerO().length; i++){
            this.playerO[i] = copyingPlayerO[i];
            this.playerX[i] = copyingPlayerX[i];
        }
        
    }
    
    
    //Method used to initialize a new un-played game board (blank board)
    private void initBlankBoard(BoardVals startingPlayer){
        //set all spaces to blank initially
        for(int i = 0; i < BOARD_DIMENSION; i++){
            for(int j = 0; j < BOARD_DIMENSION; j++){
                theBoard[i][j] = BoardVals.BLANK;
            }
        }
        
        //set player X and O in their respective corners - starting player is always in upper-left corner (0, 0)
        if(startingPlayer == BoardVals.PLAYER_O){
            playerO[0] = 0;
            playerO[1] = 0;
            playerX[0] = (BOARD_DIMENSION - 1);
            playerX[1] = (BOARD_DIMENSION - 1);
            theBoard[getPlayerO()[0]][getPlayerO()[1]] = BoardVals.PLAYER_O;
            theBoard[getPlayerX()[0]][getPlayerX()[1]] = BoardVals.PLAYER_X;
        }else{
            playerX[0] = 0;
            playerX[1] = 0;
            playerO[0] = (BOARD_DIMENSION - 1);
            playerO[1] = (BOARD_DIMENSION - 1);
            theBoard[getPlayerO()[0]][getPlayerO()[1]] = BoardVals.PLAYER_O;
            theBoard[getPlayerX()[0]][getPlayerX()[1]] = BoardVals.PLAYER_X;
        }
    }
    
    
    //Method used by constructors to initialize the game maps
    private void initMaps(){
        rowMap = new HashMap();
        inverseRowMap = new HashMap();
        getRowMap().put("A", 0);
        getRowMap().put("B", 1);
        getRowMap().put("C", 2);
        getRowMap().put("D", 3);
        getRowMap().put("E", 4);
        getRowMap().put("F", 5);
        getRowMap().put("G", 6);
        getRowMap().put("H", 7);
        getInverseRowMap().put(0, "A");
        getInverseRowMap().put(1, "B");
        getInverseRowMap().put(2, "C");
        getInverseRowMap().put(3, "D");
        getInverseRowMap().put(4, "E");
        getInverseRowMap().put(5, "F");
        getInverseRowMap().put(6, "G");
        getInverseRowMap().put(7, "H");
    }
    
    
    //Method to evaluate the "goodness" of a specified players current position - this method is used by the minimax algorithm
    //to determine the value of the current board state represented by this GameBoard object.
    //In this case, the players position is evaluated as a measure of the possible moves they have from their current position minus
    //the number of moves available to their opponent. The opponents moves are multiplied by 2 to reflect the desire of the player to maximize
    //their own moves available while minimizing their opponents.
    public int evaluatePlayerPosition(BoardVals player){
        int score;
        BoardVals opponent;
        if(player == BoardVals.PLAYER_O){
            opponent = BoardVals.PLAYER_X;
        }else{
            opponent = BoardVals.PLAYER_O;
        }
        
        if(checkIfPlayerLose(player)){
            score = Integer.MIN_VALUE;
        }else if(checkIfPlayerLose(opponent)){
            score = Integer.MAX_VALUE;
        }else{
            List<GameMove> playerMoves = getPossibleMoves(player);
            List<GameMove> opponentMoves = getPossibleMoves(opponent);
            score = playerMoves.size() - (2 * opponentMoves.size());
        }

        return score;
    }
    
    
    //Method to get all of the possible legal moves that an indicated player could make
    //This method does NOT use the "validatePlayerMove" method because it would be less efficient that way 
    //(rechecking spaces multiple times unnecessarily)
    public List<GameMove> getPossibleMoves(BoardVals player){
        List<GameMove> possibleMoves = new ArrayList<>();
        int playerCurrentRow;
        int playerCurrentCol;
        if(player == BoardVals.PLAYER_O){
            playerCurrentRow = getPlayerO()[0];
            playerCurrentCol = getPlayerO()[1];
        }else{
            playerCurrentRow = getPlayerX()[0];
            playerCurrentCol = getPlayerX()[1];
        }
        
        //get vertical moves
        int checkRow = playerCurrentRow - 1;
        //check movement up
        while((checkRow >= 0) && checkSpace(checkRow, playerCurrentCol)){
            possibleMoves.add(new GameMove(checkRow, playerCurrentCol, player));
            checkRow--;
        }
        //check movement down
        checkRow = playerCurrentRow + 1;
        while((checkRow < BOARD_DIMENSION) && checkSpace(checkRow, playerCurrentCol)){
            possibleMoves.add(new GameMove(checkRow, playerCurrentCol, player));
            checkRow++;
        }
        
        //get horizontal moves
        int checkCol = playerCurrentCol - 1;
        //check movement left
        while((checkCol >= 0) && checkSpace(playerCurrentRow, checkCol)){
            possibleMoves.add(new GameMove(playerCurrentRow, checkCol, player));
            checkCol--;
        }
        //check movement right
        checkCol = playerCurrentCol + 1;
        while((checkCol < BOARD_DIMENSION) && checkSpace(playerCurrentRow, checkCol)){
            possibleMoves.add(new GameMove(playerCurrentRow, checkCol, player));
            checkCol++;
        }
        
        //get diagonal moves
        //up and left
        checkRow = playerCurrentRow - 1;
        checkCol = playerCurrentCol - 1;
        while((checkRow >= 0) && (checkCol >= 0) && checkSpace(checkRow, checkCol)){
            possibleMoves.add(new GameMove(checkRow, checkCol, player));
            checkRow--;
            checkCol--;
        }
        //up and right
        checkRow = playerCurrentRow - 1;
        checkCol = playerCurrentCol + 1;
        while((checkRow >= 0) && (checkCol < BOARD_DIMENSION) && checkSpace(checkRow, checkCol)){
            possibleMoves.add(new GameMove(checkRow, checkCol, player));
            checkRow--;
            checkCol++;
        }
        //down and right
        checkRow = playerCurrentRow + 1;
        checkCol = playerCurrentCol + 1;
        while((checkRow < BOARD_DIMENSION) && (checkCol < BOARD_DIMENSION) && checkSpace(checkRow, checkCol)){
            possibleMoves.add(new GameMove(checkRow, checkCol, player));
            checkRow++;
            checkCol++;
        }
        //down and left
        checkRow = playerCurrentRow + 1;
        checkCol = playerCurrentCol - 1;
        while((checkRow < BOARD_DIMENSION) && (checkCol >= 0) && checkSpace(checkRow, checkCol)){
            possibleMoves.add(new GameMove(checkRow, checkCol, player));
            checkRow++;
            checkCol--;
        }
        
        return possibleMoves;
    }
    
    
    //Method to be used when moving a player piece from one place on the board to another (checks validity of move - returns true if move is valid)
    private boolean validatePlayerMove(BoardVals player, int row, int column){
        boolean validMove = true;
        int playerCurrentRow;
        int playerCurrentCol;
        //get the current row and column of the indicated player for use in this method
        if(player == BoardVals.PLAYER_O){
            playerCurrentRow = getPlayerO()[0];
            playerCurrentCol = getPlayerO()[1];
        }else{
            playerCurrentRow = getPlayerX()[0];
            playerCurrentCol = getPlayerX()[1];
        }
        
        if((playerCurrentRow == row) && (playerCurrentCol == column)){ //it's invalid to attempt to stay in the same position!
            validMove = false;
        }else if(!checkInBounds(row, column)){ //check bounds - move outside board bounds is invalid
            validMove = false;
        }else if(row == playerCurrentRow){ //horizontal movement
            if(playerCurrentCol > column){ //decrement - move left
                for(int i = (playerCurrentCol - 1); i >= column; i--){
                    if(!checkSpace(playerCurrentRow, i)){
                        validMove = false;
                        break;
                    }
                }
            }else{ //increment - move right
                for(int i = (playerCurrentCol + 1); i <= column; i++){
                    if(!checkSpace(playerCurrentRow, i)){
                        validMove = false;
                        break;
                    }
                }
            }
        }else if(column == playerCurrentCol){ //vertical movement
            if(playerCurrentRow > row){ //decrement - move up
                for(int i = (playerCurrentRow - 1); i >= row; i--){
                    if(!checkSpace(i, playerCurrentCol)){
                        validMove = false;
                        break;
                    }
                }
            }else{ //increment - move down
                for(int i = (playerCurrentRow + 1); i <= row; i++){
                    if(!checkSpace(i, playerCurrentCol)){
                        validMove = false;
                        break;
                    }
                }
            }
        }else if(Math.abs(playerCurrentRow - row) == Math.abs(playerCurrentCol - column)){ //Note that points are on a diagonal if the row and column differences between the two are the same
            int spacesToMove = Math.abs(playerCurrentRow - row); //diagonally moved spaces are equal to absolute value of both column and row distance
            int checkRow = playerCurrentRow;
            int checkCol = playerCurrentCol;
            
            if((playerCurrentRow > row) && (playerCurrentCol > column)){ //decrement both - move up and left
                for(int i = spacesToMove; i > 0; i--){
                    checkRow--;
                    checkCol--;
                    if(!checkSpace(checkRow, checkCol)){
                        validMove = false;
                        break;
                    }
                }
            }else if((playerCurrentRow > row) && (playerCurrentCol < column)){ //decrement row, increment column - move up and right
                for(int i = spacesToMove; i > 0; i--){
                    checkRow--;
                    checkCol++;
                    if(!checkSpace(checkRow, checkCol)){
                        validMove = false;
                        break;
                    }
                }
            }else if((playerCurrentRow < row) && (playerCurrentCol < column)){ //increment both - move down and right
                for(int i = spacesToMove; i > 0; i--){
                    checkRow++;
                    checkCol++;
                    if(!checkSpace(checkRow, checkCol)){
                        validMove = false;
                        break;
                    }
                }
            }else if((playerCurrentRow < row) && (playerCurrentCol > column)){ //increment row, decrement column - move down and left
                for(int i = spacesToMove; i > 0; i--){
                    checkRow++;
                    checkCol--;
                    if(!checkSpace(checkRow, checkCol)){
                        validMove = false;
                        break;
                    }
                }
            }else{
                validMove = false; //catch-all for something going wrong! In theory this should never be reached
            }
        }else{
            validMove = false; //if it's not vertical, horizontal, or diagonal movement, it's automatically non-valid
        }
        
        return validMove;
    }
    
    
    //Helper method to check a specified board space to see if it's currently occupied by a player, or has already been used
    //returns true if the space is marked "blank" (unused), false otherwise
    private boolean checkSpace(int row, int column){
        boolean spaceEmpty = true;
        
        if(theBoard[row][column] != BoardVals.BLANK){
            spaceEmpty = false;
        }
        
        return spaceEmpty;
    }
    
    
    //Method which returns true if indicated coordinates are in board bounds, false otherwise
    private boolean checkInBounds(int row, int column){
        boolean inBounds = true;
        if((row >= BOARD_DIMENSION) || (row < 0) || (column >= BOARD_DIMENSION) || (column < 0)){
            inBounds = false;
        }
        return inBounds;
    }
    
    
    //Method which sets a new position for the indicated player on the board while also marking their old position as used.
    //Only to be called by the program once the coordinates are deemed valid (hence private access)
    private void setPlayerPos(BoardVals player, int row, int column){
        if(player == BoardVals.PLAYER_O){
            theBoard[getPlayerO()[0]][getPlayerO()[1]] = BoardVals.USED; //set players old location to "used"
            playerO[0] = row;
            playerO[1] = column;
            theBoard[row][column] = BoardVals.PLAYER_O;
        }else{
            theBoard[getPlayerX()[0]][getPlayerX()[1]] = BoardVals.USED; //set players old location to "used"
            playerX[0] = row;
            playerX[1] = column;
            theBoard[row][column] = BoardVals.PLAYER_X;
        }
        boardFreeSpaces--;
    }
    
    
    //Publicly accessible method for moving a player piece - calls appropriate private methods to validate move and then make the move if it's valid.
    //returns true if the move was made, false otherwise. 
    //NOTE: Method must accept column values in 1 based (for user input) - decrement values here before operating on them to compare to
    //stored row and column values (which are 0 based)
    public boolean attemptToMove(BoardVals player, String row, int column){
        boolean moveMade = true;
        int mappedRow = getRowMap().get(row);
        column--;
        
        if(validatePlayerMove(player, mappedRow, column)){
            setPlayerPos(player, mappedRow, column);
        }else{
            moveMade = false;
        }
        
        return moveMade;
    }
    
    
    //Method which checks to see if there is at least 1 empty space around the indicated player - player only loses if all adjacent spaces are
    //out of bounds or already used/occupied. Returns true if the indicated player cannot move (has lost) and false otherwise.
    public boolean checkIfPlayerLose(BoardVals player){
        boolean playerLost = true;
        int[] thePlayer;
        if(player == BoardVals.PLAYER_O){
            thePlayer = getPlayerO();
        }else{
            thePlayer = getPlayerX();
        }
        
        for(int i = -1; i <= 1; i++){ //check rows from current - 1 to current + 1
            for(int j = -1; j <= 1; j++){ //check columns from current - 1 to current + 1
                if((i == 0) && (j == 0)){
                    continue; //ignore space where player currently is located
                }
                int rowToCheck = thePlayer[0] + i;
                int colToCheck = thePlayer[1] + j;
                
                if(checkInBounds(rowToCheck, colToCheck) && (getTheBoard()[rowToCheck][colToCheck] == BoardVals.BLANK)){
                    playerLost = false;
                    break;
                }
            }
            if(!playerLost){ //if "playerLost" is set to false, don't need to do any more checking, so end outer loop
                break;
            }
        }
        
        return playerLost;
    }
    
    
    //Method which returns a string representing the current state of the board
    public String printBoard(){
        StringBuilder boardVisual = new StringBuilder();
        boardVisual.append("  1 2 3 4 5 6 7 8\n");
        for(int i = 0; i < BOARD_DIMENSION; i++){
            boardVisual.append(getInverseRowMap().get(i) + " ");
            for(int j = 0; j < BOARD_DIMENSION; j++){
                switch(getTheBoard()[i][j]){
                    case BLANK:
                        boardVisual.append("- ");
                        break;
                    case USED:
                        boardVisual.append("# ");
                        break;
                    case PLAYER_X:
                        boardVisual.append("X ");
                        break;
                    case PLAYER_O:
                        boardVisual.append("O ");
                        break;
                }
            }
            boardVisual.append("\n");
        }
        
        return boardVisual.toString();
    }

    
    //Getters for all of the various game attributes
    //rowmap getter
    public Map<String, Integer> getRowMap() {
        return rowMap;
    }

    //inverse rowmap getter
    public Map<Integer, String> getInverseRowMap() {
        return inverseRowMap;
    }

    //getter for playerX coordinates
    public int[] getPlayerX() {
        return playerX;
    }

    //getter for playerO coordinates
    public int[] getPlayerO() {
        return playerO;
    }

    //getter for the board array
    public BoardVals[][] getTheBoard() {
        return theBoard;
    }
    
    
//    public int getFreeSpaces(){
//        return boardFreeSpaces;
//    }
    
}
