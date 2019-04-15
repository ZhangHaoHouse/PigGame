package com.cloudmanx.piggame.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/8 下午6:12
 */
public class WayData {
    public int count;//方向上空闲状态的格子数
    public boolean isBlock;//中间是否有障碍
    public int x,y;//位置
    public WayData preWayData = null;

    public WayData(int count, boolean isBlock, int x, int y) {
        this.count = count;
        this.isBlock = isBlock;
        this.x = x;
        this.y = y;
    }
    public WayData(int x,int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof WayData ? x == ((WayData) obj).x && y == ((WayData) obj).y :this == obj;
    }

    @NonNull
    @Override
    public String toString() {
//        return "x: " + x + "\ty: " + y;
        return y + "," + x;
    }
}
