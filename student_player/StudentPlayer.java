package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.Board;
import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("xxxxxxxxx");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */

    public double evaluate(SaboteurBoardState pBoard){
        //needs to verify again
        if(pBoard.getWinner() == player_id){ return Integer.MAX_VALUE -1;}  //first player is us?
        else if(pBoard.getWinner() == 1-player_id){ return Integer.MAX_VALUE -1;}  //second player
        else if(pBoard.getWinner() == Board.DRAW){ return 0.0;} //a tie

        boolean exist = player_id==0;
        double score1 = 3*MyTools.disToThreeHidden();
        double score2 = MyTools.openEndsWholeBoard(pBoard);

        SaboteurTile[][] tiles = pBoard.getHiddenBoard();
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





    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        MyTools.getSomething();

        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();

        // Return your move to be processed by the server.
        return myMove;
    }


    public static void main(String args[]){
        student_player.StudentPlayer s1 = new student_player.StudentPlayer();
        SaboteurBoardState board = new SaboteurBoardState();
        board.printBoard();  //with search board
        MyTools tryy = new MyTools(board.getHiddenIntBoard(),board.getHiddenBoard());

        System.out.println(s1.evaluate(board));

        SaboteurMove move1 = new SaboteurMove((new SaboteurTile("0")),6,5,1);
        board.processMove(move1);

        System.out.println(s1.evaluate(board));

        board.printBoard();  //with search board



    }
}