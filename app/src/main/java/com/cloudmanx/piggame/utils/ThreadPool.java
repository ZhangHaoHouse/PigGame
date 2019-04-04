package com.cloudmanx.piggame.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2019/4/4 下午5:40
 */
public class ThreadPool {
    private volatile static ThreadPool mInstance;
    private ExecutorService mExecutorService;
    private ThreadPool(){
        mExecutorService = Executors.newCachedThreadPool();
    }
    public static ThreadPool getInstance(){
        if (mInstance == null){
            synchronized(ThreadPool.class){
                if (mInstance == null){
                    mInstance = new ThreadPool();
                }
            }
        }
        return mInstance;
    }

    public static void shutdown(){
        if (mInstance != null){
            mInstance.mExecutorService.shutdownNow();
            mInstance = null;
        }
    }

    public Future<?> excute(Runnable command){
        return mExecutorService.submit(command);
    }

    @NonNull
    @Override
    public String toString() {
        return mExecutorService.toString();
    }
}
