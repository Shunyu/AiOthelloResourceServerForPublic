package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import lombok.Getter; 
import lombok.Setter;

/**
 * 認可サーバーでログイン済のユーザーから受領した
 * AIオセロの最適手に関するリクエスト（accessTokenつきのリクエスト）にて、
 * 最適手をクライアントに返却するときに利用するクラス
 * 
 * 最適手以外にも、対応する乱数のデータも付与したデータを返却する。
 * @author shunyu
 *
 */
@Getter
@Setter
public class LoginUserAiOthelloResponseResource {

	private String bestmove;
	private String bestmoveid;
	
	public void setBestmoveFromAiOthelloResponseResource(AiOthelloResponseResource aiOthelloResponseResource) {
		this.bestmove = aiOthelloResponseResource.getBestmove();
	}
 
}
