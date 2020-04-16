package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurCard;
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
        super("260769099");
    }

    public static int evalMove (SaboteurMove sm) {
        int[] posi=sm.getPosPlayed();
        int disToEntrance = (posi[0]+posi[1]-10);
        if(posi[0] == 4) { //equivalent as drop
            disToEntrance = -10;
        }
        if(posi[0] <= 3) { // we would rather drop
            disToEntrance = -100;
        }
        int cardScore=0;
        String name = sm.getCardPlayed().getName();
        if(sm.getCardPlayed() instanceof SaboteurTile){
            name = ((SaboteurTile) sm.getCardPlayed()).getIdx();
        }
        String[] opening = {"0","0_flip", "5","5_flip","6","6_flip","7","7_flip","8","8_flip", "9","9_flip","10","10_flip"};
        String[] closing = {"1","1_flip", "2","2_flip","3","3_flip","4","4_flip","11","11_flip","12","12_flip", "13","13_flip","14","14_flip","15","15_flip"};

        for(String o : opening){
            if(o.equals(name)) {
                cardScore += 20;
                break;
            }
        }

        for(String o : closing){
            if(o.equals(name)) {
                cardScore -= 2000;
                break;
            }
        }

        if(name.equals("Malus")) {
            return 100;    //100
        }
        if(name.equals("Map")) {
            return 500;
        }
        if(name.equals("Bonus")) {
            return 10000;
        }
        if(name.equals("Drop")) {
            return -10;
        }
        if(name.equals("Destroy")) {
            return 0;
        }
        return disToEntrance+cardScore;
    }




    public static double evaluate(SaboteurBoardState pBoard){
        int player_id = pBoard.getTurnPlayer();
        //needs to verify again
        if(pBoard.getWinner() ==1 - player_id){
//            System.out.println("Total Score:");
//            System.out.println(Integer.MAX_VALUE -1);
            return Integer.MAX_VALUE -1;}  //first player is us?
        else if(pBoard.getWinner() == player_id){

//            System.out.println("Total Score:");
//            System.out.println(Integer.MIN_VALUE +1);
            return Integer.MIN_VALUE + 1;}  //second player
        else if(pBoard.getWinner() == Board.DRAW){ return 0.0;} //a tie

        double score1 = MyTools.disToThreeHidden(pBoard);

      return score1;
    }

    public static double evaluate(SaboteurBoardStateCopy pBoard){
        //needs to verify again
        int player_id = pBoard.getTurnPlayer();
        if(pBoard.getWinner() ==1 - player_id){
            return Integer.MAX_VALUE -1;}  //first player is us?
        else if(pBoard.getWinner() == player_id){

            return Integer.MIN_VALUE + 1;}  //second player
        else if(pBoard.getWinner() == Board.DRAW){ return 0.0;} //a tie

        double score1 = MyTools.disToThreeHidden(pBoard);

        return score1;
    }



    public static ArrayList<SaboteurMove>  filterMoves(SaboteurBoardStateCopy board, ArrayList<SaboteurMove> allMoves, int factor){ //moves to be pruned. factor to determine #of moves stored
        ArrayList<SaboteurMove> filter= new ArrayList<SaboteurMove>();
        HashMap<String,SaboteurMove> map = new HashMap<String,SaboteurMove>();
        int counts =0 ;
        Collections.sort(allMoves, new Comparator<SaboteurMove>() {   //sort from largest to smallest
            @Override
            public int compare(SaboteurMove o1, SaboteurMove o2) {
                int r1 = evalMove(o1);
                int r2 = evalMove(o2);
                //as reverse order
                if(r2>r1){ return 1; }
                else if(r2<r1){ return -1; }
                return 0;
            }
        });

        for(SaboteurMove move : allMoves ){
            int movePts = evalMove(move);
            if(movePts == 1000  || movePts ==100 ){ //priority given to Bonus, Map, Malus
                filter.add(move);                                   //If a priority Move found, filter will return with size =1
                return filter;
            }else if( movePts == 500){  //when maps, need to filter different positioned one
                int col = move.getPosPlayed()[1];
                //first card has been mapped.
                if((col == 3)&& board.thisPlayerHiddenStatus()[0]){ continue; }
                //second card has been mapped
                else if((col == 5)&& board.thisPlayerHiddenStatus()[1]){ continue; }
                //third card has been mapped
                else if((col == 7)&& board.thisPlayerHiddenStatus()[2]){ continue; }
                //if not we can map it.
                filter.add(move);
                return filter;

            }else if(counts<factor){ //to avoid the case where we pruned all tiles<0.
                map.put(move.toPrettyString(),move);
                counts++;
            }
            if(counts==factor){ break; } //when max number of factors achieved
        }

        filter = new ArrayList<SaboteurMove>(map.values());

        return filter;
    }



    public static double minimax(int depth, SaboteurBoardStateCopy board, double alpha, double beta, boolean isMax, int factor){
        double score;
        ArrayList<SaboteurMove> allMoves = student_player.StudentPlayer.filterMoves(board,board.getAllLegalMoves(), factor); //factors also decide the size of board
        //if leaf node:
        if(depth == 0 || board.getWinner() == board.AGENT_ID || board.getWinner() == (1-board.AGENT_ID) || board.getWinner()==Board.DRAW){
            double result =  evaluate(board);
            return result;
        }
        if(isMax){
            for(Iterator<SaboteurMove> a = allMoves.iterator(); a.hasNext();){
                SaboteurMove move = a.next();
                SaboteurBoardStateCopy child = new SaboteurBoardStateCopy(board);
                child.processMove(move);
                score = minimax(depth-1, child, alpha,beta,false, factor); //next round is opponent
                if(score> alpha){
                    alpha = score;
                }
                if(alpha>=beta){
                    break;
                }
            }
            return alpha;
        }else{ //opponent play
            for(Iterator<SaboteurMove> a = allMoves.iterator(); a.hasNext();){
                SaboteurMove move = a.next();
                SaboteurBoardStateCopy child = new SaboteurBoardStateCopy(board);
                child.processMove(move);
                score = minimax(depth-1, child, alpha,beta,true,factor);//next round is us
                if(score < beta){
                    beta = score;
                }
                if(alpha>=beta){
                    break;
                }
            }
            return beta;
        }
    }


    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState board) {

        ArrayList<SaboteurMove> legalMovesPruned= filterMoves(new SaboteurBoardStateCopy(board),board.getAllLegalMoves(), 20); //initial size 10;
        if(legalMovesPruned.size()==1){

            return legalMovesPruned.get(0);  //return the first move. (must be bonus, map... )
        }

        SaboteurMove resultMove = legalMovesPruned.get(0); //for base case if no moves are found.

        double max=Integer.MIN_VALUE;       //finds the maxvalue node
        for (SaboteurMove m : legalMovesPruned ) {
            SaboteurBoardStateCopy copy = new SaboteurBoardStateCopy(board);

            if (copy.isLegal(m)) {
                copy.processMove(m); //we move, our current state becomes the root
                double value = evaluate(copy); //INIFINITE LOOP

                if(value>max){ //choose the max value
                    max = value;
                    resultMove = m;
                }
            }
        }
        System.out.println("  ");
        return resultMove;
    }

    public static void main(String args[]){


    }

}