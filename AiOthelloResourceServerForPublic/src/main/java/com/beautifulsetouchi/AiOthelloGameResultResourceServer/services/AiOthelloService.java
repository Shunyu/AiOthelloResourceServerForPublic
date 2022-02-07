
package com.beautifulsetouchi.AiOthelloGameResultResourceServer.services;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service; 
import org.springframework.web.client.RestTemplate;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloRequestResource;
import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloResponseResource;

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
