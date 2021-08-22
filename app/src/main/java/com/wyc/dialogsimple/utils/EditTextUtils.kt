package com.wyc.dialogsimple.utils

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


/**
 *作者： wyc
 * <p>
 * 创建时间： 2021/8/10 17:51
 * <p>
 * 文件名字： com.wyc.dialogsimple.utils
 * <p>
 * 类的介绍：
 */
object EditTextUtils {

    @JvmStatic
    fun showSoftInputFromWindow(context: Context, editText: EditText?) {
        if (editText == null) {
            return
        }
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.showSoftInput(editText, 0)
    }

    @JvmStatic
    fun hideKeyboard(context: Context, editText: EditText?) {
        if (editText == null) {
            return
        }
        editText.clearFocus()
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}