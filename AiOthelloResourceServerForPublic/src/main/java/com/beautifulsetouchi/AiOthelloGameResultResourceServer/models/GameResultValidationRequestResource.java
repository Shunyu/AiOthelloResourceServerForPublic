package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * AIオセロとの対決の盤面推移の情報を格納するクラス
 * 盤面推移の情報が妥当かどうか（チートされた形跡がないか）確認する際に利用する。
 */
@Getter
@Setter
public class GameResultValidationRequestResource {
	
	private List<GameSituation> gamesituationlist;
	
}