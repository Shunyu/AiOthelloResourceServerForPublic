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


@RestController
@CrossOrigin("*")
public class AiOthelloGameResultController {

	private final SiteUserGameRecordRepository siteUserGameRecordRepository;
	private final AiOthelloService aiOthelloService;
	private final AiOthelloUsageRecordRepository aiOthelloUsageRecordRepository;
	private final LoginUserAiOthelloResultChecker loginUserAiOthelloResultChecker;
	
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
		
		if (loginUserAiOthelloResultChecker.isValid(gameSituationList, userid)) {		
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
