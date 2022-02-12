package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Aiオセロの対戦結果を更新するリクエストの際に、
 * 勝者、およびAIオセロとの対決の盤面推移の情報を格納するクラス
 * @author shunyu
 *
 */
@Getter
@Setter
public class GameResultRequestResource {
	
	private String gameresult;
	private List<GameSituation> gameSituationList;
	
}