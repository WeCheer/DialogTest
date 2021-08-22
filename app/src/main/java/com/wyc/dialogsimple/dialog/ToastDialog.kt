package com.wyc.dialogsimple.dialog

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wyc.dialog.base.BaseAnimStyle
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialog.listener.ListenerUtils
import com.wyc.dialogsimple.R

/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/4/9 14:43
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：
 */

class ToastDialog(activity: FragmentActivity) : BaseDialogFragment.Builder(activity), Runnable,
    ListenerUtils.OnShowListener {

    private var mMessageView: TextView
    private var mIconView: ImageView
    private var mContainer: CardView

    private var mType: Type? = null
    private var mDuration = 2000L

    init {
        setContentView(R.layout.dialog_toast)
        setAnimStyle(BaseAnimStyle.TOAST)
        setBackgroundDimEnabled(false)
        setCancelable(false)

        mMessageView = findViewById(R.id.tv_toast_message)
        mIconView = findViewById(R.id.iv_toast_icon)
        mContainer = findViewById(R.id.container_toast)

        addOnShowListener(this)
    }

    fun setType(type: Type): ToastDialog {
        mType = type
        when (type) {
            Type.FINISH -> {
                mIconView.setImageResource(R.mipmap.ic_dialog_finish)
                mContainer.setCardBackgroundColor(Color.GREEN)
            }
            Type.ERROR -> {
                mIconView.setImageResource(R.mipmap.ic_dialog_error)
                mContainer.setCardBackgroundColor(Color.RED)
            }
            Type.WARN -> {
                mIconView.setImageResource(R.mipmap.ic_dialog_warning)
                mContainer.setCardBackgroundColor(Color.YELLOW)
            }
            Type.NORMAL -> {
                mIconView.setImageResource(R.mipmap.ic_dialog_warning)
                mContainer.setCardBackgroundColor(Color.parseColor("#DC000000"))
            }
        }
        return this
    }

    fun setShowMessage(isShow: Boolean) {
        mMessageView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun setIcon(@DrawableRes resId: Int): ToastDialog {
        mIconView.setImageResource(resId)
        return this
    }

    fun setDialogBackground(@ColorInt color: Int): ToastDialog {
        mContainer.setCardBackgroundColor(color)
        return this
    }

    fun setDuration(duration: Long): ToastDialog {
        mDuration = duration
        return this
    }

    fun setMessage(@StringRes id: Int): ToastDialog {
        return setMessage(getString(id))
    }

    fun setMessage(text: String): ToastDialog {
        mMessageView.text = text
        return this
    }

    override fun create(): BaseDialog {
        //如果显示的类型为空就抛出异常
        if (mType == null) {
            throw IllegalArgumentException("The display type must be specified")
        }
        return super.create()
    }

    override fun run() {
        if (dialogFragment != null && dialogFragment!!.isAdded
            && dialog != null && dialog!!.isShowing
        ) {
            dismiss()
        }
    }

    override fun onShow(dialog: BaseDialog) {
        //延迟自动关闭
        postDelayed(this, mDuration)
    }

    /**
     * 显示的类型
     */
    enum class Type {
        // 完成，错误，警告
        FINISH,
        ERROR,
        WARN,
        NORMAL
    }
}