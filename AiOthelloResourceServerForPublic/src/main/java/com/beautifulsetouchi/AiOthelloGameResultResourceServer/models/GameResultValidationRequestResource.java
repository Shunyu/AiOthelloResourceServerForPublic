package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResultValidationRequestResource {
	
	private List<GameSituation> gamesituationlist;
	
}