package com.wyc.dialogsimple.dialog

import android.app.Activity
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.R
import com.wyc.dialogsimple.utils.ScreenUtils

/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/4/9 16:09
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：消息Dialog
 */

class MessageDialog(activity: FragmentActivity) : BaseDialogFragment.Builder(activity) {

    private var mAutoDismiss = true

    private var mTitleView: TextView
    private var mMessageView: TextView
    private var mCancelView: TextView
    private var mLineView: View
    private var mConfirmView: TextView

    init {
        setContentView(R.layout.dialog_message)
        setWidth((ScreenUtils.getScreenWidth() * 0.75).toInt())
        mTitleView = findViewById(R.id.tv_message_title)
        mMessageView = findViewById(R.id.tv_message_message)

        mCancelView = findViewById(R.id.tv_message_cancel)
        mLineView = findViewById(R.id.v_message_line)
        mConfirmView = findViewById(R.id.tv_message_confirm)
        setCancel("取消", null)
    }

    fun setTitle(@StringRes id: Int): MessageDialog {
        return setTitle(getString(id))
    }

    fun setTitle(text: CharSequence): MessageDialog {
        mTitleView.text = text
        return this
    }

    fun setMessage(@StringRes id: Int): MessageDialog {
        return setMessage(getString(id))
    }

    fun setMessage(text: CharSequence): MessageDialog {
        mMessageView.text = text
        return this
    }

    fun setCancel(@StringRes id: Int, listener: OnCancelListener?): MessageDialog {
        return setCancel(getString(id), listener)
    }

    fun setCancel(text: CharSequence, listener: OnCancelListener?): MessageDialog {
        mCancelView.text = text
        mCancelView.setOnClickListener {
            listener?.onCancel(dialog)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun setConfirm(@StringRes id: Int, listener: OnConfirmListener?): MessageDialog {
        return setConfirm(getString(id), listener)
    }

    fun setConfirm(text: CharSequence, listener: OnConfirmListener?): MessageDialog {
        mConfirmView.visibility = View.VISIBLE
        mLineView.visibility = View.VISIBLE
        mConfirmView.text = text
        mConfirmView.setOnClickListener {
            listener?.onConfirm(dialog)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun setAutoDismiss(dismiss: Boolean): MessageDialog {
        mAutoDismiss = dismiss
        return this
    }

    override fun create(): BaseDialog {
        //如果内容为空就抛出异常
        if (TextUtils.isEmpty(mMessageView.text.toString().trim())) {
            throw IllegalArgumentException("Dialog message not null")
        }
        return super.create()
    }

    interface OnConfirmListener {

        fun onConfirm(dialog: BaseDialog)
    }

    interface OnCancelListener {

        fun onCancel(dialog: BaseDialog)
    }
}