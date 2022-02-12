package com.beautifulsetouchi.AiOthelloGameResultResourceServer.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.GameResultRequestResource;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.GameSituation;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.LoginUserAiOthelloResponseResource;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.SiteUserGameRecord;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.repositories.AiOthelloUsageRecordRepository;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.repositories.SiteUserGameRecordRepository;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.services.AiOthelloService;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.validators.LoginUserAiOthelloResultChecker;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloRequestResource;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloResponseResource;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloUsageRecord;


/**
 * リソースサーバーにおけるコントローラークラス
 * accessToken(JWTトークン)の情報から権限を確認しつつ、対戦成績の取得や更新を実施する。
 * また、最適な手に関するリクエストを受けた際には、乱数の情報を付加して、レスポンスを返す。
 * @author shunyu
 *
 */
@RestController
@CrossOrigin("*")
public class AiOthelloGameResultController {

	private final SiteUserGameRecordRepository siteUserGameRecordRepository;
	private final AiOthelloService aiOthelloService;
	private final AiOthelloUsageRecordRepository aiOthelloUsageRecordRepository;
	private final LoginUserAiOthelloResultChecker loginUserAiOthelloResultChecker;
	
	/**
	 * 依存性の注入を実施する。
	 * @param siteUserGameRecordRepository
	 * @param aiOthelloService
	 * @param aiOthelloUsageRecordRepository
	 * @param loginUserAiOthelloResultChecker
	 */
	@Autowired
	public AiOthelloGameResultController(
			SiteUserGameRecordRepository siteUserGameRecordRepository, 
			AiOthelloService aiOthelloService,
			AiOthelloUsageRecordRepository aiOthelloUsageRecordRepository,
			LoginUserAiOthelloResultChecker loginUserAiOthelloResultChecker
			) {
		this.siteUserGameRecordRepository = siteUserGameRecordRepository;
		this.aiOthelloService = aiOthelloService;
		this.aiOthelloUsageRecordRepository = aiOthelloUsageRecordRepository;
		this.loginUserAiOthelloResultChecker = loginUserAiOthelloResultChecker;
	}

	/**
	 * accessToken(JWTトークン)の権限の有無を確認したのちに、ユーザIDを確認し、
	 * 該当するユーザーIDの方の対戦成績をデータベースから取得する。
	 * データベースに該当のユーザーIDが存在しなかった場合には、ユーザーを作成する。
	 * @param jwt
	 * @return
	 */
	@GetMapping("/user/v2/ai-othello/playresult")
	@ResponseBody
	public Map<String, Object> getPlayResult(@AuthenticationPrincipal Jwt jwt) {

		System.out.println("/user/v2/playresult");
		System.out.println("jwt.subject:"+jwt.getSubject());
		String userid = jwt.getSubject();

		boolean existFlag = siteUserGameRecordRepository.existsByUserid(userid);

		if (!existFlag) {
			System.out.println("create new userid:"+userid);
			SiteUserGameRecord newSiteUser = new SiteUserGameRecord();
			newSiteUser.setUserid(userid);
			siteUserGameRecordRepository.saveAndFlush(newSiteUser);
		}

		SiteUserGameRecord siteUser = siteUserGameRecordRepository.findByUserid(userid);
		Long winNum = siteUser.getWinnumber();
		Long loseNum = siteUser.getLosenumber();
		Long drawNum = siteUser.getDrawnumber();
		System.out.println("winNum:"+winNum);
		System.out.println("loseNum:"+loseNum);
		System.out.println("drawNum:"+drawNum);
		
		Map<String, Object> playResult = new HashMap<>();
		playResult.put("winNum", winNum);
		playResult.put("loseNum", loseNum);
		playResult.put("drawNum", drawNum);

		return playResult;
	}
	
	/**
	 * accessToken(JWTトークン)の権限の有無を確認したのちに、
	 * AIオセロサーバーに対して、最適な手に関するリクエストを投げて、最適な手の情報を取得する。
	 * その後、bestmoveidという乱数を作成し、ユーザー名・bestmoveid・最適な手の一覧をデータベースに格納しておく。
	 * 最後に、bestmoveidと最適な手に関する情報をレスポンスする。
	 * 
	 * のちに、対戦成績更新のリクエストが来た際には、
	 * ユーザー名・bestmoveidに関する情報から、このデータベースを確認して、
	 * 確かに、このリソースサーバー経由でAIオセロの最適な手をクライアントにレスポンスしていたかを検証し、
	 * チート行為が行われていないか、確かめている。
	 * @param jwt
	 * @param aiOthelloRequestResource
	 * @return
	 */
	@PostMapping("/user/v2/ai-othello/best-move")
	@ResponseBody
	public LoginUserAiOthelloResponseResource getBestMove(
			@AuthenticationPrincipal Jwt jwt, 
			@RequestBody AiOthelloRequestResource aiOthelloRequestResource
			) {

		System.out.println("/user/v2/ai-othello/best-move");
		
		// bestmoveを取得
		AiOthelloResponseResource aiOthelloResponseResource = aiOthelloService.getAiOthelloResponse(aiOthelloRequestResource);
		String bestmove = aiOthelloResponseResource.getBestmove();
		System.out.println("bestmove: "+bestmove);
		
		// 乱数を生成してbestmoveidを作成
		LocalDateTime nowDateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String nowDateTimeString = nowDateTime.format(dateTimeFormatter);
		Random random = new Random();
		int randomInt = random.nextInt(1000000);
		String randomString = String.valueOf(randomInt);
		String bestmoveid = nowDateTimeString + randomString;
		System.out.println("bestmoveid: "+bestmoveid);
		
		// ユーザー名
		System.out.println("jwt.subject:"+jwt.getSubject());
		String userid = jwt.getSubject();
		
		// ユーザー名・bestmoveid・bestmoveの保存
		// AiOthelloUsageRecordをAiOthelloUsageRecordRepositoryに保存
		System.out.println("register ai othello usage record");
		System.out.println("userid: "+userid);
		System.out.println("bestmoveid: "+bestmoveid);
		System.out.println("bestmove: "+bestmove);
		AiOthelloUsageRecord aiOthelloUsageRecord = new AiOthelloUsageRecord();
		aiOthelloUsageRecord.setUserid(userid);
		aiOthelloUsageRecord.setBestmoveid(bestmoveid);
		aiOthelloUsageRecord.setBestmove(bestmove);
		aiOthelloUsageRecordRepository.saveAndFlush(aiOthelloUsageRecord);
		
		// レスポンスの作成
		LoginUserAiOthelloResponseResource loginUserAiOthelloResponseResource = new LoginUserAiOthelloResponseResource();
		loginUserAiOthelloResponseResource.setBestmove(bestmove);
		loginUserAiOthelloResponseResource.setBestmoveid(bestmoveid);
		
		return loginUserAiOthelloResponseResource; 
	}
	
	/**
	 * accessToken(JWTトークン)の権限の有無を確認したのちに、ユーザIDを確認し、
	 * 該当するユーザーIDの方の対戦成績を更新する。
	 * ただし、 loginUserAiOthelloResultChecker.isValidにて、
	 * オセロ盤面の推移に関する情報に異常がないか確かめており、
	 * 異常がない場合に限って、対戦成績を更新する。
	 * @param jwt
	 * @param gameResultRequestResource オセロ盤面の推移に関する情報
	 * @return
	 */
	@PostMapping("/user/v2/ai-othello/playresult/update")
	@ResponseStatus(value = HttpStatus.OK)
	public Map<String, Object> postGameResultAfterValidation(
			@AuthenticationPrincipal Jwt jwt, 
			@RequestBody GameResultRequestResource gameResultRequestResource
			) {

		// ゲームの状況のリスト
		List<GameSituation> gameSituationList = gameResultRequestResource.getGameSituationList();
		System.out.println("gameSituationList:"+gameSituationList);
		
		// userid
		System.out.println("/user/v2/ai-othello/playresult/update");
		System.out.println("jwt.subject: "+jwt.getSubject());
		String userid = jwt.getSubject();
		
		SiteUserGameRecord siteUser = siteUserGameRecordRepository.findByUserid(userid);
		Long winNum = siteUser.getWinnumber(); 
		Long loseNum = siteUser.getLosenumber();
		Long drawNum = siteUser.getDrawnumber(); 
		System.out.println("winNum before this game:"+winNum);
		System.out.println("loseNum before this game:"+loseNum);
		System.out.println("drawNum before this game:"+drawNum);
		
		// 一連のオセロ盤面の推移からチート行為がないか確認する。
		if (loginUserAiOthelloResultChecker.isValid(gameSituationList, userid)) {
			// チート行為がない場合
			String gameresult = gameResultRequestResource.getGameresult();
			System.out.println("this game result:"+gameresult); 
			if (gameresult.equals("black")) {
				++loseNum; 
			} else if (gameresult.equals("white")) {
				++winNum; 
			} else if (gameresult.equals("draw")) {
				++drawNum; 
			} 
			System.out.println("winNum after this game:"+winNum);
			System.out.println("loseNum after this game:"+loseNum);
			System.out.println("drawNum after this game:"+drawNum);
			 
			siteUserGameRecordRepository.setFixedWinnumberFor(winNum, userid);
			siteUserGameRecordRepository.setFixedLosenumberFor(loseNum, userid);
			siteUserGameRecordRepository.setFixedDrawnumberFor(drawNum, userid);
		}
		
		Map<String, Object> playResult = new HashMap<>();
		playResult.put("winNum", winNum);
		playResult.put("loseNum", loseNum);
		playResult.put("drawNum", drawNum);

		return playResult;	 
	}
	
}
