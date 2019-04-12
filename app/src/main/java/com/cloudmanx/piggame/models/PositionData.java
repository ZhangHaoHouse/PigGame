package com.cloudmanx.piggame.models;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/12 下午3:12
 */
public class PositionData {
    public float startX;
    public float startY;
    public float endX;
    public float endY;

    @Override
    public String toString() {
        return startX + "," + startY + "," + endX + "," + endY;
    }
}
