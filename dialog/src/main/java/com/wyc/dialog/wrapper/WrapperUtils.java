package com.wyc.dialog.wrapper;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;

import com.wyc.dialog.base.BaseDialog;
import com.wyc.dialog.listener.ListenerUtils;

import java.lang.ref.WeakReference;

/**
 * 作者： wyc
 * <p>
 * 创建时间： 2020/4/15 16:39
 * <p>
 * 文件名字： com.wyc.dialog.wrapper
 * <p>
 * 类的介绍：
 */
public interface WrapperUtils {

    class ListenersWrapper<T extends DialogInterface.OnShowListener & DialogInterface.OnCancelListener & DialogInterface.OnDismissListener>
            extends WeakReference<T> implements DialogInterface.OnShowListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

        public ListenersWrapper(T referent) {
            super(referent);
        }

        @Override
        public void onShow(DialogInterface dialog) {
            if (get() != null) {
                get().onShow(dialog);
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (get() != null) {
                get().onCancel(dialog);
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (get() != null) {
                get().onDismiss(dialog);
            }
        }
    }

    /**
     * 显示监听包装类
     */
    class ShowListenerWrapper implements ListenerUtils.OnShowListener {

        private final DialogInterface.OnShowListener mListener;

        public ShowListenerWrapper(DialogInterface.OnShowListener listener) {
            mListener = listener;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            // 在横竖屏切换后监听对象会为空
            if (mListener != null) {
                mListener.onShow(dialog);
            }
        }
    }

    /**
     * 取消监听包装类
     */
    class CancelListenerWrapper implements ListenerUtils.OnCancelListener {

        private final DialogInterface.OnCancelListener mListener;

        public CancelListenerWrapper(DialogInterface.OnCancelListener listener) {
            mListener = listener;
        }

        @Override
        public void onCancel(BaseDialog dialog) {
            // 在横竖屏切换后监听对象会为空
            if (mListener != null) {
                mListener.onCancel(dialog);
            }
        }
    }

    /**
     * 销毁监听包装类
     */
    class DismissListenerWrapper implements ListenerUtils.OnDismissListener {

        private final DialogInterface.OnDismissListener mListener;

        public DismissListenerWrapper(DialogInterface.OnDismissListener listener) {
            mListener = listener;
        }

        @Override
        public void onDismiss(BaseDialog dialog) {
            // 在横竖屏切换后监听对象会为空
            if (mListener != null) {
                mListener.onDismiss(dialog);
            }
        }
    }

    class KeyListenerWrapper implements DialogInterface.OnKeyListener {

        private final ListenerUtils.OnKeyListener mListener;

        public KeyListenerWrapper(ListenerUtils.OnKeyListener listener) {
            mListener = listener;
        }

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            // 在横竖屏切换后监听对象会为空
            if (mListener != null && dialog instanceof BaseDialog) {
                return mListener.onKey((BaseDialog) dialog, event);
            }
            return false;
        }
    }

    class ShowPostAtTimeWrapper implements ListenerUtils.OnShowListener {

        private final Runnable mRunnable;
        private final long mUptimeMillis;

        public ShowPostAtTimeWrapper(Runnable r, long uptimeMillis) {
            mRunnable = r;
            mUptimeMillis = uptimeMillis;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.postAtTime(mRunnable, mUptimeMillis);
            }
        }
    }

    class ShowPostDelayedWrapper implements ListenerUtils.OnShowListener {

        private final Runnable mRunnable;
        private final long mDelayMillis;

        public ShowPostDelayedWrapper(Runnable r, long delayMillis) {
            mRunnable = r;
            mDelayMillis = delayMillis;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.postDelayed(mRunnable, mDelayMillis);
            }
        }
    }

    class ShowPostWrapper implements ListenerUtils.OnShowListener {

        private final Runnable mRunnable;

        public ShowPostWrapper(Runnable r) {
            mRunnable = r;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.post(mRunnable);
            }
        }
    }

    class ViewClickWrapper implements View.OnClickListener {

        private final BaseDialog mDialog;
        private final ListenerUtils.OnClickListener mListener;

        public ViewClickWrapper(BaseDialog dialog, ListenerUtils.OnClickListener listener) {
            mDialog = dialog;
            mListener = listener;
        }

        @Override
        public final void onClick(View v) {
            mListener.onClick(mDialog, v);
        }
    }
}
