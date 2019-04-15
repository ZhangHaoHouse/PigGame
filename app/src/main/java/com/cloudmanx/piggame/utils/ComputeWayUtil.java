package com.cloudmanx.piggame.utils;

import com.cloudmanx.piggame.customize.views.Item;
import com.cloudmanx.piggame.models.WayData;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/12 下午3:14
 */
public class ComputeWayUtil {

    private static final int STATE_WALKED = 3;//状态标记(已走过)

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

    /**
     * 当前pos先入队
     * 进入循环
     * 队头出队
     * 根据当前pos拿到nextPos[]，遍历
     * 从header开始添加，每次创建新的list对象储存
     * 每次检查是否已到达边界
     * 入队当前遍历的pos
     * 添加新的全部，开始下一轮
     */
    public static List<WayData> findWay2(int[][] items, WayData currentPos) {
        int verticalCount = items.length;
        int horizontalCount = items[0].length;
        Queue<WayData> way = new ArrayDeque<>();
        List<List<WayData>> footprints = new ArrayList<>();
        List<WayData> walked = new ArrayList<>();
        int[][] pattern = new int[verticalCount][horizontalCount];
        for (int vertical = 0; vertical < verticalCount; vertical++) {
            System.arraycopy(items[vertical], 0, pattern[vertical], 0, horizontalCount);
        }
        way.offer(currentPos);

        while (!way.isEmpty()){
            WayData wayData = way.poll();
            if (wayData == null)
                break;
            pattern[wayData.y][wayData.x] = STATE_WALKED;
            if (wayData.x == 0 || wayData.x == horizontalCount-1 || wayData.y == 0 || wayData.y == verticalCount -1){
                List<WayData> list = new ArrayList<>();
                WayData co = wayData;
                while (co != null){
                    list.add(0,co);
                    co = co.preWayData;
                }
                return list;
            }
            List<WayData> temp = getCanArrivePos(pattern,wayData);
            for(WayData data : temp){
                data.preWayData = wayData;
                way.offer(data);
            }
        }
//        List<WayData> temp = new ArrayList<>();
//        temp.add(currentPos);
//        footprints.add(temp);
//        pattern[currentPos.y][currentPos.x] = STATE_WALKED;
//
//        //广度优先遍历(同上)
//        while (!way.isEmpty()) {
//            WayData header = way.poll();
//            List<WayData> directions = getCanArrivePos(pattern, header);
//            List<List<WayData>> list = new ArrayList<>();
//            for (int i = 0; i < directions.size(); i++) {
//                WayData direction = directions.get(i);
//                for (List<WayData> tmp : footprints) {
//                    if (canLinks(header, tmp)) {
//                        List<WayData> list2 = new ArrayList<>(tmp);
//                        list2.add(direction);
//                        list.add(list2);
//                    }
//                }
//                if (isEdge(verticalCount, horizontalCount, direction)) {
//                    if (!list.isEmpty()) {
//                        footprints.addAll(list);
//                    }
//
//                    for (List<WayData> list2 : footprints) {
//                        if (!list2.isEmpty() && isEdge2(verticalCount, horizontalCount, list2)) {
//                            return list2;
//                        }
//                    }
//                }
//                way.offer(direction);
//
//            }
//            if (!list.isEmpty()) {
//                footprints.addAll(list);
//            }
//        }
        return null;
    }

    /**
     寻找周围6个方向可以到达的位置(不包括越界的,标记过的,不是空闲的)
     */
    public static List<WayData> getCanArrivePos(int[][] items, WayData currentPos) {
        int verticalCount = items.length;
        int horizontalCount = items[0].length;
        List<WayData> result = new ArrayList<>();
        int offset = currentPos.y % 2 == 0 ? 0 : 1, offset2 = currentPos.y % 2 == 0 ? 1 : 0;
        for (int i = 0; i < 6; i++) {
            WayData tmp = getNextPosition(currentPos, offset, offset2, i);
            if ((tmp.x > -1 && tmp.x < horizontalCount) && (tmp.y > -1 && tmp.y < verticalCount)) {
                if (items[tmp.y][tmp.x] != Item.STATE_SELECTED && items[tmp.y][tmp.x] != Item.STATE_OCCUPIED && items[tmp.y][tmp.x] != STATE_WALKED) {
                    result.add(tmp);
                    items[tmp.y][tmp.x] = STATE_WALKED;
                }
            }
        }
        Collections.shuffle(result);
        return result;
    }

    /**
     根据当前方向获取对应的位置
     */
    private static WayData getNextPosition(WayData currentPos, int offset, int offset2, int direction) {
        WayData result = new WayData(currentPos.x, currentPos.y);
        switch (direction) {
            case 0:
                //左
                result.x -= 1;
                break;
            case 1:
                //左上
                result.x -= offset;
                result.y -= 1;
                break;
            case 2:
                //左下
                result.x -= offset;
                result.y += 1;
                break;
            case 3:
                //右
                result.x += 1;
                break;
            case 4:
                //右上
                result.x += offset2;
                result.y -= 1;
                break;
            case 5:
                //右下
                result.x += offset2;
                result.y += 1;
                break;
        }
        return result;
    }

    /**
     检查src是否跟list中最后一个元素的位置是一样的
     */
    private static boolean canLinks(WayData src, List<WayData> list) {
        boolean isCanLinks = false;
        if (!list.isEmpty()) {
            WayData lastItem = list.get(list.size() - 1);
            if (lastItem.y == src.y && lastItem.x == src.x) {
                isCanLinks = true;
            }
        }
        return isCanLinks;
    }

    /**
     检查是不是在边界
     */
    public static boolean isEdge(int verticalCount, int horizontalCount, WayData direction) {
        return direction.x == 0 || direction.x == horizontalCount - 1 || direction.y == 0 || direction.y == verticalCount - 1;
    }

    /**
     检查是不是在边界
     */
    private static boolean isEdge2(int verticalCount, int horizontalCount, List<WayData> list2) {
        return list2.get(list2.size() - 1).y == 0
                || list2.get(list2.size() - 1).x == 0
                || list2.get(list2.size() - 1).y == verticalCount - 1
                || list2.get(list2.size() - 1).x == horizontalCount - 1;
    }
}
