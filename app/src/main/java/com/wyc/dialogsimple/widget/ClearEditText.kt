package com.wyc.dialogsimple.widget

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.wyc.dialogsimple.R


/**
 * 作者： wyc
 *
 * 创建时间： 2019/3/25 9:55
 *
 * 文件名字： com.wyc.helper
 *
 * 类的介绍：
 */
class ClearEditText
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle)
    : RegexEditText(context, attrs, defStyleAttr), View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private lateinit var mClearIcon: Drawable

    private var mOnTouchListener: OnTouchListener? = null
    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    init {
        initialize(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialize(context: Context) {
        val drawable = ContextCompat.getDrawable(context, R.mipmap.input_delete)
        val wrappedDrawable = DrawableCompat.wrap(drawable!!)
        mClearIcon = wrappedDrawable
        mClearIcon.setBounds(0, 0, mClearIcon.intrinsicWidth, mClearIcon.intrinsicHeight)
        setClearIconVisible(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        super.addTextChangedListener(this)
    }

    private fun setClearIconVisible(visible: Boolean) {
        if (mClearIcon.isVisible == visible) return

        mClearIcon.setVisible(visible, false)
        val compoundDrawables = compoundDrawables
        setCompoundDrawables(
            compoundDrawables[0],
            compoundDrawables[1],
            if (visible) mClearIcon else null,
            compoundDrawables[3]
        )
    }

    /**
     * [View.OnFocusChangeListener]
     */
    override fun setOnFocusChangeListener(listener: OnFocusChangeListener) {
        mOnFocusChangeListener = listener
    }

    /**
     * [View.OnTouchListener]
     */
    override fun setOnTouchListener(listener: OnTouchListener) {
        mOnTouchListener = listener
    }

    /**
     * [View.OnFocusChangeListener]
     */

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus && text != null) {
            setClearIconVisible(text!!.isNotEmpty())
        } else {
            setClearIconVisible(false)
        }
        mOnFocusChangeListener?.onFocusChange(view, hasFocus)
    }

    /**
     * [View.OnTouchListener]
     */

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val x = event.x.toInt()
        if (mClearIcon.isVisible && x > width - paddingRight - mClearIcon.intrinsicWidth) {
            if (event.action == MotionEvent.ACTION_UP) {
                setText("")
            }
            return true
        }
        return mOnTouchListener != null && mOnTouchListener!!.onTouch(view, event)
    }

    /**
     * [TextWatcher]
     */

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isFocused) {
            setClearIconVisible(s.isNotEmpty())
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {}


    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.paste) {
            //调用剪贴板，清除剪切板中的数据样式
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            //改变剪贴板中Content
            if (clipboardManager != null) {
                val clipData = clipboardManager.primaryClip
                clipData?.let {
                    if (it.itemCount > 0) {
                        setText(it.getItemAt(0).coerceToText(context).toString())
                        setSelection(text?.length ?: 0)
                    }
                }
            }
            return true
        } else {
            return super.onTextContextMenuItem(id)
        }
    }

}
