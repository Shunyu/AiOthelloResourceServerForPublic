package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUserGameRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String userid;
	
	private Long winnumber = (long) 0;
	private Long losenumber = (long) 0;
	private Long drawnumber = (long) 0;

}
