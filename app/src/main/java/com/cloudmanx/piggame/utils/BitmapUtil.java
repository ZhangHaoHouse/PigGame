package com.cloudmanx.piggame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/9 下午6:33
 */
public class BitmapUtil {
    public static Bitmap getBitmapFromResource(Context context, @DrawableRes int id){
        return BitmapFactory.decodeResource(context.getResources(),id);
    }
}
