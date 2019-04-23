package com.cloudmanx.piggame.customize;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/23 下午2:35
 */
public class MyPath extends Path {

    private List<PointF> data;

    public MyPath(){
        super();
        data = new ArrayList<>();
    }

    @Override
    public void moveTo(float x, float y) {
        super.moveTo(x, y);
        data.add(new PointF(x,y));
    }

    @Override
    public void lineTo(float x, float y) {
        super.lineTo(x, y);
        data.add(new PointF(x,y));
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        super.quadTo(x1, y1, x2, y2);
        data.add(new PointF(x1,y1));
        data.add(new PointF(x2,y2));
    }

    public List<PointF> getData() {
        return data;
    }
}
