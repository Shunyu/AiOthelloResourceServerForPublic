package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

import lombok.Getter; 
import lombok.Setter;

/**
 * 別サーバーであるAIオセロサーバー宛に
 * 最適手のリクエストを行う際の、リクエストボディのクラス
 * 
 * リソースサーバーは最適手に関するリクエストを仲介し、
 * AIオセロサーバーに、改めて最適手のリクエストを送る。
 * @author shunyu
 *
 */
@Getter
@Setter
public class AiOthelloRequestResource {

	private int nextplayer;
	private String boardarray;

}
