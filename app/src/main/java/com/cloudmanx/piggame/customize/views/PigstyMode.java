package com.cloudmanx.piggame.customize.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.cloudmanx.piggame.PigApplication;
import com.cloudmanx.piggame.R;
import com.cloudmanx.piggame.activities.MainActivity;
import com.cloudmanx.piggame.customize.MyDrawable;
import com.cloudmanx.piggame.customize.MyValueAnimator;
import com.cloudmanx.piggame.customize.Pig;
import com.cloudmanx.piggame.models.MissionData;
import com.cloudmanx.piggame.models.WayData;
import com.cloudmanx.piggame.utils.BitmapUtil;
import com.cloudmanx.piggame.utils.LevelUtil;
import com.cloudmanx.piggame.utils.ThreadPool;

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
        init();
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
        initCar();
        initPaint();
        initPiggies();
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
        mPiggies = new Pig[PIGGY_COUNT];
        Pig.OnTouchListener onTouchListener = ((pig, event, index) -> {
            switch (event.getAction() & event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    pigActionMove(pig, event, index);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    pigActionUp(pig, false);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 手指按下小猪(小猪跟随手指移动)
     */
    private void pigActionDown(Pig pig, MotionEvent event, int index) {
    }

    /**
     * 手指按下小猪移动(播放小猪被拖动的动画)
     */
    private void pigActionMove(Pig pig, MotionEvent event, int index) {}

    /**
     * 手指松开小猪(重新定位小猪位置,并且开始找出路)
     */
    private void pigActionUp(Pig pig, boolean isPlayAnimation) {}

    /**
     * 开始绘制
     */
    public void restart() {
        isStopped = false;
        if (mStartTime == 0){
            mStartTime = SystemClock.uptimeMillis();
        }
        if (mItems == null){
//            initItems();
        }
//        if (mPropOffsetHelper == null) {
//            initPropOffsetHelper();
//        }
        mWidth = getWidth();
        mHeight = getHeight();
//        mTop = getHeight() - mPropOffsetHelper.getPropHeight();
//        mLeftOffset = (mPropSize - mPropOffsetHelper.getPropWidth()) / 2;
        isDrawing = true;
        mDrawTask = ThreadPool.getInstance().execute(this);
        if (isFirstInit) {
            playInitAnimation();
        } else {
            if (!isGameOver) {
                startGenerate();
            }
        }
    }

    /**
     * 小车开过的动画
     */
    private void playInitAnimation() {
        isPiggyByCar = true;
        mCarHead.setX(mWidth + mCarHead.getIntrinsicWidth());
        float y = mHeight / 2 - mCarHead.getIntrinsicHeight();
        mCarHead.setY(y);
        MyValueAnimator.create(mWidth, -(mCarHead.getIntrinsicWidth() * 2 + mCarBody.getIntrinsicWidth() * 6), y, y, mCarHead)
                .setDuration(10000).setOnAnimatorMiddleListener(() -> {
            isPiggyByCar = false;
            startGenerate();
        }).start();
        isFirstInit = false;
    }

    private void startGenerate() {
        isNeed = true;
        startGenerateProp();
//        mPropOffsetHelper.startComputeOffset();
    }

    private void startGenerateProp() {
//        mPropGenerateTask = ThreadPool.getInstance().execute(() -> {
//            while (isNeed) {
//                if (mPropOffsetHelper == null) {
//                    return;
//                }
//                synchronized (PROP_GENERATE_TASK_LOCK) {
//                    //当前未离队的树头数量达到指定值,则暂停生成
//                    while (mPropOffsetHelper.getQueueSize() >= MAX_PROP_SIZE) {
//                        try {
//                            LogUtil.print("线程暂停");
//                            PROP_GENERATE_TASK_LOCK.wait();
//                        } catch (InterruptedException e) {
//                            return;
//                        }
//                    }
//                }
//                LogUtil.print("线程休眠");
//                try {
//                    Thread.sleep(mMissionData.propDelay);
//                } catch (InterruptedException e) {
//                    return;
//                }
//                if (!isNeed || mPropOffsetHelper == null) {
//                    return;
//                }
//                mPropOffsetHelper.addProp();
//            }
//        });
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

    public void setCurrentLevel(int currentLevel) {
        mCurrentLevel = currentLevel;
        if (mCurrentLevel > LevelUtil.PIGSTY_MODE_MAX_LEVEL) {
            mCurrentLevel = -1;
        }
        mMissionData = LevelUtil.getMissionData(currentLevel);
        showMissionDialog();
    }

    /**
     * 任务对话框
     */
    private void showMissionDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pigsty_mode_mission_view, null, false);
        OnClickListener onClickListener = v -> {
            switch (v.getId()) {
                case R.id.start_button:
                    mMissionDialog.dismiss();
                    isMissionDialogShown = true;
                    PigApplication.savePigstyModeCurrentValidHeartCount(getContext(), PigApplication.getPigstyModeCurrentValidHeartCount(getContext()) - 1);
                    restart();
                    break;
                case R.id.menu_button:
                    ((MainActivity) getContext()).backToHome();
                    break;
                default:
                    break;
            }
        };
        ((TextView) dialogView.findViewById(R.id.message)).setText(mMissionData.toString(getContext(), mCurrentLevel));
        dialogView.findViewById(R.id.start_button).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.menu_button).setOnClickListener(onClickListener);
        mMissionDialog = new AlertDialog.Builder(getContext(), R.style.DialogTheme).setView(dialogView).setCancelable(false).show();
    }
    public void exit(OnExitedListener listener) {
        showExitDialog(listener);
    }

    private void showExitDialog(OnExitedListener listener) {
        if (mGameResultDialog != null && mGameResultDialog.isShowing()) {
            mGameResultDialog.dismiss();
            exitNow();
            listener.onExited();
        } else if (mMissionDialog != null && mMissionDialog.isShowing()) {
            mMissionDialog.dismiss();
            exitNow();
            listener.onExited();
        } else if (mHeartEmptyDialog != null && mHeartEmptyDialog.isShowing()) {
            mHeartEmptyDialog.dismiss();
            exitNow();
            listener.onExited();
        } else {
            if (mExitDialog == null) {
                initExitDialog(listener);
            }
            if (!mExitDialog.isShowing()) {
                mExitDialog.show();
            }
        }
    }

    public void exitNow() {
        //触发surfaceDestroyed
        setVisibility(GONE);
        release();
    }

    private void initExitDialog(OnExitedListener listener) {
        OnClickListener onClickListener = v -> {
            switch (v.getId()) {
                case R.id.continue_game_btn:
                    mExitDialog.dismiss();
                    break;
                case R.id.back_to_menu_btn:
                    mExitDialog.dismiss();
                    exitNow();
                    listener.onExited();
                    break;
                default:
                    break;
            }
        };
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exit_view, null, false);
        dialogView.findViewById(R.id.continue_game_btn).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.back_to_menu_btn).setOnClickListener(onClickListener);
        mExitDialog = new AlertDialog.Builder(getContext(), R.style.DialogTheme).setView(dialogView).create();
    }

    public void release() {
        if (mPiggies != null) {
            for (Pig pig : mPiggies) {
                if (pig != null) {
//                    pig.release();
                }
            }
            mPiggies = null;
        }
        if (mComputePathTasks != null) {
            for (Future task : mComputePathTasks) {
                if (task != null) {
                    task.cancel(true);
                }
            }
            mComputePathTasks = null;
        }
        if (mCarBody != null) {
            mCarBody.release();
            mCarBody = null;
        }
        if (mCarHead != null) {
            mCarHead.release();
            mCarHead = null;
        }
        if (mFrameBackgroundBitmap != null) {
            if (!mFrameBackgroundBitmap.isRecycled()) {
                mFrameBackgroundBitmap.recycle();
            }
            mFrameBackgroundBitmap = null;
        }
        mCaughtPiggies = null;
        mCaughtPiggiesPosition = null;
        mPaint = null;
        mCarriageIsOccupied = null;
        mFrameBackground = null;
//        if (mPropOffsetHelper != null) {
//            mPropOffsetHelper.release();
//            mPropOffsetHelper = null;
//        }
        mSurfaceHolder = null;
        mItems = null;
        mItemStatus = null;
//        mDraggingProps = null;
        mDraggingPiggies = null;
        mDraggingPropIds = null;
        mDraggingPiggyIds = null;
        mPiggiesOccupiedPosition = null;
        mMissionDialog = null;
        mGameResultDialog = null;
        mExitDialog = null;
    }
}
