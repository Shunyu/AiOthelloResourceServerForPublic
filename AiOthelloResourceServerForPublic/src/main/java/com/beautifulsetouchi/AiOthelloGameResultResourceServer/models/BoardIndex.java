package com.beautifulsetouchi.AiOthelloGameResultResourceServer.models;

/**
 * オセロの盤面のマスの位置の情報を示すクラス
 * @author shunyu
 *
 */
public class BoardIndex {
    private int verticalIndex;
    private int horizontalIndex;

    public BoardIndex(int verticalIndex, int horizontalIndex) {
        this.verticalIndex = verticalIndex;
        this.horizontalIndex = horizontalIndex;
    }

    public void setVerticalIndex(int verticalIndex) {
        this.verticalIndex = verticalIndex;
    }

    public void setHorizontalIndex(int horizontalIndex) {
        this.horizontalIndex = horizontalIndex;
    }


    public int getVerticalIndex() {
        return verticalIndex;
    }

    public int getHorizontalIndex() {
        return horizontalIndex;
    }
}
