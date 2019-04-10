package com.cloudmanx.piggame.models;

import android.support.annotation.NonNull;

import com.cloudmanx.piggame.customize.views.Item;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/10 下午6:23
 */
public class WayData2 extends WayData {
    public Item item;

    public WayData2(Item item,int count,boolean isBlock){
        super(count,isBlock,0,0);
        this.item = item;
    }

    @NonNull
    @Override
    public String toString() {
        return "isBlock:"+isBlock+"\ncount:"+count;
    }
}
