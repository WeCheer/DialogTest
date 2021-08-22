package com.wyc.dialogsimple.dialog

import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat.getSystemService
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.wyc.dialog.base.BaseAnimStyle
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialog.listener.ListenerUtils
import com.wyc.dialogsimple.R
import com.wyc.dialogsimple.utils.EditTextUtils
import com.wyc.dialogsimple.utils.ScreenUtils

/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/4/9 17:02
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：输入Dialog
 */

class InputDialog(private val mActivity: FragmentActivity) : BaseDialogFragment.Builder(mActivity),
    ListenerUtils.OnShowListener, ListenerUtils.OnDismissListener {
    private var mAutoDismiss = true
    private var mTitleView: TextView
    private var mInputView: EditText
    private var mCancelView: TextView
    private var mLineView: View
    private var mConfirmView: TextView

    init {
        setContentView(R.layout.dialog_input)
        setAnimStyle(BaseAnimStyle.IOS)
        setWidth((ScreenUtils.getScreenWidth() * 0.85).toInt())
        mTitleView = findViewById(R.id.tv_input_title)
        mInputView = findViewById(R.id.tv_input_message)
        mCancelView = findViewById(R.id.tv_input_cancel)
        mLineView = findViewById(R.id.v_input_line)
        mConfirmView = findViewById(R.id.tv_input_confirm)
        addOnShowListener(this)
        addOnDismissListener(this)
        setCancel("取消", null)
    }

    fun setTitle(@StringRes id: Int): InputDialog {
        return setTitle(getString(id))
    }

    fun setTitle(text: CharSequence?): InputDialog {
        mTitleView.visibility = View.VISIBLE
        mTitleView.text = text
        return this
    }

    fun setHint(@StringRes id: Int): InputDialog {
        return setHint(getString(id))
    }

    fun setHint(text: CharSequence?): InputDialog {
        mInputView.hint = text
        return this
    }

    fun setContent(@StringRes id: Int): InputDialog {
        return setContent(getString(id))
    }

    fun setContent(text: CharSequence?): InputDialog {
        mInputView.setText(text)
        val index = mInputView.text.toString().length
        if (index > 0) {
            mInputView.requestFocus()
            mInputView.setSelection(index)
        }
        return this
    }

    fun setConfirm(@StringRes id: Int, listener: OnConfirmListener?): InputDialog {
        return setConfirm(getString(id), listener)
    }

    fun setConfirm(text: CharSequence?, listener: OnConfirmListener?): InputDialog {
        mConfirmView.text = text
        mConfirmView.setOnClickListener {
            val str = mInputView.text.toString().trim()
            if (TextUtils.isEmpty(str)) {
                Toast.makeText(mActivity, "编辑框不能为空", Toast.LENGTH_SHORT).show()
            } else {
                listener?.onConfirm(dialog, str)
                if (mAutoDismiss) {
                    dismiss()
                }
            }
        }
        return this
    }

    fun setCancel(@StringRes id: Int, listener: OnCancelListener?): InputDialog {
        return setCancel(getString(id), listener)
    }

    fun setCancel(text: CharSequence?, listener: OnCancelListener?): InputDialog {
        mCancelView.text = text
        mLineView.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
        mCancelView.setOnClickListener {
            listener?.onCancel(dialog)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun setAutoDismiss(dismiss: Boolean): InputDialog {
        mAutoDismiss = dismiss
        return this
    }

    fun setAnim(@StyleRes id: Int): InputDialog {
        setAnimStyle(id)
        return this
    }

    override fun onShow(dialog: BaseDialog) {
        postDelayed({
            EditTextUtils.showSoftInputFromWindow(activity, mInputView)
        }, 500)
    }

    override fun onDismiss(dialog: BaseDialog) {
        EditTextUtils.hideKeyboard(activity, mInputView)
    }


    interface OnConfirmListener {
        /**
         * 点击确定时回调
         */
        fun onConfirm(dialog: BaseDialog, content: String)

    }

    interface OnCancelListener {
        /**
         * 点击取消时回调
         */
        fun onCancel(dialog: BaseDialog)
    }
}
