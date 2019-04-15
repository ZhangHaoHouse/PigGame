package com.cloudmanx.piggame.models;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/13 上午11:26
 */
public class Position {

    private static int minHPos = 0;
    private static int minVPos = 0;
    private static int maxVPos = Integer.MAX_VALUE;
    private static int maxHPos = Integer.MAX_VALUE;


    public int vPos;
    public int hPos;
    public Position next;
    private boolean isOdd;//是否是奇数行

    public Position(int vPos, int hPos) {
        if (vPos <minVPos || vPos > maxVPos
                ||hPos < minHPos || hPos > maxHPos ){
            throw new IndexOutOfBoundsException("坐标超出边界");
        }
        this.vPos = vPos;
        this.hPos = hPos;
        isOdd = vPos % 2 == 0;
    }

    public Position getLeft(){
        if (hPos > minHPos){
            return new Position(vPos,hPos-1);
        }
        return null;
    }

    public Position getRight(){
        if (hPos < maxHPos){
            return new Position(vPos,hPos+1);
        }
        return null;
    }

    public Position getTopLeft(){
        int ver;
        int hor;
        ver = vPos - 1;
        if (isOdd){
            hor = hPos;
        }else {
            hor = hPos - 1;
        }
        if (ver > minVPos -1 && ver < maxVPos +1 && hor >minHPos -1 && hor < maxHPos +1){
            return new Position(ver,hor);
        }
        return null;
    }

    public Position getTopRight(){
        int ver;
        int hor;
        ver = vPos - 1;
        if (isOdd){
            hor = hPos+1;
        }else {
            hor = hPos;
        }
        if (ver > minVPos -1 && ver < maxVPos +1 && hor >minHPos -1 && hor < maxHPos +1){
            return new Position(ver,hor);
        }
        return null;
    }

    public Position getBottomLeft(){
        int ver;
        int hor;
        ver = vPos + 1;
        if (isOdd){
            hor = hPos;
        }else {
            hor = hPos - 1;
        }
        if (ver > minVPos -1 && ver < maxVPos +1 && hor >minHPos -1 && hor < maxHPos +1){
            return new Position(ver,hor);
        }
        return null;
    }

    public Position getBottomRight(){
        int ver;
        int hor;
        ver = vPos + 1;
        if (isOdd){
            hor = hPos + 1;
        }else {
            hor = hPos;
        }
        if (ver > minVPos -1 && ver < maxVPos +1 && hor >minHPos -1 && hor < maxHPos +1){
            return new Position(ver,hor);
        }
        return null;
    }

    public static int getMinHPos() {
        return minHPos;
    }

    public static void setMinHPos(int minHPos) {
        Position.minHPos = minHPos;
    }

    public static int getMinVPos() {
        return minVPos;
    }

    public static void setMinVPos(int minVPos) {
        Position.minVPos = minVPos;
    }

    public static int getMaxVPos() {
        return maxVPos;
    }

    public static void setMaxVPos(int maxVPos) {
        Position.maxVPos = maxVPos;
    }

    public static int getMaxHPos() {
        return maxHPos;
    }

    public static void setMaxHPos(int maxHPos) {
        Position.maxHPos = maxHPos;
    }
}
