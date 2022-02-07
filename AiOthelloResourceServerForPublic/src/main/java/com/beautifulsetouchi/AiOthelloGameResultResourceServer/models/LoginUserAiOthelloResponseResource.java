package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import lombok.Getter; 
import lombok.Setter;

@Getter
@Setter
public class LoginUserAiOthelloResponseResource {

	private String bestmove;
	private String bestmoveid;
	
	public void setBestmoveFromAiOthelloResponseResource(AiOthelloResponseResource aiOthelloResponseResource) {
		this.bestmove = aiOthelloResponseResource.getBestmove();
	}
 
}
