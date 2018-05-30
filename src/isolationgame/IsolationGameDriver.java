//Christopher Kilian
//CS 420 - Spring 2018
//Programming Project #3 - Isolation Game

package isolationgame;

import java.io.Console;
import java.util.List;

//Driver for the isolation game, which handles the command line user interface and the tracking of game status
public class IsolationGameDriver {

    
    public static void main(String[] args) {
        boolean mainMenuFlag = true;
        
        Console console = System.console();
        if (console == null) {
            System.out.println("No console: To run the menu, run from the command line!");
            System.exit(0);
        }
        
        while(mainMenuFlag){
            System.out.println("Welcome to the Isolation game!");
            System.out.println("Main Menu:");
            System.out.println("1) Start a new game");
            System.out.println("2) Exit");
            System.out.println("Please enter one of the menu option numbers now:");
            String menuChoice = console.readLine();
            if(menuChoice.equals("1")){
                playGame();
            }else if(menuChoice.equals("2")){
                mainMenuFlag = false;
                System.out.println("Thanks for playing!");
            }else{
                System.out.println("Invalid menu choice. Try again.");
                System.out.println("");
                System.out.println(""); //spacing
                System.out.println("");
            }
        } 
    }
    
    
    //Method which handles main game play loop
    public static void playGame(){
        Console console = System.console();
        boolean playingGameFlag = true;
        int turnNum = 1;
        int round = 0;
        IsolationGame theGame;
        BoardVals startingPlayer;
        long timeLimit;
        
        
        System.out.println("");//spacing
        System.out.println("");
        System.out.println("Before playing, please enter who is playing first.");
        System.out.println("Player X is the computer, and player O is the human player. Enter X or O now: ");
        while(true){ //loop until player enters appropriate value, then break - get starting player here
            String whichPlayer = console.readLine();
            whichPlayer = whichPlayer.trim();
            if(whichPlayer.equalsIgnoreCase("x")){
                startingPlayer = BoardVals.PLAYER_X;
                break;
            }else if(whichPlayer.equalsIgnoreCase("o")){
                startingPlayer = BoardVals.PLAYER_O;
                break;
            }else{
                System.out.println("That is an invalid player selection. Please try again.");
            }
        }

        System.out.println("");//spacing
        System.out.println("");
        System.out.println("Now please enter the turn time limit the AI will have to abide by. Note that the minimum time is 0.5 seconds.");
        System.out.println("Please enter time limit in number of seconds now: ");
        while(true){ //loop until player enters appropriate value, then break - get time limit here
            String time = console.readLine();
            time = time.trim();

            if(time.matches("\\d*\\.?\\d+")){
                try{
                    double playerTime = Double.parseDouble(time);
                    timeLimit = (long)(1000 * playerTime); //convert user entered seconds to milliseconds            
                    break;
                }catch(Exception e){
                    System.out.println("Error converting entered value to numeric - try again.");
                }
            }else{
                System.out.println("That is not a valid number - please only enter a positive value.");
                System.out.println("Enter the time limit (in seconds) again:");
            }
        }

        //with starting player and time limit selected, game can begin
        theGame = new IsolationGame(startingPlayer, timeLimit);
        //main control loop for gameplay
        while(playingGameFlag){
            for(int i = 0; i < 100; i++){
                System.out.println(""); //fake clearing the screen - since Java lacks command line clearing support
            }
            
            if((turnNum % 2) == 0){
                round++;
            }
            
            printMoves(theGame, round);
            System.out.println(theGame.getGameBoard().printBoard());
            if(theGame.getPlayerXMoves().size() > 0){
                System.out.println("Computer's move is: " + theGame.getPlayerXMoves().get(theGame.getPlayerXMoves().size() - 1));
            }
            System.out.println("");
            System.out.println("It is now Player " + ((theGame.getPlayerTurn() == BoardVals.PLAYER_O) ? "O" : "X") +"'s turn!");
            if(theGame.getPlayerTurn() == BoardVals.PLAYER_O){
                System.out.println("Player O, please enter a letter and number combination to indicate your move.");
                System.out.println("Please format your entry as such: A2 (note the lack of space between the letter and number)");
                System.out.println("To quit the game, type quit");
                while(true){
                    //basic second AI player - uncomment this block (and comment out the user control block) to allow the AI to play against itself
//                    System.out.println("Player O is thinking..");
//                    AIPlayer playerO = new AIPlayer();
//                    GameMove aiMove = playerO.getAIMove(theGame.getGameBoard(), BoardVals.PLAYER_O, timeLimit);
//                    String aiRow = theGame.getGameBoard().getInverseRowMap().get(aiMove.getRow());
//                    int aiCol = aiMove.getColumn() + 1;
//                    if(theGame.processHumanTurn(aiRow, aiCol)){
//                        break;
//                    }
                    //Player control block - comment this out (and uncomment the above section of code) to allow the AI to play against itself
                    String userInput;
                    userInput = console.readLine();
                    userInput = userInput.trim();
                    if(userInput.matches("[A-Ha-h][1-8]")){
                        String userRow = userInput.substring(0, 1).toUpperCase();
                        int userColumn = Integer.parseInt(userInput.substring(1, 2));
                        if(theGame.processHumanTurn(userRow, userColumn)){
                            System.out.println("Move made");
                            break;
                        }else{
                            System.out.println("That is an invalid move - please try again.");
                        }
                    }else if(userInput.equalsIgnoreCase("quit")){
                        System.out.println("Thanks for playing!");
                        playingGameFlag = false;
                        break;
                    }else{
                        System.out.println("Invalid entry, please try again.");
                    }
                    //end of player control block
                }
            }else{
                System.out.println("Player X is thinking...");
                boolean aiTurnResult = theGame.processAITurn();
                if(!aiTurnResult){
                    System.out.println("AI FAILURE");
                }
            }
            
            //check if there is a winner yet
            if(theGame.getGameWinner() != BoardVals.NO_WINNER){
                playingGameFlag = false;
            }
            
            turnNum++;
        }
        
        for(int i = 0; i < 100; i++){
            System.out.println(""); //fake clearing the screen - since Java lacks command line clearing support in Windows 10
        }
        
        System.out.println("GAME OVER");
        if(theGame.getGameWinner() == BoardVals.PLAYER_O){
                System.out.println("Player O wins!!!");
        }else if(theGame.getGameWinner() == BoardVals.PLAYER_X){
                System.out.println("Player X wins!!!");
        }
        System.out.println("Final moves list: ");
        printMoves(theGame, round);
        System.out.println("Final game board:");
        System.out.println(theGame.getGameBoard().printBoard());
        System.out.println("");
        System.out.println("");
        
    }
    
    
    //Method which prints out all of the current moves in a game in a formatted manner
    public static void printMoves(IsolationGame theGame, int turns){
        List<String> humanMoves = theGame.getPlayerOMoves();
        List<String> aiMoves = theGame.getPlayerXMoves();
        int numAIMoves = aiMoves.size();
        int numHumanMoves = humanMoves.size();
        
        StringBuilder moves = new StringBuilder();
        moves.append("Computer vs. Opponent\n");
        
        for(int i = 0; i < turns; i++){
            moves.append("" + (i+1) + ". ");
            if(i < numAIMoves){
                moves.append(aiMoves.get(i));
            }
            moves.append("\t\t");
            if(i < numHumanMoves){
                moves.append(humanMoves.get(i));
            }
            moves.append("\n");
        }
        
        System.out.println(moves.toString());
    }

    
}
