//Christopher Kilian
//CS 420 - Spring 2018
//Programming Project #3 - Isolation Game

package isolationgame;

import java.util.List;
import java.util.Random;

//Class implementing the AI player - this includes the minimax algorithm implementation with alpha-beta pruning and iterative deepening. It includes methods both
//to simply find the next best move, and to implement that move in the current game.
public class AIPlayer {
    
    //This method implements the minimax algorithm. The initial call to this state builds a list of all the possible moves from the provided (current)
    //game state, and manages the timer which will force a return when it runs out. The list of possible moves are iterated through with calls to "maxValue", which
    //is the jumping off point into the minimax algorithm methods (maxValue and minValue). Further, a max-depth value is maintained and iterated on by this method
    //call in order to implement iterative deepening. The move with the highest value seen so far is maintained at all times until this method returns, at which
    //time that highest value move is returned. By maintaining this value across depth iterations, the best move seen so far can always be returned whenever the
    //timer happens to expire.
    public GameMove getAIMove(GameBoard theBoard, BoardVals player, long timeLimit){
        MoveTimer timer = new MoveTimer(timeLimit); //timer starts the instant it's instantiated
        List<GameMove> possibleMoves = theBoard.getPossibleMoves(player); //get all possible moves for player from current state
        GameMove bestMove = possibleMoves.get(0); //default value - pick the first move available
        int maxDepth = 1; //to be iterated as the tree is searched
        int currentScore = Integer.MIN_VALUE;
        
        while(!timer.isTimeElapsed()){ //search for the best move until time expires
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            
            for(GameMove move : possibleMoves){
                if(!timer.isTimeElapsed()){
                    int moveScore = minValue(new GameBoard(theBoard, player, move), player, (maxDepth - 1), alpha, beta, timer);
                    if(currentScore < moveScore){
                        bestMove = move;
                        currentScore = moveScore;
                    }else if(currentScore == moveScore){
                        Random rand = new Random();
                        if(rand.nextDouble() > 0.75){ //injecting randomness into move selection - if a move has an equal score to the current score, there is a 25% chance to accept the new move
                            bestMove = move;
                        }
                    }
                    
                    //pruning
                    if(currentScore >= beta){
                        break;
                    }
                    alpha = Math.max(alpha, currentScore);
                    //System.out.println("bestMove score is " + currentScore);
                }else{
                    break;
                }
            }
            
            maxDepth++; //increment depth with every search iteration
            //System.out.println("Deepening search to depth " + maxDepth);
            if(maxDepth > 200){
                break; //stop iterating on depth after 200 - mainly for ending the search quickly in late-game circumstances with few possible moves
            }
        }
        
        //System.out.println("final accepted score was " + currentScore);
        //System.out.println("Time elapsed was " + timer.getElapsedTime());
        //System.out.println("Final depth reached was " + maxDepth);
        return bestMove;
    }
    
    
    //Method to actually make a move on the indicated board - the passed GameMove should be generated by the getAIMove algorithm
    public boolean makeAIMove(GameBoard board, GameMove aiMove){
        boolean moveMade;
        String aiRow = board.getInverseRowMap().get(aiMove.getRow());
        int aiCol = aiMove.getColumn() + 1;
        moveMade = board.attemptToMove(aiMove.getPlayer(), aiRow, aiCol);
        
        return moveMade;
    }
    
    
    //Method which implementats the "max" part of the minimax algorithm - checks if the current state passed to it is a leaf state (either max depth
    //has been reached or it's a win/lose state) and evaluates its value if it is.
    
    //Accepts a game board representing the current state 
    private int maxValue(GameBoard theBoard, BoardVals player, int depth, int alpha, int beta, MoveTimer timer){
        int currentScore = Integer.MIN_VALUE;
        BoardVals opponent;
        if(player == BoardVals.PLAYER_O){
            opponent = BoardVals.PLAYER_X;
        }else{
            opponent = BoardVals.PLAYER_O;
        }
        //"opponent" is THIS nodes mover - in other words, if Player X moved to get to "theBoard", now generate all possible moves for player O
        
        if(checkIfTerminalState(theBoard, depth) || timer.isTimeElapsed()){
            currentScore = theBoard.evaluatePlayerPosition(player);
        }else{
            List<GameMove> possibleMoves = theBoard.getPossibleMoves(opponent);
            for(GameMove move : possibleMoves){
                if(!timer.isTimeElapsed()){
                    int minScore = minValue(new GameBoard(theBoard, opponent, move), opponent, (depth - 1), alpha, beta, timer);
                    currentScore = Math.max(currentScore, minScore);

                    //pruning
                    if(currentScore >= beta){
                        break;
                    }
                    alpha = Math.max(alpha, currentScore);
                }else{
                    break; //stop searching when time is elapsed
                }
            }
        }
        
        //System.out.println("maxValue is returning score of " + currentScore);
        return currentScore;
    }
    
    
    //theBoard passed is generated by moving "player" to this location
    private int minValue(GameBoard theBoard, BoardVals player, int depth, int alpha, int beta, MoveTimer timer){
        int currentScore = Integer.MAX_VALUE;
        BoardVals opponent;
        if(player == BoardVals.PLAYER_O){
            opponent = BoardVals.PLAYER_X;
        }else{
            opponent = BoardVals.PLAYER_O;
        }
        //"opponent" is THIS nodes mover - in other words, if Player X moved to get to "theBoard", now generate all possible moves for player O
        
        if(checkIfTerminalState(theBoard, depth) || timer.isTimeElapsed()){
            currentScore = theBoard.evaluatePlayerPosition(player);
        }else{
            List<GameMove> possibleMoves = theBoard.getPossibleMoves(opponent);
            for(GameMove move : possibleMoves){
                if(!timer.isTimeElapsed()){
                    int maxScore = maxValue(new GameBoard(theBoard, opponent, move), opponent, (depth - 1), alpha, beta, timer);
                    currentScore = Math.min(currentScore, maxScore);

                    //pruning
                    if(currentScore <= alpha){
                        break;
                    }
                    beta = Math.min(beta, currentScore);
                }else{
                    break;
                }
            }
        }
        
        //System.out.println("minValue is returning score of " + currentScore);
        return currentScore;
    }
    
    //checks if a game board state is terminal - state is terminal if the associated depth is 0, or if one of the players has lost the game
    //returns true if a terminal state, false otherwise
    private boolean checkIfTerminalState(GameBoard theBoard, int depth){
        boolean terminal = false;
        if(theBoard.checkIfPlayerLose(BoardVals.PLAYER_X) || theBoard.checkIfPlayerLose(BoardVals.PLAYER_O) || (depth == 0)){
            terminal = true;
        }
        return terminal;
    }
    
}
