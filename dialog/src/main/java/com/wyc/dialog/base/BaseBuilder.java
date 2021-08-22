package com.wyc.dialog.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import com.wyc.dialog.R;
import com.wyc.dialog.listener.ListenerUtils;
import com.wyc.dialog.wrapper.WrapperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： wyc
 * <p>
 * 创建时间： 2020/4/15 16:19
 * <p>
 * 文件名字： com.wyc.dialog
 * <p>
 * 类的介绍：
 */
public class BaseBuilder {

    /**
     * Context 对象
     */
    private Context mContext;
    /**
     * Dialog 对象
     */
    private BaseDialog mDialog;
    /**
     * Dialog 布局
     */
    private View mContentView;

    /**
     * 主题
     */
    private int mThemeId = R.style.BaseDialogStyle;
    /**
     * 动画
     */
    private int mAnimations = BaseAnimStyle.NO_ANIM;
    /**
     * 位置
     */
    private int mGravity = Gravity.NO_GRAVITY;
    /**
     * 宽度和高度
     */
    private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    /**
     * 背景遮盖层开关
     */
    private boolean mBackgroundDimEnabled = true;
    /**
     * 背景遮盖层透明度
     */
    private float mBackgroundDimAmount = 0.5f;
    /**
     * 是否能够被取消
     */
    private boolean mCancelable = true;
    /**
     * 点击空白是否能够取消  前提是这个对话框可以被取消
     */
    private boolean mCanceledOnTouchOutside = true;

    /**
     * Dialog Show 监听
     */
    private List<ListenerUtils.OnShowListener> mOnShowListeners;
    /**
     * Dialog Cancel 监听
     */
    private List<ListenerUtils.OnCancelListener> mOnCancelListeners;
    /**
     * Dialog Dismiss 监听
     */
    private List<ListenerUtils.OnDismissListener> mOnDismissListeners;
    /**
     * Dialog Key 监听
     */
    private ListenerUtils.OnKeyListener mOnKeyListener;

    /**
     * 一些 View 属性设置存放集合
     */
    private SparseArray<CharSequence> mTextArray;
    private SparseIntArray mVisibilityArray;
    private SparseArray<Drawable> mBackgroundArray;
    private SparseArray<Drawable> mImageArray;
    private SparseArray<ListenerUtils.OnClickListener> mClickArray;

    public BaseBuilder(Context context) {
        mContext = context;
    }

    /**
     * 设置主题 id
     */
    public BaseBuilder setThemeStyle(@StyleRes int id) {
        if (isCreated()) {
            // Dialog 创建之后不能再设置主题 id
            throw new IllegalStateException("are you ok?");
        }
        mThemeId = id;
        return this;
    }

    /**
     * 设置布局
     */
    public BaseBuilder setContentView(@LayoutRes int id) {
        // 这里解释一下，为什么要传 new FrameLayout，因为如果不传的话，XML 的根布局获取到的 LayoutParams 对象会为空，也就会导致宽高解析不出来
        return setContentView(LayoutInflater.from(mContext).inflate(id, new FrameLayout(mContext), false));
    }

    public BaseBuilder setContentView(View view) {
        mContentView = view;

        if (isCreated()) {
            mDialog.setContentView(view);
        } else {
            if (mContentView != null) {
                ViewGroup.LayoutParams params = mContentView.getLayoutParams();
                if (params != null && mWidth == ViewGroup.LayoutParams.WRAP_CONTENT
                        && mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
                    setWidth(params.width);
                    setHeight(params.height);
                }

                // 如果当前没有设置重心，就自动获取布局重心
                if (mGravity == Gravity.NO_GRAVITY) {
                    if (params instanceof FrameLayout.LayoutParams) {
                        setGravity(((FrameLayout.LayoutParams) params).gravity);
                    } else if (params instanceof LinearLayout.LayoutParams) {
                        setGravity(((LinearLayout.LayoutParams) params).gravity);
                    } else {
                        // 默认重心是居中
                        setGravity(Gravity.CENTER);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 设置重心位置
     */
    public BaseBuilder setGravity(int gravity) {
        // 适配 Android 4.2 新特性，布局反方向（开发者选项 - 强制使用从右到左的布局方向）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            gravity = Gravity.getAbsoluteGravity(gravity, getResources().getConfiguration().getLayoutDirection());
        }
        mGravity = gravity;
        if (isCreated()) {
            mDialog.setGravity(gravity);
        }
        return this;
    }

    /**
     * 设置宽度
     */
    public BaseBuilder setWidth(int width) {
        mWidth = width;
        if (isCreated()) {
            mDialog.setWidth(width);
        } else {
            ViewGroup.LayoutParams params = mContentView != null ? mContentView.getLayoutParams() : null;
            if (params != null) {
                params.width = width;
                mContentView.setLayoutParams(params);
            }
        }
        return this;
    }

    /**
     * 设置高度
     */
    public BaseBuilder setHeight(int height) {
        mHeight = height;
        if (isCreated()) {
            mDialog.setHeight(height);
        } else {
            // 这里解释一下为什么要重新设置 LayoutParams
            // 因为如果不这样设置的话，第一次显示的时候会按照 Dialog 宽高显示
            // 但是 Layout 内容变更之后就不会按照之前的设置宽高来显示
            // 所以这里我们需要对 View 的 LayoutParams 也进行设置
            ViewGroup.LayoutParams params = mContentView != null ? mContentView.getLayoutParams() : null;
            if (params != null) {
                params.height = height;
                mContentView.setLayoutParams(params);
            }
        }
        return this;
    }

    /**
     * 是否可以取消
     */
    public BaseBuilder setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        if (isCreated()) {
            mDialog.setCancelable(cancelable);
        }
        return this;
    }

    /**
     * 是否可以通过点击空白区域取消
     */
    public BaseBuilder setCanceledOnTouchOutside(boolean cancel) {
        mCanceledOnTouchOutside = cancel;
        if (isCreated() && mCancelable) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    /**
     * 设置动画，已经封装好几种样式，具体可见{@link BaseAnimStyle}类
     */
    public BaseBuilder setAnimStyle(@StyleRes int id) {
        mAnimations = id;
        if (isCreated()) {
            mDialog.setWindowAnimations(id);
        }
        return this;
    }

    /**
     * 设置背景遮盖层开关
     */
    public BaseBuilder setBackgroundDimEnabled(boolean enabled) {
        mBackgroundDimEnabled = enabled;
        if (isCreated()) {
            mDialog.setBackgroundDimEnabled(enabled);
        }
        return this;
    }

    /**
     * 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态）
     */
    public BaseBuilder setBackgroundDimAmount(float dimAmount) {
        mBackgroundDimAmount = dimAmount;
        if (isCreated()) {
            mDialog.setBackgroundDimAmount(dimAmount);
        }
        return this;
    }

    /**
     * 添加显示监听
     */
    public BaseBuilder addOnShowListener(@NonNull ListenerUtils.OnShowListener listener) {
        if (isCreated()) {
            mDialog.addOnShowListener(listener);
        } else {
            if (mOnShowListeners == null) {
                mOnShowListeners = new ArrayList<>();
            }
            mOnShowListeners.add(listener);
        }
        return this;
    }

    /**
     * 添加取消监听
     */
    public BaseBuilder addOnCancelListener(@NonNull ListenerUtils.OnCancelListener listener) {
        if (isCreated()) {
            mDialog.addOnCancelListener(listener);
        } else {
            if (mOnCancelListeners == null) {
                mOnCancelListeners = new ArrayList<>();
            }
            mOnCancelListeners.add(listener);
        }
        return this;
    }

    /**
     * 添加销毁监听
     */
    public BaseBuilder addOnDismissListener(@NonNull ListenerUtils.OnDismissListener listener) {
        if (isCreated()) {
            mDialog.addOnDismissListener(listener);
        } else {
            if (mOnDismissListeners == null) {
                mOnDismissListeners = new ArrayList<>();
            }
            mOnDismissListeners.add(listener);
        }
        return this;
    }

    /**
     * 设置按键监听
     */
    public BaseBuilder setOnKeyListener(@NonNull ListenerUtils.OnKeyListener listener) {
        if (isCreated()) {
            mDialog.setOnKeyListener(listener);
        } else {
            mOnKeyListener = listener;
        }
        return this;
    }

    /**
     * 设置文本
     */
    public BaseBuilder setText(@IdRes int viewId, @StringRes int stringId) {
        return setText(viewId, getString(stringId));
    }

    public BaseBuilder setText(@IdRes int id, CharSequence text) {
        if (isCreated()) {
            TextView textView = mDialog.findViewById(id);
            if (textView != null) {
                textView.setText(text);
            }
        } else {
            if (mTextArray == null) {
                mTextArray = new SparseArray<>();
            }
            mTextArray.put(id, text);
        }
        return this;
    }

    /**
     * 设置可见状态
     */
    public BaseBuilder setVisibility(@IdRes int id, int visibility) {
        if (isCreated()) {
            View view = mDialog.findViewById(id);
            if (view != null) {
                view.setVisibility(visibility);
            }
        } else {
            if (mVisibilityArray == null) {
                mVisibilityArray = new SparseIntArray();
            }
            mVisibilityArray.put(id, visibility);
        }
        return this;
    }

    /**
     * 设置背景
     */
    public BaseBuilder setBackground(@IdRes int viewId, @DrawableRes int drawableId) {
        return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
    }

    public BaseBuilder setBackground(@IdRes int id, Drawable drawable) {
        if (isCreated()) {
            View view = mDialog.findViewById(id);
            if (view != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(drawable);
                } else {
                    view.setBackgroundDrawable(drawable);
                }
            }
        } else {
            if (mBackgroundArray == null) {
                mBackgroundArray = new SparseArray<>();
            }
            mBackgroundArray.put(id, drawable);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public BaseBuilder setImageDrawable(@IdRes int viewId, @DrawableRes int drawableId) {
        return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
    }

    public BaseBuilder setImageDrawable(@IdRes int id, Drawable drawable) {
        if (isCreated()) {
            ImageView imageView = mDialog.findViewById(id);
            if (imageView != null) {
                imageView.setImageDrawable(drawable);
            }
        } else {
            if (mImageArray == null) {
                mImageArray = new SparseArray<>();
            }
            mImageArray.put(id, drawable);
        }
        return this;
    }

    /**
     * 设置点击事件
     */
    public BaseBuilder setOnClickListener(@IdRes int id, @NonNull ListenerUtils.OnClickListener listener) {
        if (isCreated()) {
            View view = mDialog.findViewById(id);
            if (view != null) {
                view.setOnClickListener(new WrapperUtils.ViewClickWrapper(mDialog, listener));
            }
        } else {
            if (mClickArray == null) {
                mClickArray = new SparseArray<>();
            }
            mClickArray.put(id, listener);
        }
        return this;
    }

    /**
     * 创建
     */
    @SuppressLint("RtlHardcoded")
    public BaseDialog create() {

        // 判断布局是否为空
        if (mContentView == null) {
            throw new IllegalArgumentException("Dialog layout cannot be empty");
        }

        // 如果当前没有设置重心，就设置一个默认的重心
        if (mGravity == Gravity.NO_GRAVITY) {
            mGravity = Gravity.CENTER;
        }

        // 如果当前没有设置动画效果，就设置一个默认的动画效果
//        if (mAnimations == BaseAnimStyle.NO_ANIM) {
//            switch (mGravity) {
//                case Gravity.TOP:
//                    mAnimations = BaseAnimStyle.TOP;
//                    break;
//                case Gravity.BOTTOM:
//                    mAnimations = BaseAnimStyle.BOTTOM;
//                    break;
//                case Gravity.LEFT:
//                    mAnimations = BaseAnimStyle.LEFT;
//                    break;
//                case Gravity.RIGHT:
//                    mAnimations = BaseAnimStyle.RIGHT;
//                    break;
//                default:
//                    mAnimations = BaseAnimStyle.DEFAULT;
//                    break;
//            }
//        }

        mDialog = createDialog(mContext, mThemeId);

        mDialog.setContentView(mContentView);
        mDialog.setCancelable(mCancelable);
        if (mCancelable) {
            mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        }

        // 设置参数
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            params.gravity = mGravity;
            params.windowAnimations = mAnimations;
            window.setAttributes(params);
            if (mBackgroundDimEnabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setDimAmount(mBackgroundDimAmount);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }

        if (mOnShowListeners != null) {
            mDialog.setOnShowListeners(mOnShowListeners);
        }

        if (mOnCancelListeners != null) {
            mDialog.setOnCancelListeners(mOnCancelListeners);
        }

        if (mOnDismissListeners != null) {
            mDialog.setOnDismissListeners(mOnDismissListeners);
        }

        if (mOnKeyListener != null) {
            mDialog.setOnKeyListener(mOnKeyListener);
        }

        // 设置文本
        for (int i = 0; mTextArray != null && i < mTextArray.size(); i++) {
            ((TextView) mContentView.findViewById(mTextArray.keyAt(i)))
                    .setText(mTextArray.valueAt(i));
        }

        // 设置可见状态
        for (int i = 0; mVisibilityArray != null && i < mVisibilityArray.size(); i++) {
            mContentView.findViewById(mVisibilityArray.keyAt(i))
                    .setVisibility(mVisibilityArray.valueAt(i));
        }

        // 设置背景
        for (int i = 0; mBackgroundArray != null && i < mBackgroundArray.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mContentView.findViewById(mBackgroundArray.keyAt(i))
                        .setBackground(mBackgroundArray.valueAt(i));
            } else {
                mContentView.findViewById(mBackgroundArray.keyAt(i))
                        .setBackgroundDrawable(mBackgroundArray.valueAt(i));
            }
        }

        // 设置图片
        for (int i = 0; mImageArray != null && i < mImageArray.size(); i++) {
            ((ImageView) mContentView.findViewById(mImageArray.keyAt(i)))
                    .setImageDrawable(mImageArray.valueAt(i));
        }

        // 设置点击事件
        for (int i = 0; mClickArray != null && i < mClickArray.size(); i++) {
            mContentView.findViewById(mClickArray.keyAt(i))
                    .setOnClickListener(new WrapperUtils.ViewClickWrapper(mDialog, mClickArray.valueAt(i)));
        }

        return mDialog;
    }

    /**
     * 显示
     */
    public BaseDialog show() {
        final BaseDialog dialog = create();
        dialog.show();
        return dialog;
    }

    /**
     * 当前 Dialog 是否创建了（仅供子类调用）
     */
    protected boolean isCreated() {
        return mDialog != null;
    }

    /**
     * 当前 Dialog 是否显示了（仅供子类调用）
     */
    protected boolean isShowing() {
        return isCreated() && mDialog.isShowing();
    }

    /**
     * 销毁当前 Dialog（仅供子类调用）
     */
    protected void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 创建 Dialog 对象（子类可以重写此方法来改变 Dialog 类型）
     */
    protected BaseDialog createDialog(Context context, @StyleRes int themeId) {
        return new BaseDialog(context, themeId);
    }

    /**
     * 延迟执行
     */
    protected final void post(Runnable r) {
        if (isShowing()) {
            mDialog.post(r);
        } else {
            addOnShowListener(new WrapperUtils.ShowPostWrapper(r));
        }
    }

    /**
     * 延迟一段时间执行
     */
    protected final void postDelayed(Runnable r, long delayMillis) {
        if (isShowing()) {
            mDialog.postDelayed(r, delayMillis);
        } else {
            addOnShowListener(new WrapperUtils.ShowPostDelayedWrapper(r, delayMillis));
        }
    }

    /**
     * 在指定的时间执行
     */
    protected final void postAtTime(Runnable r, long uptimeMillis) {
        if (isShowing()) {
            mDialog.postAtTime(r, uptimeMillis);
        } else {
            addOnShowListener(new WrapperUtils.ShowPostAtTimeWrapper(r, uptimeMillis));
        }
    }

    /**
     * 获取上下文对象（仅供子类调用）
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * 获取资源对象（仅供子类调用）
     */
    protected Resources getResources() {
        return mContext.getResources();
    }

    /**
     * 根据 id 获取一个文本（仅供子类调用）
     */
    protected String getString(@StringRes int id) {
        return mContext.getString(id);
    }

    /**
     * 根据 id 获取一个颜色（仅供子类调用）
     */
    protected int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    /**
     * 根据 id 获取一个 Drawable（仅供子类调用）
     */
    protected Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(mContext, id);
    }

    /**
     * 获取 Dialog 的根布局
     */
    protected View getContentView() {
        return mContentView;
    }

    /**
     * 根据 id 查找 View（仅供子类调用）
     */
    protected <V extends View> V findViewById(@IdRes int id) {
        if (mContentView == null) {
            // 没有 setContentView 就想 findViewById ?
            throw new IllegalStateException("are you ok?");
        }
        return mContentView.findViewById(id);
    }

    /**
     * 获取当前 Dialog 对象（仅供子类调用）
     */
    protected BaseDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取系统服务
     */
    protected <T> T getSystemService(@NonNull Class<T> serviceClass) {
        return ContextCompat.getSystemService(mContext, serviceClass);
    }
}
