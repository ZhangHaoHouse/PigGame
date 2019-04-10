package com.cloudmanx.piggame.customize.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.MyLayoutParams;
import com.cloudmanx.piggame.utils.LevelUtil;

import java.util.Random;
import java.util.Stack;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/10 上午10:53
 */
public class ClassicMode extends ViewGroup {

    private int[][] mItemStatus;//棋盘状态
    private Item[][] mItems;//棋盘的实例对象
    private Stack<int[][]> mHistory;//保存历史
    private int mHorizontalPos,mVerticalPos;//当前小猪的位置
    private int mItemSize;//单个格子的尺寸
    private int mItemSpacing;//格子之间的外间距
    private int mVerticalCount;//棋盘的行数
    private int mHorizontalCount;//棋盘的列数
    private int mItemPadding;//格子之间的内间距
    private View mDropTouchView;//接受拖动手势的view
    private OnTouchListener mDropTouchListener;//拖动小猪的监听
    private ImageView mSelectedView,//选择状态下的view（有木头）
            mOccupiedView,//战用状态（小猪站立的）
            mDropView;//拖动状态（小猪被拖动）
    private boolean isAnimationPlaying,mLastItemIsLeft;//小猪面朝的方向
    private int mOffset;
    private Random mRandom;

    //小猪各种状态下的动画
    private AnimationDrawable mGoLeftAnimationDrawabel,mGoRightAnimationDrawable,
            mDropLeftAnimationDrawable, mDropRightAnimationDrawable;
    private OnGameOverListener mOnGameOverListener;
    private OnPiggyDraggedListener mOnPiggyDraggedListener;
    private boolean isDragEnable,//开启拖动；
            isNavigationOn;//开启导航
    private boolean isGameOver;
    private int mCurrentLevel;
    private int mLastX,mLastY;

    public ClassicMode(Context context) {
        this(context,null);
    }

    public ClassicMode(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ClassicMode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClassicMode,defStyleAttr,0);
        mItemSize = (int)a.getDimension(R.styleable.ClassicMode_item_size,36);
        mItemSpacing = (int)a.getDimension(R.styleable.ClassicMode_item_spacing,4);
        mVerticalCount = (int)a.getInteger(R.styleable.ClassicMode_item_vertical_count,8);
        mHorizontalCount = a.getInteger(R.styleable.ClassicMode_item_horizontal_count,8);
        if (mItemSpacing == 0){
            mItemPadding = (int) getResources().getDimension(R.dimen.item_spacing);
        }
        a.recycle();
        init();
    }

    public void setDragEnable(){
        isDragEnable = true;
    }

    public void setNavigationOn(){
        isNavigationOn = true;
    }

    public void setLevel(int level){
        if (isAnimationPlaying){
            return;
        }
        isDragEnable = isNavigationOn = false;
        mCurrentLevel = level;
        refresh();
        if (mCurrentLevel > 0){
            //初始化默认的木头
            int[][] position = LevelUtil.getDefaultFencePosition(mVerticalCount,mHorizontalCount,level);
            for (int vertical = 0;vertical<mVerticalCount;vertical++){
                for (int horizontal = 0;horizontal < mHorizontalCount;horizontal++){
                    if (position[vertical][horizontal] == Item.STATE_SELECTED){
                        mItemStatus[vertical][horizontal] = Item.STATE_SELECTED;
                        mItems[vertical][horizontal].setStatus(Item.STATE_SELECTED);
                    }
                }
            }
        }else {
            //随机选择木头
            setRandomSelected();
        }

    }

    public void refresh(){

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        mHistory = new Stack<>();
        mRandom = new Random();
        setClipChildren(false);
        setClipToPadding(false);
        mItemStatus = new int[mVerticalCount][mHorizontalCount];
        mItems = new Item[mVerticalCount][mHorizontalCount];
        /*
        小猪的触摸监听:
		action down: 隐藏站立的小猪，显示拖动状态的小猪，并播放动画
		action move：跟随手指移动
		action up：根据小猪的腿的位置来判断应该要把小猪放在哪个格子上
		*/
        mDropTouchListener = (v,event) ->{
            if (!isDragEnable){
                return false;
            }
            MyLayoutParams layoutParams = (MyLayoutParams) mDropView.getLayoutParams();
            int x = (int) event.getX(),y = (int)event.getY();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isAnimationPlaying = true;
                    mLastX = x;
                    mLastY = y;
                    mItems[mVerticalPos][mHorizontalPos].hideOccupiedImage();
                    layoutParams.isDrag = false;
                    requestLayout();
                    if (mLastItemIsLeft = mItems[mVerticalPos][mHorizontalPos].isLeft()){
                        mDropView.setImageDrawable(mDropLeftAnimationDrawable);
                        mDropLeftAnimationDrawable.start();
                    }else {
                        mDropView.setImageDrawable(mDropRightAnimationDrawable);
                        mDropRightAnimationDrawable.start();
                    }
                    mDropView.setVisibility(VISIBLE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    layoutParams.isDrag = true;
                    layoutParams.x = x - mLastX;
                    layoutParams.y = y - mLastY;
                    requestLayout();
                    mLastX = x;
                    mLastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    layoutParams = (MyLayoutParams) mDropView.getLayoutParams();
                    layoutParams.isDrag = true;
                    layoutParams.x = x - mLastX;
                    layoutParams.y = y - mLastY;
                    requestLayout();
                    mLastX = x;
                    mLastY = y;
                    layoutParams.isDrag = false;

                    break;
            }
            return true;
        };
    }

    private void locationOccupiedView(float x,float y){
        RectF rect = new RectF();
        boolean isPlaceChanged = false;
        //根据x,y查找所在的格子
        tag:for (int vertical = 0; vertical < mVerticalCount;vertical++){
            for (int horizontal = 0;horizontal < mHorizontalCount;horizontal++){
                Item item = mItems[vertical][horizontal];
                rect.left = item.getLeft();
                rect.right = item.getRight();
                rect.top = item.getTop();
                rect.bottom = item.getBottom();
                if (rect.contains(x,y) && mItemStatus[vertical][horizontal] == Item.STATE_UNSELECTED){
                    mDropView.setVisibility(GONE);
                    if (mLastItemIsLeft){
                        mDropRightAnimationDrawable.stop();
                    }else {
                        mDropRightAnimationDrawable.stop();
                    }
                    mItems[mVerticalPos][mHorizontalPos].setStatus(Item.STATE_UNSELECTED);
                    mItemStatus[mVerticalPos][mHorizontalPos] = Item.STATE_UNSELECTED;
                    item.setIsLeft(mLastItemIsLeft);
                    item.setStatus(Item.STATE_OCCUPIED);
                    mItemStatus[vertical][horizontal] = Item.STATE_OCCUPIED;
                    requestLayout();
                    mVerticalPos = vertical;
                    mHorizontalPos = horizontal;
                    mOffset = mVerticalPos % 2 == 0 ? 0:1;
                    isPlaceChanged = true;
                    break tag;
                }
            }
        }
        isAnimationPlaying = false;
        //x,y所在的位置无效
        if (!isPlaceChanged){
            mItems[mVerticalPos][mHorizontalPos].showOccupiedImage();
            mDropView.setVisibility(GONE);
            if (mLastItemIsLeft){
                mDropLeftAnimationDrawable.stop();
            }else {
                mDropRightAnimationDrawable.stop();
            }
        }else {
            //重新检测是否被木头围住了
//            WayData2 left =
        }
    }

    /**
     随机木头
     */
    private void setRandomSelected() {
        int selectedSize = (Math.min(mHorizontalCount, mVerticalCount) / 2) + 1;
        int tmp = 0;
        while (tmp < selectedSize) {
            int vertical = mRandom.nextInt(mVerticalCount);
            int horizontal = mRandom.nextInt(mHorizontalCount);
            if (mItemStatus[vertical][horizontal] == Item.STATE_UNSELECTED || mItemStatus[vertical][horizontal] == Item.STATE_GUIDE) {
                mItemStatus[vertical][horizontal] = Item.STATE_SELECTED;
                mItems[vertical][horizontal].setStatus(Item.STATE_SELECTED);
                tmp++;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public interface OnGameOverListener{
        void onWin();
        void onLost();
    }

    private interface ComputeDrection{
        Item getItem(int count,boolean isSameGroup,int tmp);
    }
    public interface OnPiggyDraggedListener{
        void onDragged();
    }
}
