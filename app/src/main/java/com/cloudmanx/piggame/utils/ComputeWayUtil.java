package com.cloudmanx.piggame.utils;

import com.cloudmanx.piggame.models.WayData;

import java.util.List;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/12 下午3:14
 */
public class ComputeWayUtil {

    /**
     经典模式小猪找出路
     */
    public static WayData findWay(int level, int[][] items, WayData currentPos, List<WayData> data) {
        //经典模式第10关之后,小猪变聪明
        if (level < 0 || level > 10) {
            List<WayData> list = findWay2(items, currentPos);
            if (list != null && list.size() >= 2) {
                return list.get(1);
            }
        }
        //第10关之前,向周围没有被拦住(可以直线一直走)的方向走
        WayData result = null;
        for (WayData tmp : data) {
            if (!tmp.isBlock) {
                result = tmp;
                break;
            }
        }
        return result;
    }
}
