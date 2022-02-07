package com.beautifulsetouchi.AiOthelloGameResultResourceServer.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloUsageRecord;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.GameSituation;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.repositories.AiOthelloUsageRecordRepository;

@Component
public class LoginUserAiOthelloResultChecker {
	
	private final AiOthelloUsageRecordRepository aiOthelloUsageRecordRepository;
	private final OthelloGameSimulator othelloGameSimulator;
	
	@Autowired
	public LoginUserAiOthelloResultChecker(
			AiOthelloUsageRecordRepository aiOthelloUsageRecordRepository,
			OthelloGameSimulator othelloGameSimulator
			) {
		this.aiOthelloUsageRecordRepository = aiOthelloUsageRecordRepository;
		this.othelloGameSimulator = othelloGameSimulator;
	}
	
	public boolean isValid(List<GameSituation> gameSituationList, String userid) {
		
		// 結果の検証の際には、{変化前盤面・黒/白・乱数/手・変化後盤面}*Nの形式のデータをPOSTしてもらう。
		// 　　・一次判定では、乱数がデータベースに存在していた場合には、乱数系列を手に置き換えて、判定を終了する。
		// 　　　　　　　　　　乱数がデータベースに存在していない場合には、何もせずに判定を終了する。
		// 　　　※一次判定を行ったユーザーについては、乱数系列をデータベースから削除する。
		// 　　・二次判定では、以下を判定する。
		// 　　　あ）一番始めの変化前盤面が正しい状態であること。・・・完全一致の確認
		// 　　　い）変化前盤面から変化後盤面への遷移が正しいこと。・・・オセロサービスで変化させてみて完全一致の確認
		// 　　　う）変化後盤面=変化前盤面であること。・・・完全一致の確認
		// 　　　え）一番最後の変化後盤面が終局状態であること。・・・オセロサービスで終局の確認
		
		// 登録している情報（bestmoveidとbestmoveのマップ作成）
		List<AiOthelloUsageRecord> aiOthelloUsageRecordList = aiOthelloUsageRecordRepository.findByUserid(userid);
		System.out.println("aiOthelloUsageRecordList:"+aiOthelloUsageRecordList);
		
		Map<String, String> bestmoveidToBestmove = aiOthelloUsageRecordList
				.stream()
				.collect(Collectors.toMap(AiOthelloUsageRecord::getBestmoveid, AiOthelloUsageRecord::getBestmove));
		System.out.println("bestmoveidToBestmove:"+bestmoveidToBestmove);
		
		List<GameSituation> revisedGameSituationList = new ArrayList<>();
		// ゲームの状況のリストの一次判定
		for (GameSituation gameSituation : gameSituationList) {
			int player = gameSituation.getPlayer();
			String action = gameSituation.getAction();
			String revisedAction = null;
			if (player==1) {
				if (bestmoveidToBestmove.containsKey(action)) {
					System.out.println("action exists:"+action);
					// 存在する場合にはOK
					revisedAction = bestmoveidToBestmove.get(action);
				} else {
					System.out.println("action does not exist:"+action);
					
					// 判定を行った人物のデータは削除する。
					aiOthelloUsageRecordRepository.deleteByUserid(userid);
					
					// 存在しない場合にはNG
					return false;
				}
			} else {
				revisedAction = action;
			}
			
			GameSituation revisedGameSituation = new GameSituation();
			revisedGameSituation.setBeforeboard(gameSituation.getBeforeboard());
			revisedGameSituation.setAfterboard(gameSituation.getAfterboard());
			revisedGameSituation.setPlayer(player);
			revisedGameSituation.setAction(revisedAction);
			
			revisedGameSituationList.add(revisedGameSituation);
			System.out.println("revisedGameSituationList:"+revisedGameSituationList);
		}
		// 一次判定後にデータを削除する。
		// 本当は、同一人物から同時に何回もバリデーションを求められた際に、何回もOKの判定を出さないように対処が必要。
		// また、一次判定後に、再度一次判定から再開するケースへの対応も必要。（現状ではデータが消えてしまっている）
		aiOthelloUsageRecordRepository.deleteByUserid(userid);
		System.out.println("delete by userid in aiOthelloUsageRecordRepository");
		
		// ゲームの状況リストの二次判定
		int[][] preAfterboard = new Othello().getBoardArray(); // 初期盤面
		for (GameSituation gameSituation : revisedGameSituationList) {
			int[][] beforeboard = gameSituation.getBeforeboard();
			if (!Arrays.deepEquals(preAfterboard, beforeboard)) {
				System.out.println("preAfterboard:"+preAfterboard);
				System.out.println("beforeboard:"+beforeboard);
				
				// 初期盤面との一致しない場合
				// 一つ前の盤面と一致しない場合
				return false;
			}
			int player = gameSituation.getPlayer();
			String action = gameSituation.getAction();
			int[][] afterboard = gameSituation.getAfterboard();
			int[][] computedAfterboard = othelloGameSimulator.computeAfterBoard(beforeboard, player, action);
			if (!Arrays.deepEquals(computedAfterboard, afterboard)) {
				System.out.println("computedAfterboard:"+computedAfterboard);
				System.out.println("afterboard:"+afterboard);
				
				// 計算結果が盤面と一致しない場合
				return false;
			}
			
			preAfterboard = gameSituation.getAfterboard();
		}
		if (!othelloGameSimulator.isGameOverBoard(preAfterboard)) {
			System.out.println("preAfterboard:"+preAfterboard);
			
			// 最後の盤面について終局していない場合
			return false;
		}
		
		return true;
		
	}
}
