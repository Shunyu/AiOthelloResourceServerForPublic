package com.beautifulsetouchi.AiOthelloGameResultResourceServer.validators;

import org.springframework.stereotype.Component;

@Component
public class OthelloGameSimulator {

	public int[][] computeAfterBoard(int[][] beforeboard, int player, String action) {
		
		// 一手後の盤面（AfterBoard）の計算
		Othello othello = new Othello();
		othello.setBoardArray(beforeboard);
		othello.setPlayer(player);
		othello.updateOneMove(action);
		
		return othello.getBoardArray();
	}
	
	public boolean isGameOverBoard(int[][] board) {
		
		// 盤面がゲームオーバーであるか否かの判定
		Othello othello = new Othello();
		othello.setBoardArray(board);
		
		return othello.isGameOver();
	}
	
}
