package com.beautifulsetouchi.AiOthelloGameResultResourceServer.validators;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.BoardIndex;
import com.google.gson.Gson;


/**
 * オセロの盤面のデータ、およびオセロのルールを保持しているクラス
 * 
 * 現在の盤面と現在のplayerのもとで、特定のオセロの手を打った際に、
 * 変化した後の盤面の状態を計算する際や、
 * ゲームオーバーであるか確認する際にも利用する。
 * @author shunyu
 *
 */
public class Othello {
	
    private static final int BLACK = 1;
    private static final int WHITE = 2;

    private int[][] boardArray;

    private int player;

    public Othello() {
        initializeOthello();
    }

    public void initializeOthello(){
        boardArray = new int[10][10];

        initializeBoard();

        this.player = BLACK;
    }

    public void initializeBoard(){

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                this.boardArray[i][j] = -1;
            }
        }

        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                this.boardArray[i][j] = 0;
            }
        }

        this.boardArray[4][4] = 1;
        this.boardArray[4][5] = 2;
        this.boardArray[5][4] = 2;
        this.boardArray[5][5] = 1;
    }

    public int getPlayer(){
        return this.player;
    }

    public void setPlayer(int player){
        this.player = player;
        return;
    }
    
    public void setPlayerString(String playerString) {
    	if(playerString.equals("black")) {
    		this.player = BLACK;
    	} else if (playerString.equals("white")) {
    		this.player = WHITE;
    	} else {
    		this.player = WHITE;
    	}
    }

    public int[][] getBoardArray(){
        return this.boardArray;
    }
    
    public String getBoard() {
    	Gson gson = new Gson();
    	return gson.toJson(this.boardArray);
    }
    
    public void setBoardArray(int[][] boardArray) {
    	this.boardArray= boardArray;
    }
    
    public void setBoard(String board) {
    	
    	Gson gson = new Gson();
    	int[][] boardArray = gson.fromJson(board, int[][].class);
    	this.boardArray = boardArray;
    	
    }

    private int getOpponentPlayer(){
        int opponentPlayer = 3 - this.player;
        return opponentPlayer;
    }

    private int countTurnOverStones(int p, int q, int d, int e){
    	
        int i;
        int opponentPlayer = getOpponentPlayer();

        for(i = 1; this.boardArray[p+i*d][q+i*e] == opponentPlayer; i++){}

        if (this.boardArray[p+i*d][q+i*e] == this.player) {
            return i - 1;
        } else {
            return 0;
        }
    }

    private boolean isLegalMove(int p, int q) {
    	
        if (p < 1 || p > 8 || q < 1 || q > 8){
            return false;
        }
        if (this.boardArray[p][q] != 0){
            return false;
        }

        for (int d = -1; d <= 1; d++){
            for (int e = -1; e <= 1; e++){
                if (d == 0 && e == 0){
                    continue;
                }
                if (countTurnOverStones(p, q, d, e) > 0){
                    return true;
                }
            }
        }

        return false;    	
    }
    
    private boolean isLegalMove(BoardIndex boardIndex) {
    	
        int p = boardIndex.getVerticalIndex();
        int q = boardIndex.getHorizontalIndex();

        return isLegalMove(p, q); 
    }

    private boolean existLegalMove(){

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                if (isLegalMove(i, j)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isGameOver() {
    	
    	setPlayer(BLACK);
    	if(existLegalMove()) {
    		return false;
    	}
    	
    	setPlayer(WHITE);
    	if(existLegalMove()) {
    		return false;
    	}
    	
    	return true;
    }

    private void turnOverStones(BoardIndex boardIndex){

        int p = boardIndex.getVerticalIndex();
        int q = boardIndex.getHorizontalIndex();

        for (int d = -1; d <= 1; d++){
            for (int e = -1; e <= 1; e++){
                if (d == 0 && e == 0){
                    continue;
                }
                int count = countTurnOverStones(p, q, d, e);

                for (int i = 1; i <= count; i++){
                    int tempVerticalIndex = p + i * d;
                    int tempHorizontalIndex = q + i * e;
                    this.boardArray[tempVerticalIndex][tempHorizontalIndex] = this.player;

                }
            }
        }

        this.boardArray[p][q] = this.player;
    }
    
    public void updateOneMove(String action) {
    	
    	int verticalIndex = Integer.parseInt(Character.toString(action.charAt(0)));
    	int horizontalIndex = Integer.parseInt(Character.toString(action.charAt(1)));
    	BoardIndex boardIndex = new BoardIndex(verticalIndex, horizontalIndex);
    	
    	if (isLegalMove(boardIndex)) {
    		turnOverStones(boardIndex);
    	} else {
    		
    	}
    	
    }

}
