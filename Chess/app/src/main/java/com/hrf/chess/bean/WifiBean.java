package com.hrf.chess.bean;

import java.util.List;

/**
 * User: HRF
 * Date: 2017/8/28
 * Time: 15:33
 * Description: Too
 */
public class WifiBean {
    public boolean isClose;//是否关闭
    public boolean isStart;//是否开始
    public String oneIp;//ip
    public String towIp;//ip
    public List<String> otherIpList;//观战ip
    public String firstIp;//先手ip 黑色先手
    public String runIp;//
    public Piece runPiece;//
    public Piece onePiece;//
    public Piece towPiece;//
    public List<Piece> pieceList;
    public List<Piece> hintList;//提示标记
    public String victoryIp;//胜利ip
    public String giveUpIp;//认输ip
    public boolean isPiece;//棋子是否完成下一步


    public class Piece {
        public String name;//棋子名
        public int x;//x坐标
        public int y;//y坐标
        public boolean direction;//true表示红方

        public Piece() {
        }

        public Piece(String name, int x, int y, boolean direction) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }
}
