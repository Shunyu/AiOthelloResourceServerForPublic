package com.beautifulsetouchi.AiOthelloGameResultResourceServer.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
		
		http.cors().and()
			.csrf().disable() // JWTの利用
			.authorizeRequests()
			.antMatchers("/user/v2/ai-othello/best-move").hasRole("user")
			.antMatchers("/user/v2/ai-othello/playresult").hasRole("user")
			.antMatchers("/user/v2/ai-othello/playresult/update").hasRole("user")
			.anyRequest().denyAll()
			.and()
			.oauth2ResourceServer()
			.jwt()
			.jwtAuthenticationConverter(jwtAuthenticationConverter);
	}

}
