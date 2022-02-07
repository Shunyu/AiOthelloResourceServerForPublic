package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResultRequestResource {
	
	private String gameresult;
	private List<GameSituation> gameSituationList;
	
}