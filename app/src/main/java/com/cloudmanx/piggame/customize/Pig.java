package com.cloudmanx.piggame.customize;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.view.MotionEvent;

import com.cloudmanx.piggame.models.WayData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/3/27 下午4:07
 */
public class Pig {

    public static final int STATE_STANDING = 0, STATE_RUNNING = 1, STATE_DRAGGING = 2;
    public static final int ORIENTATION_LEFT = 0, ORIENTATION_RIGHT = 1;

    public void setX(float x){};
    public void setY(float y){};

    /**
     * 小猪状态: (拖动中, 逃跑中, 站立中)
     */
    @IntDef({STATE_DRAGGING, STATE_RUNNING, STATE_STANDING})
    @IntRange(from = STATE_STANDING, to = STATE_DRAGGING)
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    /**
     * 小猪的面朝方向: 左, 右
     */
    @IntDef({ORIENTATION_LEFT, ORIENTATION_RIGHT})
    @IntRange(from = ORIENTATION_LEFT, to = ORIENTATION_RIGHT)
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    public interface OnTouchListener {
        void onTouch(Pig pig, MotionEvent event, int index);
    }

    public interface OnPositionUpdateListener {
        void onUpdate(Pig pig, WayData oldPosition, WayData newPosition);
    }

    public interface OnLeavedListener {
        void onLeaved();
    }
}
