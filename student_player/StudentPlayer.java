package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.Board;
import boardgame.Move;

import java.lang.reflect.Array;
import java.util.*;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
	public static final int DEFAULT_SEARCH_DEPTH = 2;
    public static final int FIRST_STEP_SEARCH_DEPTH = 3;
    public static final int DEFAULT_TIMEOUT = 1930;
    public static final int FIRST_MOVE_TIMEOUT = 29930;
    
    public StudentPlayer() {
        super("260786231");
    }
    
    private class MoveValue {

        public double returnValue;
        public Move returnMove;
  
        public MoveValue(double returnValue) {
            this.returnValue = returnValue;
        }
  
        public MoveValue(double returnValue, Move returnMove) {
            this.returnValue = returnValue;
            this.returnMove = returnMove;
        }
    }
    
    private class StateMove{
      public Move move;
      public SaboteurBoardState boardState;
      public double eval;

      public StateMove(Move move, SaboteurBoardState boardState) {
          this.move = move;
          this.boardState = boardState;
          this.eval = evaluate(boardState); //to implement
      }
    }
    
    //1. evaluate on boardState
    public static double evaluate(SaboteurBoardState pBoard){
    		int player_id = pBoard.getTurnPlayer();
        //needs to verify again
        if(pBoard.getWinner() == player_id){ return Integer.MAX_VALUE -1;}  //first player is us?
        else if(pBoard.getWinner() == 1-player_id){ return Integer.MIN_VALUE + 1;}  //second player
        else if(pBoard.getWinner() == Board.DRAW){ return 0.0;} //a tie

        boolean exist = player_id==0;
        double score1 = 3* MyTools.disToThreeHidden();
        double score2 = MyTools.openEndsWholeBoard(pBoard);
        SaboteurTile[][] tiles= pBoard.getHiddenBoard();
        int numOfHidden = 0;
        int nugget= 0;
        int i =0;
        while(i<3){
            int j = 3 + i*2;
            //if the first card is hidden1 or 2.
            if(tiles[12][j].getIdx().equals("hidden1") || tiles[12][j].getIdx().equals("hidden2") ){
                numOfHidden++;
            }else if(tiles[12][j].getIdx().equals("nugget")){
                nugget++;
            }
            i++;
        }
        double score3 = numOfHidden*10 + nugget*30; //check hidden cards

        double score4 = pBoard.getNbMalus(pBoard.getTurnPlayer());

        return score1+score2+score3+score4;
    }
    
    //2. evaluate on the copy board:
    public static double evaluate(SaboteurBoardStateCopy pBoard){
        //needs to verify again
    		int player_id = pBoard.getTurnPlayer();
        if(pBoard.getWinner() == player_id){ return Integer.MAX_VALUE -1;}  //first player is us?
        else if(pBoard.getWinner() == 1-player_id){ return Integer.MIN_VALUE + 1;}  //second player
        else if(pBoard.getWinner() == Board.DRAW){ return 0.0;} //a tie

        boolean exist = player_id==0;
        double score1 = 3*MyTools.disToThreeHidden();
        double score2 = MyTools.openEndsWholeBoard(pBoard);
        SaboteurTile[][] tiles= pBoard.getHiddenBoard();
        int numOfHidden = 0;
        int nugget= 0;
        int i =0;
        while(i<3){
            int j = 3 + i*2;
            //if the first card is hidden1 or 2.
            if(tiles[12][j].getIdx().equals("hidden1") || tiles[12][j].getIdx().equals("hidden2") ){
                numOfHidden++;
            }else if(tiles[12][j].getIdx().equals("nugget")){
                nugget++;
            }
            i++;
        }
        double score3 = numOfHidden*10 + nugget*30; //check hidden cards

        double score4 = pBoard.getNbMalus(pBoard.getTurnPlayer());

        return score1+score2+score3+score4;
    }
    
    
    
    
    protected MoveValue minimax(double alpha, double beta, int originalDepth, int maxDepth, SaboteurBoardState boardState, int turnplayer, final SaboteurMove lastMove) {
    		return null;
    }
    
    
    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState board) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();
        Move myMove = null;
        // Is random the best you can do?
        double [] scoreOFMoves = new double[board.getAllLegalMoves().size()];
        int i=0;
          
        for (SaboteurMove m : board.getAllLegalMoves()) {
        	    //System.out.println("Chose the move: "+ m.toPrettyString());
     		SaboteurBoardStateCopy sbsc = new SaboteurBoardStateCopy(board);
        		
        		if (!sbsc.isLegal(m)) {
        			System.out.println("illegal");
        			continue;
        		}
        		sbsc.processMove(m);
        		scoreOFMoves[i]= StudentPlayer.evaluate(sbsc);
        		//System.out.println("score is:"+ scoreOFMoves[i]);
        		i++;
        }
        
        Arrays.sort(scoreOFMoves);
        double maxScore = scoreOFMoves[scoreOFMoves.length-1];
        for (SaboteurMove m : board.getAllLegalMoves()) {
        		//System.out.println("Chose the move: "+ m.toPrettyString());
        		SaboteurBoardStateCopy sbsc = new SaboteurBoardStateCopy(board);
    		
        		if (!sbsc.isLegal(m)) {
        			System.out.println("illegal");
        			continue;
        		}
        		sbsc.processMove(m);
        		if(StudentPlayer.evaluate(sbsc)==maxScore) {
        			//how to choose a good move?
        			myMove = m;
        		}
        		i++;
    }
        return myMove;
        //Move myMove = boardState.getRandomMove(); //return type = SaboteurMove

        // Return your move to be processed by the server.
       
    }
    
    public static void main(String args[]){
    		System.out.println("helo");
    }
    
    
}
