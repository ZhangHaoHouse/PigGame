package com.cloudmanx.piggame.customize;

import android.graphics.PointF;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/23 下午2:35
 */
public abstract class Keyframes {
    MyPath mPath;
    PointF mTempPointF;

    Keyframes(MyPath path){
        if (path == null || path.isEmpty()){
            throw new IllegalArgumentException("The path must not be null or empty");
        }
        mTempPointF = new PointF();
        init(path);
    }

    void reverse(){
        List<PointF> data = mPath.getData();
        MyPath path = new MyPath();
        Collections.reverse(data);
        path.moveTo(data.get(0).x,data.get(0).y);
        Queue<PointF> queue = new ArrayDeque<>(data);
        while (!queue.isEmpty()){
            PointF item = queue.poll();
            if (item == null)
                continue;
            if (!queue.isEmpty()){
                PointF item2 = queue.poll();
                if (item2 == null)
                    continue;
                path.quadTo(item.x,item.y,item2.x,item2.y);
            }else {
                path.lineTo(item.x,item.y);
            }
        }
        init(path);
    }

    abstract PointF getValue(float fraction);

    abstract void init(MyPath path);
}
