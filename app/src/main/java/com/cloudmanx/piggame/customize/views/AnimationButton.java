package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/3/27 下午5:15
 */
public class AnimationButton extends AppCompatTextView {
    private static final String TAG = "AnimationButton";
    private boolean isCancel = false;
    public AnimationButton(Context context) {
        super(context);
    }

    public AnimationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isCancel = false;
                startAnimation(getActionDownAnimation());
                Log.e(TAG, "onTouchEvent: down");
                break;
            case MotionEvent.ACTION_CANCEL:
                isCancel = true;
                startAnimation(getActionUpAnimation());
                Log.e(TAG, "onTouchEvent: cancel");
                break;
            case MotionEvent.ACTION_MOVE:
                if ( !isCancel && (event.getX() < 0 ||event.getY()<0 || event.getX()>this.getWidth() || event.getY() > this.getHeight())){
                    startAnimation(getActionUpAnimation());
                    isCancel = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isCancel){
                    Animation animation = getActionUpAnimation();
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            performClick();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    startAnimation(animation);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private Animation getActionDownAnimation(){
        ScaleAnimation animation = new ScaleAnimation(1,0.9F,1,0.9F,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(150);
        animation.setFillAfter(true);
        return animation;
    }

    private Animation getActionUpAnimation(){
        ScaleAnimation animation = new ScaleAnimation(.9f,1,.9f,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.start();
        return animation;

    }
}
