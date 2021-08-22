package com.wyc.dialog.listener;

import android.view.KeyEvent;
import android.view.View;

import com.wyc.dialog.base.BaseDialog;

/**
 * 作者： wyc
 * <p>
 * 创建时间： 2020/4/15 16:38
 * <p>
 * 文件名字： com.wyc.dialog.listener
 * <p>
 * 类的介绍：
 */
public interface ListenerUtils {

    interface OnCancelListener {

        /**
         * Dialog 取消了
         */
        void onCancel(BaseDialog dialog);
    }

    interface OnClickListener {

        void onClick(BaseDialog dialog, View view);
    }

    interface OnDismissListener {
        /**
         * Dialog 销毁了
         */
        void onDismiss(BaseDialog dialog);
    }

    interface OnKeyListener {
        /**
         * 触发了按键
         */
        boolean onKey(BaseDialog dialog, KeyEvent event);
    }

    interface OnShowListener {
        /**
         * Dialog 显示了
         */
        void onShow(BaseDialog dialog);
    }
}
