package com.cloudmanx.piggame.models;

import android.content.Context;

import com.cloudmanx.piggame.R;

import java.util.Locale;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/18 下午4:56
 */
public class MissionData {
    public long speed;//小猪速度
    public long propDelay;//树头出现的隔间时间
    public int mustCaughtCount;//必须要捉到的小猪个数

    public String toString(Context context, int currentLevel) {
        String format = context.getString(R.string.pigsty_mode_mission_message_format);
        if (currentLevel < 0) {
            format = format.split("\n\n")[1];
            return String.format(Locale.getDefault(), format, mustCaughtCount);
        }
        return String.format(Locale.getDefault(), format, currentLevel, mustCaughtCount);
    }
}
