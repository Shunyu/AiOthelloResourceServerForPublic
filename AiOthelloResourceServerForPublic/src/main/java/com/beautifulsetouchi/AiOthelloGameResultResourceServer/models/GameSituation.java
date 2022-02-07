package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSituation {
	
	private int[][] beforeboard;
	private int player;
	private String action;
	private int[][] afterboard;

}