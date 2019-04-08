package com.cloudmanx.piggame.utils;

import android.animation.ValueAnimator;

import java.lang.reflect.Field;

/**
 * @version 1.0
 * @Description:
 * @Author: zhh
 * @Date: 2019/4/8 23:41
 */
public class ValueAnimatorUtil {

    public static void resetDutationScale(){
        try {
            getField().setFloat(null,1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Field getField() throws NoSuchFieldException{
        Field field = ValueAnimator.class.getDeclaredField("sDurationScale");
        field.setAccessible(true);
        return field;
    }
}
