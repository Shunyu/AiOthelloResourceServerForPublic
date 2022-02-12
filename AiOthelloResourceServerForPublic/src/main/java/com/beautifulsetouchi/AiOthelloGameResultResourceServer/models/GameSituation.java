package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import lombok.Getter;
import lombok.Setter;

/**
 * 検証する一連の盤面推移のデータにおける
 * 1時点の盤面推移のデータを格納するクラス
 * 
 * 石を置く前の盤面、その時のplayer、置かれた手（もしくはリソースサーバーで生成した乱数）、石を置いた後の盤面
 * @author shunyu
 *
 */
@Getter
@Setter
public class GameSituation {
	
	private int[][] beforeboard;
	private int player;
	private String action;
	private int[][] afterboard;

}