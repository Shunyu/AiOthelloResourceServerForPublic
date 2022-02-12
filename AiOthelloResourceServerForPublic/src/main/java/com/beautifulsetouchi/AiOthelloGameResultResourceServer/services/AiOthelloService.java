
package com.beautifulsetouchi.AiOthelloGameResultResourceServer.services;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service; 
import org.springframework.web.client.RestTemplate;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloRequestResource;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloResponseResource;

/**
 * 最適手を取得するリクエストが来た際に、
 * AIオセロの機能を提供する別サーバーにリクエストを仲介する際に利用するクラス
 * @author shunyu
 *
 */
@Service 
public class AiOthelloService {

	@Autowired
	private RestTemplate restTemplate;
	
	public static final String URL = "xxxx";
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}

	
	public AiOthelloResponseResource getAiOthelloResponse(AiOthelloRequestResource request) {
		
		return restTemplate.postForObject(URL, request, AiOthelloResponseResource.class); 
	}

 
}
