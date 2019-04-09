package com.cloudmanx.piggame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

    public static Bitmap toGray(Bitmap target){
        Bitmap temp = target.copy(Bitmap.Config.ARGB_8888,true);
        int width = temp.getWidth(),height =temp.getHeight();
        int[] targetPixels = new int[width * height];
        temp.getPixels(targetPixels,0,width,0,0,width,height);
        int index = 0;
        int pixelColor;
        int a,r,g,b;
        for (int y = 0;y<height;y++){
            for (int x = 0;x < width; x++){
                pixelColor = targetPixels[index];
                a = Color.alpha(pixelColor);
                r = Color.red(pixelColor);
                g = Color.green(pixelColor);
                b = Color.blue(pixelColor);
                int gray = (r + g + b)/3;
                targetPixels[index] = Color.argb(a,gray,gray,gray);
                ++index;
            }
        }
        temp.setPixels(targetPixels,0,width,0,0,width,height);
        return temp;
    }
}
