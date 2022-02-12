package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * データベースに格納している対戦成績のデータを抽出したり、
 * データベースに新規ユーザーを0勝0負0分で格納したりする際に利用するクラス
 * @author shunyu
 *
 */
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
