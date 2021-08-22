package com.wyc.dialog.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;

import com.wyc.dialog.R;
import com.wyc.dialog.listener.ListenerUtils;
import com.wyc.dialog.wrapper.WrapperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： wyc
 * <p>
 * 创建时间： 2020/4/15 16:17
 * <p>
 * 文件名字： com.wyc.dialog
 * <p>
 * 类的介绍：
 */
public class BaseDialog extends AppCompatDialog implements DialogInterface.OnShowListener,
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private final Object mHandlerToken = hashCode();

    /**
     * Dialog 是否可以取消
     */
    private boolean mCancelable = true;

    private final WrapperUtils.ListenersWrapper<BaseDialog> mListeners = new WrapperUtils.ListenersWrapper<>(this);

    private List<ListenerUtils.OnShowListener> mShowListeners;
    private List<ListenerUtils.OnCancelListener> mCancelListeners;
    private List<ListenerUtils.OnDismissListener> mDismissListeners;

    public BaseDialog(Context context) {
        this(context, R.style.BaseDialogStyle);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(mCancelable = flag);
    }

    /**
     * 获取 Dialog 的根布局
     */
    public View getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 是否设置了取消（仅供子类调用）
     */
    protected boolean isCancelable() {
        return mCancelable;
    }

    /**
     * 获取当前设置重心
     */
    public int getGravity() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            return params.gravity;
        }
        return Gravity.NO_GRAVITY;
    }

    /**
     * 设置宽度
     */
    public void setWidth(int width) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            window.setAttributes(params);
        }
    }

    /**
     * 设置高度
     */
    public void setHeight(int height) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = height;
            window.setAttributes(params);
        }
    }

    /**
     * 设置 Dialog 重心
     */
    public void setGravity(int gravity) {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(gravity);
        }
    }

    /**
     * 设置 Dialog 的动画
     */
    public void setWindowAnimations(@StyleRes int id) {
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(id);
        }
    }

    /**
     * 设置背景遮盖层开关
     */
    public void setBackgroundDimEnabled(boolean enabled) {
        Window window = getWindow();
        if (window != null) {
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }
    }

    /**
     * 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态）
     */
    public void setBackgroundDimAmount(float dimAmount) {
        Window window = getWindow();
        if (window != null) {
            window.setDimAmount(dimAmount);
        }
    }

    /**
     * 延迟执行
     */
    public final boolean post(Runnable r) {
        return postDelayed(r, 0);
    }

    /**
     * 延迟一段时间执行
     */
    public final boolean postDelayed(Runnable r, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postAtTime(r, SystemClock.uptimeMillis() + delayMillis);
    }

    /**
     * 在指定的时间执行
     */
    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return HANDLER.postAtTime(r, mHandlerToken, uptimeMillis);
    }

    @Override
    public void hide() {
        // 如果当前有 View 获得焦点，就必须将这个对话框 dismiss 掉，否则 Dialog 无法正常显示
        // 复现步骤：创建一个带有 EditText 的对话框，并弹出输入法，点击返回到桌面，然后再回来程序界面
        View view = getCurrentFocus();
        if (view != null) {
            dismiss();
        } else {
            super.hide();
        }
    }

    /**
     * 设置一个显示监听器
     *
     * @param listener 显示监听器对象
     */
    @Deprecated
    @Override
    public final void setOnShowListener(@Nullable DialogInterface.OnShowListener listener) {
        if (listener == null) {
            return;
        }
        addOnShowListener(new WrapperUtils.ShowListenerWrapper(listener));
    }

    /**
     * 设置一个取消监听器
     *
     * @param listener 取消监听器对象
     */
    @Deprecated
    @Override
    public final void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        if (listener == null) {
            return;
        }
        addOnCancelListener(new WrapperUtils.CancelListenerWrapper(listener));
    }

    /**
     * 设置一个销毁监听器
     *
     * @param listener 销毁监听器对象
     */
    @Deprecated
    @Override
    public final void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        if (listener == null) {
            return;
        }
        addOnDismissListener(new WrapperUtils.DismissListenerWrapper(listener));
    }

    /**
     * 设置一个按键监听器
     *
     * @param listener 按键监听器对象
     * @deprecated 请使用 {@link #setOnKeyListener(BaseDialog.OnKeyListener)}
     */
    @Deprecated
    @Override
    public final void setOnKeyListener(@Nullable DialogInterface.OnKeyListener listener) {
        super.setOnKeyListener(listener);
    }

    public void setOnKeyListener(@Nullable ListenerUtils.OnKeyListener listener) {
        super.setOnKeyListener(new WrapperUtils.KeyListenerWrapper(listener));
    }

    /**
     * 添加一个显示监听器
     *
     * @param listener 监听器对象
     */
    public void addOnShowListener(@Nullable ListenerUtils.OnShowListener listener) {
        if (mShowListeners == null) {
            mShowListeners = new ArrayList<>();
            super.setOnShowListener(mListeners);
        }
        mShowListeners.add(listener);
    }

    /**
     * 添加一个取消监听器
     *
     * @param listener 监听器对象
     */
    public void addOnCancelListener(@Nullable ListenerUtils.OnCancelListener listener) {
        if (mCancelListeners == null) {
            mCancelListeners = new ArrayList<>();
            super.setOnCancelListener(mListeners);
        }
        mCancelListeners.add(listener);
    }

    /**
     * 添加一个销毁监听器
     *
     * @param listener 监听器对象
     */
    public void addOnDismissListener(@Nullable ListenerUtils.OnDismissListener listener) {
        if (mDismissListeners == null) {
            mDismissListeners = new ArrayList<>();
            super.setOnDismissListener(mListeners);
        }
        mDismissListeners.add(listener);
    }

    /**
     * 移除一个显示监听器
     *
     * @param listener 监听器对象
     */
    public void removeOnShowListener(@Nullable ListenerUtils.OnShowListener listener) {
        if (mShowListeners != null) {
            mShowListeners.remove(listener);
        }
    }

    /**
     * 移除一个取消监听器
     *
     * @param listener 监听器对象
     */
    public void removeOnCancelListener(@Nullable ListenerUtils.OnCancelListener listener) {
        if (mCancelListeners != null) {
            mCancelListeners.remove(listener);
        }
    }

    /**
     * 移除一个销毁监听器
     *
     * @param listener 监听器对象
     */
    public void removeOnDismissListener(@Nullable ListenerUtils.OnDismissListener listener) {
        if (mDismissListeners != null) {
            mDismissListeners.remove(listener);
        }
    }

    /**
     * 设置显示监听器集合
     */
    public void setOnShowListeners(@Nullable List<ListenerUtils.OnShowListener> listeners) {
        super.setOnShowListener(mListeners);
        mShowListeners = listeners;
    }

    /**
     * 设置取消监听器集合
     */
    public void setOnCancelListeners(@Nullable List<ListenerUtils.OnCancelListener> listeners) {
        super.setOnCancelListener(mListeners);
        mCancelListeners = listeners;
    }

    /**
     * 设置销毁监听器集合
     */
    public void setOnDismissListeners(@Nullable List<ListenerUtils.OnDismissListener> listeners) {
        super.setOnDismissListener(mListeners);
        mDismissListeners = listeners;
    }

    /**
     * {@link DialogInterface.OnShowListener}
     */
    @Override
    public void onShow(DialogInterface dialog) {
        if (mShowListeners != null) {
            for (ListenerUtils.OnShowListener listener : mShowListeners) {
                listener.onShow(this);
            }
        }
    }

    /**
     * {@link DialogInterface.OnCancelListener}
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCancelListeners != null) {
            for (ListenerUtils.OnCancelListener listener : mCancelListeners) {
                listener.onCancel(this);
            }
        }
    }

    /**
     * {@link DialogInterface.OnDismissListener}
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDismissListeners != null) {
            for (ListenerUtils.OnDismissListener listener : mDismissListeners) {
                listener.onDismiss(this);
            }
        }

        // 移除和这个 Dialog 相关的消息回调
        HANDLER.removeCallbacksAndMessages(mHandlerToken);
    }
}
