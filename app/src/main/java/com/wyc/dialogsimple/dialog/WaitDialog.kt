package com.wyc.dialogsimple.dialog

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import android.view.View
import android.widget.TextView
import com.wyc.dialog.base.BaseAnimStyle
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.R

/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/4/9 20:14
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：
 */
class WaitDialog(activity: FragmentActivity) : BaseDialogFragment.Builder(activity) {
    private var mMessageView: TextView

    init {
        setContentView(R.layout.dialog_wait)
        setAnimStyle(BaseAnimStyle.TOAST)
        setBackgroundDimEnabled(false)
        setCancelable(false)

        mMessageView = findViewById(R.id.tv_wait_message)
    }

    fun setMessage(@StringRes id: Int): WaitDialog {
        return setMessage(getString(id))
    }

    fun setMessage(text: CharSequence?): WaitDialog {
        mMessageView.text = text
        mMessageView.visibility = if (text == null) View.GONE else View.VISIBLE
        return this
    }
}