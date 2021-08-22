package com.wyc.dialogsimple.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/4/16 15:27
 * <p>
 * 文件名字：
 * <p>
 * 类的介绍：
 */
object RunnableUtils {

    private val mHandler = Handler(Looper.getMainLooper())
    private val mHandlerToken = hashCode()

    /**
     * 延迟执行
     */
    fun post(r: Runnable): Boolean {
        return postDelayed(r, 0)
    }

    /**
     * 延迟一段时间执行
     */
    fun postDelayed(r: Runnable, delayMillis: Long): Boolean {
        var temp = delayMillis
        if (temp < 0) {
            temp = 0
        }
        return postAtTime(r, SystemClock.uptimeMillis() + temp)
    }

    /**
     * 在指定的时间执行
     */
    fun postAtTime(r: Runnable, uptimeMillis: Long): Boolean {
        // 发送和这个 Activity 相关的消息回调
        return mHandler.postAtTime(r, mHandlerToken, uptimeMillis)
    }
}