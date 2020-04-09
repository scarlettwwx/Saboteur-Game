package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.Board;

import java.lang.reflect.Array;
import java.util.*;


/*
 * aTiles: tiles in the path
 * OpenEnds: number of Open ends in this path
 * startsAtOrigin: 1, starts at origin.  0, starts at somewhere else
 * */

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }

    //    public final int BOARD_SIZE =  SaboteurBoardState.BOARD_SIZE;
    public static final int BOARD_SIZE =  5;
    public static int[][] aBoard;
    public static boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];

    public MyTools(int[][] pBoard){
        aBoard=pBoard;
        initVisited();
    }

    public static void initVisited(){
        for(int i = 0; i<BOARD_SIZE;i++){  //initialize visited array.
            for(int j=0;j<BOARD_SIZE;j++){
                visited[i][j]=false;
            }
        }
    }



    public static ArrayList<student_player.Path> searchBoard(){
        ArrayList<student_player.Path> arr =new ArrayList<student_player.Path>();
        for(int i=0;i<BOARD_SIZE;i++){
            for(int j=0;j<BOARD_SIZE;j++){
                if(aBoard[i][j]==1 && !visited[i][j]){
                    student_player.Path temp = new student_player.Path();
                    if(i==2 && j==2){  //if we found it at origin
                        temp.startsAtOrigin=true;
                    }
                    int numOnes = temp.dfs(i,j);
                    temp.numOnes = numOnes;  //updates number of one found.
                    arr.add(temp);   //adds into the list
                }
            }
        }
        return arr;
    }




    public static void main(String args[]){
        int[][] board = {{0,0,0,0,0},{0,1,0,0,1},{0,0,1,0,1},{0,0,-1,1,1},{0,0,0,0,-1}};
        student_player.MyTools tryy = new student_player.MyTools(board);
        ArrayList<student_player.Path> temp2 = student_player.MyTools.searchBoard();

        for (student_player.Path a: temp2){
            System.out.println(a);

        }


    }

}


class Path{
    public int numOnes;
    public int[] deepestEnd={0,0};
    public boolean startsAtOrigin;
    public int openEnds = 0;

    public static final int[] dx = {1,0,0,-1};
    public static final int[] dy = {0,-1,1,0};

    @Override
    public String toString() {
        return "Path{" +
                "numOnes=" + numOnes +
                ", deepestEnd=" + Arrays.toString(deepestEnd) +
                ", startsAtOrigin=" + startsAtOrigin +
                ", openEnds=" + openEnds +
                '}';
    }

    public int dfs(int i, int j){
        if( i<0 || i>= student_player.MyTools.BOARD_SIZE || j<0 || j>= student_player.MyTools.BOARD_SIZE ){return 0;}
        if( student_player.MyTools.aBoard[i][j]!= 1 || student_player.MyTools.visited[i][j]){

            if ( student_player.MyTools.aBoard[i][j]== -1 ){  //if open ends
                int row = deepestEnd[0];
                int col = deepestEnd[1];
                if (i>row){  //check if it's the deepest.
                    deepestEnd[0]=i;
                    deepestEnd[1]=j;
                }
                openEnds++;
            }
            return 0;
        }else{ //if another 1 has been found
            student_player.MyTools.visited[i][j] = true;
            int ans = 1;
            if(i==5 && j==14){  //if we found it at origin
                startsAtOrigin=true;
            }
            for (int d=0; d<4;d++){
                ans = ans + dfs(i+dx[d],j+dy[d]);
            }
            return ans;


        }
    }

}