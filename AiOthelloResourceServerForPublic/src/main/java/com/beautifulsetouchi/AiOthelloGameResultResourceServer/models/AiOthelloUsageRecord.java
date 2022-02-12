package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * AIオセロの利用状況をデータベースに登録する際に、必要となる情報を格納するentityクラス
 * @author shunyu
 *
 */
@Getter
@Setter
@Entity
public class AiOthelloUsageRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String userid;
	private String bestmoveid;
	private String bestmove;
}
