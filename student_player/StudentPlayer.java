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
        int disToEntrance = (posi[0]+posi[1]-10); // 往entrance之上放减一点分
        int cardScore=0;
        String name = sm.getCardPlayed().getName();

        //开放形tile加分：
        String[] opening = {"0","0_flip", "5","5_flip","6","6_flip","7","7_flip","8","8_flip", "9","9_flip","10","10_flip"};
        for(String o : opening){
            if(o.equals(name)) {
                cardScore += 1;
                break;
            }
        }

        if(name.equals("Malus")) {
            return 100;
        }
        if(name.equals("Map")) {
            return 500;
        }
        if(name.equals("Bonus")) {
            return 1000;
        }
        if(name.equals("Drop")) {
            return -1000;
        }
        if(name.equals("Destroy")) {
            return 0;
        }
        return disToEntrance+cardScore;
    }



    //1. evaluate on boardState
    public static double evaluate(SaboteurBoardState pBoard){
        int player_id = pBoard.getTurnPlayer();
        //needs to verify again
        if(pBoard.getWinner() == player_id){ return Integer.MAX_VALUE -1;}  //first player is us?
        else if(pBoard.getWinner() == 1-player_id){ return Integer.MIN_VALUE + 1;}  //second player
        else if(pBoard.getWinner() == Board.DRAW){ return 0.0;} //a tie

        boolean exist = player_id==0;
        double score1 = 3* MyTools.disToThreeHidden(pBoard);
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
        double score1 = 3*MyTools.disToThreeHidden(pBoard);
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

    //this filtermoves will help prune branching factors. PUT PRIORITY For must use cards.

    public static ArrayList<SaboteurMove>  filterMoves(ArrayList<SaboteurMove> allMoves, int factor){ //moves to be pruned. factor to determine #of moves stored
        ArrayList<SaboteurMove> filter= new ArrayList<SaboteurMove>();
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
            if(movePts == 1000 || movePts == 500 || movePts ==100 ){ //priority given to Bonus, Map, Malus
                filter.add(move);                                   //If a priority Move found, filter will return with size =1
                return filter;
            }else if(movePts>=0){
                filter.add(move);
                counts++;

            }else if(counts<factor){ //to avoid the case where we pruned all tiles<0.
                filter.add(move);
                counts++;
            }
            if(counts==factor){ break; } //when max number of factors achieved
        }
        return filter;
    }




    public static double minimax(int depth, SaboteurBoardStateCopy board, double alpha, double beta, boolean isMax, int factor){
        double score;
        ArrayList<SaboteurMove> allMoves = student_player.StudentPlayer.filterMoves(board.getAllLegalMoves() , factor); //factors also decide the size of board
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
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        ArrayList<SaboteurMove> legalMovesPruned= filterMoves(board.getAllLegalMoves(), 10); //initial size 10;
        if(legalMovesPruned.size()==1){
            System.out.println("Has pruned");
            System.out.println(legalMovesPruned.get(0).toPrettyString());
            return legalMovesPruned.get(0);  //return the first move. (must be bonus, map... )
        }

        SaboteurMove resultMove = legalMovesPruned.get(0); //for base case if no moves are found.

        double max=Integer.MIN_VALUE;       //finds the maxvalue node
        for (SaboteurMove m : legalMovesPruned ) {
            //System.out.println("Chose the move: "+ m.toPrettyString());
            SaboteurBoardStateCopy copy = new SaboteurBoardStateCopy(board);
            if (copy.isLegal(m)) {
                copy.processMove(m); //we move, our current state becomes the root
                double value = minimax(4,copy,Integer.MIN_VALUE,Integer.MAX_VALUE,false,6);
                if(value>max){ //choose the max value
                    max = value;
                    resultMove = m;
                    System.out.println("Has pruned");
                    System.out.println(m.toPrettyString());

                    if (m.getCardPlayed() instanceof SaboteurTile){
                        ((SaboteurTile) m.getCardPlayed()).showCard();
                    }
                }
            }
        }
        return resultMove;
    }

    public static void main(String args[]){
//        SaboteurBoardState newboard = new SaboteurBoardState();
//        newboard.processMove(newboard.getRandomMove());
//        StudentPlayer a = new StudentPlayer();
//        newboard.processMove( newboard.getRandomMove());
//        double max = Integer.MIN_VALUE;
//        SaboteurMove bestmove = null;
//        for(SaboteurMove temp: newboard.getAllLegalMoves()){
//            System.out.println(temp.toPrettyString());
//            SaboteurBoardStateCopy copy  = new SaboteurBoardStateCopy(newboard);
//        }

    }

}
