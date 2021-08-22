package com.wyc.dialog.base;


import com.wyc.dialog.R;

/**
 * 作者： wyc
 * <p>
 * 创建时间： 2020/4/15 16:18
 * <p>
 * 文件名字： com.wyc.dialog
 * <p>
 * 类的介绍：
 */
public class BaseAnimStyle {

    /**
     * 没有动画效果
     */
    public static final int NO_ANIM = 0;

    /**
     * 默认动画效果
     */
    public static final int DEFAULT = R.style.ScaleAnimStyle;

    /**
     * 缩放动画
     */
    public static final int SCALE = R.style.ScaleAnimStyle;

    /**
     * IOS 动画
     */
    public static final int IOS = R.style.IOSAnimStyle;

    /**
     * 吐司动画
     */
    public static final int TOAST = android.R.style.Animation_Toast;

    /**
     * 顶部弹出动画
     */
    public static final int TOP = R.style.TopAnimStyle;

    /**
     * 底部弹出动画
     */
    public static final int BOTTOM = R.style.BottomAnimStyle;

    /**
     * 左边弹出动画
     */
    public static final int LEFT = R.style.LeftAnimStyle;

    /**
     * 右边弹出动画
     */
    public static final int RIGHT = R.style.RightAnimStyle;
}
