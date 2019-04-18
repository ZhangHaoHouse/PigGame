package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.customize.MyDrawable;
import com.cloudmanx.piggame.customize.Pig;
import com.cloudmanx.piggame.models.MissionData;
import com.cloudmanx.piggame.models.WayData;
import com.cloudmanx.piggame.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/18 上午11:31
 */
public class PigstyMode extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private static final int MAX_PROP_SIZE;//树头最大同时存在的数量(未放置的)
    private static final int PIGGY_COUNT;//小猪数量
    private static final int VERTICAL_COUNT;//矩形行数
    private static final int HORIZONTAL_COUNT;//矩形列数
    private static final long FENCE_FIX_ANIMATION_DURATION;//树头调整位置的动画时长

    static {
        MAX_PROP_SIZE = 6;
        HORIZONTAL_COUNT = 15;
        VERTICAL_COUNT = 23;
        PIGGY_COUNT = 6;
        FENCE_FIX_ANIMATION_DURATION = 150L;
    }

    private final byte[] PROP_GENERATE_TASK_LOCK;//树头生成的锁(当树头(未放置的)达到设定的最大值后,暂停生成)
    private String mLevelStringFormat;//当前关卡格式
    private String mCarIsComingText, //车来了
            mPiggiesHasRunText, //猪全跑了
            mStartCatchText, //开始捉猪
            mDragPigText;//把小猪拖到车上
    private Future mDrawTask,//绘制线程
            mPropGenerateTask;//树头生成线程
    private SurfaceHolder mSurfaceHolder;
    private Rect[][] mItems;//矩形二维数组
    private volatile int[][] mItemStatus;//用来保存对应的矩形状态（小猪占用，木头占用，空闲）
    private Pig[] mPiggies;//小猪实例
    private volatile boolean isDrawing;//绘制中
    private int mPropSize,//树头尺寸
            mItemSize;//矩形尺寸
    //临时保存的数据
    private Set<Integer> mDraggingProp,//正在拖动的树头
            mDraggingPiggies;//正在拖动的小猪
    //正在拖动的小猪,树头的触摸事件的id(用来确定是哪一个手指)
    private SparseIntArray mDraggingPropIds,mDraggingPiggyIds;
    private Future[] mComputePathTasks;//用来计算小猪逃跑线的线程
    ////////////////////////////////////////////////////////
    private WayData[] mPiggiesOccupiedPosition;//用来保存各个小猪当前占用中的矩形坐标
    private Bitmap mFrameBackgroundBitmap;//顶部绿色的背景(用来释放资源)
    private NinePatch mFrameBackground;//顶部绿色背景的.9图
    private MyDrawable mCarHead,//车头
            mCarBody;//车身
    private volatile boolean isNeed;//是否需要更新树头坐标
    private int mWidth, mHeight;//屏幕宽高
    private int mLeftOffset;//树头的左边偏移量
    private int mTop;//树头(未放置的)top值
    private volatile boolean isStopped;//停止生成
    private volatile boolean isPiggyByCar; //小猪在坐车(不接受手指拖动事件,小猪跟随小车移动)
    private boolean isFirstInit, //第一次初始化(播放小车开过动画)
            isGameOver,//游戏结束
            isWon, //赢了
            isCarDispatched, //车在路上(屏幕内的小猪全部无路走,被围住了)
            isCarArrived, //车来到了
            isAllPiggiesAreReady;//被围住的小猪全部都被拖上车了
    private boolean[] mCarriageIsOccupied;//小车后面的车厢是否空闲状态
    private TextPaint mPaint;
    private List<Integer> mCaughtPiggies;//被围住的小猪
    private SparseIntArray mCaughtPiggiesPosition;//key: 车厢索引 value: 车厢上对应的小猪索引
    private long mStartTime;//开始时间
    private int mCurrentLevel;//当前关卡
    private boolean isMissionDialogShown;//任务对话框
    private int mValidCaughtCount;//有效捉到的小猪
    private MissionData mMissionData;//任务
    private AlertDialog mMissionDialog, mGameResultDialog, mExitDialog, mHeartEmptyDialog;

    {
        PROP_GENERATE_TASK_LOCK = new byte[0];//最省内存
    }

    public PigstyMode(Context context) {
        this(context,null);
    }

    public PigstyMode(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PigstyMode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
        isFirstInit = true;
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(this);
        mCaughtPiggies = new ArrayList<>();
//        mDraggingProps = new HashSet<>();
        mDraggingPiggies = new HashSet<>();
        mDraggingPropIds = new SparseIntArray();
        mDraggingPiggyIds = new SparseIntArray();
        mCaughtPiggiesPosition = new SparseIntArray();
        mComputePathTasks = new Future[PIGGY_COUNT];
        mPiggiesOccupiedPosition = new WayData[PIGGY_COUNT];
        mCarriageIsOccupied = new boolean[PIGGY_COUNT];
        mItemSize = (int) getContext().getResources().getDimension(R.dimen.xhpx_64);
        mPropSize = (int) getContext().getResources().getDimension(R.dimen.xhpx_108);
        mFrameBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_frame);
        mFrameBackground = new NinePatch(mFrameBackgroundBitmap, mFrameBackgroundBitmap.getNinePatchChunk(), null);
        mLevelStringFormat = getContext().getString(R.string.level_format);
    }

    private void initCar() {
        mCarBody = new MyDrawable(0, BitmapUtil.getBitmapFromResource(getContext(), R.mipmap.ic_car_body));
        Bitmap carHead0, carHead1, carHead2, carHead3;
        carHead0 = BitmapUtil.getBitmapFromResource(getContext(), R.mipmap.ic_car_head_0);
        carHead1 = BitmapUtil.getBitmapFromResource(getContext(), R.mipmap.ic_car_head_1);
        carHead2 = BitmapUtil.getBitmapFromResource(getContext(), R.mipmap.ic_car_head_2);
        carHead3 = BitmapUtil.getBitmapFromResource(getContext(), R.mipmap.ic_car_head_3);
        mCarHead = new MyDrawable(50, carHead0, carHead1, carHead2, carHead3, carHead2, carHead1);
        mCarHead.start();
    }

    private void initPaint() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getResources().getDimension(R.dimen.xhpx_38));
        mPaint.setColor(getResources().getColor(R.color.colorHint));

        mCarIsComingText = getResources().getString(R.string.car_is_coming);
        mPiggiesHasRunText = getResources().getString(R.string.piggies_has_run);
        mStartCatchText = getResources().getString(R.string.start_catch);
        mDragPigText = getResources().getString(R.string.drag_pig);
    }

    private void initPiggies() {
//        mPiggies = new Pig[PIGGY_COUNT];
//        Pig.OnTouchListener onTouchListener = (pig, event, index) -> {
//            switch (event.getAction() & event.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    pigActionDown(pig, event, index);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    pigActionMove(pig, event, index);
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_POINTER_UP:
//                    pigActionUp(pig, false);
//                    break;
//                default:
//                    break;
//            }
//        };
//        //小猪所在的矩形变更(更新位置)
//        Pig.OnPositionUpdateListener onPositionUpdateListener = (pig, oldPosition, newPosition) -> {
//            changeItemStatus(oldPosition.y, oldPosition.x, Item.STATE_UNSELECTED);
//            if (pig.getState() == Pig.STATE_RUNNING) {
//                //将小猪上一个矩形的状态设为空闲,将新的矩形状态设为小猪占用
//                changeItemStatus(newPosition.y, newPosition.x, Item.STATE_OCCUPIED);
//                pig.setPosition(newPosition.y, newPosition.x);
//                mPiggiesOccupiedPosition[pig.getIndex()] = newPosition;
//            }
//        };
//        //每一个小猪逃跑动画结束后,都检查一次是否满足游戏结束条件
//        Pig.OnLeavedListener onLeavedListener = this::checkIsGameOver;
//        float scale = mItemSize / (mItemSize * 1.15F);
//        //初始化小猪
//        for (int i = 0; i < PIGGY_COUNT; i++) {
//            Pig pig = new Pig(getContext(), scale);
//            pig.setIndex(i);
//            pig.setOnTouchListener(onTouchListener);
//            pig.setOnPositionUpdateListener(onPositionUpdateListener, mItemSize);
//            pig.setOnLeavedListener(onLeavedListener);
//            mPiggies[i] = pig;
//        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {

    }

    public interface OnExitedListener {
        void onExited();
    }
}
