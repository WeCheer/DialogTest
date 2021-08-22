package com.wyc.dialogsimple.dialog

import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.R
import com.wyc.dialogsimple.base.BaseViewHolder
import com.wyc.dialogsimple.manager.PickerLayoutManager
import java.util.*
import kotlin.collections.ArrayList


/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/5/7 17:26
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：
 */

class TimeDialog(activity: FragmentActivity) : BaseDialogFragment.Builder(activity), Runnable {

    private var mHourView: RecyclerView
    private var mMinuteView: RecyclerView
    private var mSecondView: RecyclerView

    private var mHourManager: PickerLayoutManager
    private var mMinuteManager: PickerLayoutManager
    private var mSecondManager: PickerLayoutManager

    private var mHourAdapter: PickerAdapter
    private var mMinuteAdapter: PickerAdapter
    private var mSecondAdapter: PickerAdapter

    private var mTvTimeCancel: TextView
    private var mTvTimeTitle: TextView
    private var mTvTimeConfirm: TextView

    private var mAutoDismiss = true

    init {
        setContentView(R.layout.dialog_time)

        mHourView = findViewById(R.id.rvTimeHour)
        mMinuteView = findViewById(R.id.rvTimeMinute)
        mSecondView = findViewById(R.id.rvTimeSecond)

        mTvTimeCancel = findViewById(R.id.tvTimeCancel)
        mTvTimeConfirm = findViewById(R.id.tvTimeConfirm)
        mTvTimeTitle = findViewById(R.id.tvTimeTitle)

        mHourAdapter = PickerAdapter(mutableListOf())
        mMinuteAdapter = PickerAdapter(mutableListOf())
        mSecondAdapter = PickerAdapter(mutableListOf())

        setCancel("取消", null)

        // 生产小时
        val hourData: ArrayList<String> = ArrayList(24)
        for (i in 0..23) {
            hourData.add((if (i < 10) "0" else "") + i + " 时")
        }

        // 生产分钟
        val minuteData: ArrayList<String> = ArrayList(60)
        for (i in 0..59) {
            minuteData.add((if (i < 10) "0" else "") + i + " 分")
        }

        // 生产秒钟
        val secondData: ArrayList<String> = ArrayList(60)
        for (i in 0..59) {
            secondData.add((if (i < 10) "0" else "") + i + " 秒")
        }

        mHourAdapter.setNewData(hourData)
        mMinuteAdapter.setNewData(minuteData)
        mSecondAdapter.setNewData(secondData)

        mHourManager = PickerLayoutManager.Builder(activity).build()
        mMinuteManager = PickerLayoutManager.Builder(activity).build()
        mSecondManager = PickerLayoutManager.Builder(activity).build()

        mHourView.layoutManager = mHourManager
        mMinuteView.layoutManager = mMinuteManager
        mSecondView.layoutManager = mSecondManager

        mHourView.adapter = mHourAdapter
        mMinuteView.adapter = mMinuteAdapter
        mSecondView.adapter = mSecondAdapter

        val calendar: Calendar = Calendar.getInstance()
        setHour(calendar.get(Calendar.HOUR_OF_DAY))
        setMinute(calendar.get(Calendar.MINUTE))
        setSecond(calendar.get(Calendar.SECOND))

        postDelayed(this, 1000)
    }

    fun setAutoDismiss(dismiss: Boolean): TimeDialog {
        mAutoDismiss = dismiss
        return this
    }

    fun setTitle(@StringRes id: Int): TimeDialog {
        return setTitle(getString(id))
    }

    fun setTitle(text: CharSequence): TimeDialog {
        mTvTimeTitle.text = text
        return this
    }

    fun setCancel(@StringRes id: Int, listener: OnCancelListener?): TimeDialog {
        return setCancel(getString(id), listener)
    }

    fun setCancel(text: CharSequence, listener: OnCancelListener?): TimeDialog {
        mTvTimeCancel.text = text
        mTvTimeCancel.setOnClickListener {
            listener?.onCancel(dialog)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun setConfirm(@StringRes id: Int, listener: OnSelectedListener?): TimeDialog {
        return setConfirm(getString(id), listener)
    }

    fun setConfirm(text: CharSequence, listener: OnSelectedListener?): TimeDialog {
        mTvTimeConfirm.text = text
        mTvTimeConfirm.setOnClickListener {
            listener?.onSelected(dialog, mHourManager.getPickedPosition(), mMinuteManager.getPickedPosition(), mSecondManager.getPickedPosition());
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    /**
     * 不选择秒数
     */
    fun setIgnoreSecond(): TimeDialog {
        mSecondView.visibility = View.GONE
        return this
    }

    fun setTime(time: String): TimeDialog { // 102030
        if (time.matches("\\d{6}".toRegex())) {
            setHour(time.substring(0, 2))
            setMinute(time.substring(2, 4))
            setSecond(time.substring(4, 6))
            // 10:20:30
        } else if (time.matches("\\d{2}:\\d{2}:\\d{2}".toRegex())) {
            setHour(time.substring(0, 2))
            setMinute(time.substring(3, 5))
            setSecond(time.substring(6, 8))
        }
        return this
    }

    fun setHour(hour: String): TimeDialog {
        return setHour(Integer.valueOf(hour))
    }

    fun setHour(hour: Int): TimeDialog {
        var index = hour
        if (index < 0 || hour == 24) {
            index = 0
        } else if (index > mHourAdapter.itemCount - 1) {
            index = mHourAdapter.itemCount - 1
        }
        mHourView.scrollToPosition(index)
        return this
    }

    fun setMinute(minute: String): TimeDialog {
        return setMinute(Integer.valueOf(minute))
    }

    fun setMinute(minute: Int): TimeDialog {
        var index = minute
        if (index < 0) {
            index = 0
        } else if (index > mMinuteAdapter.itemCount - 1) {
            index = mMinuteAdapter.itemCount - 1
        }
        mMinuteView.scrollToPosition(index)
        return this
    }

    fun setSecond(second: String): TimeDialog {
        return setSecond(Integer.valueOf(second))
    }

    fun setSecond(second: Int): TimeDialog {
        var index = second
        if (index < 0) {
            index = 0
        } else if (index > mSecondAdapter.itemCount - 1) {
            index = mSecondAdapter.itemCount - 1
        }
        mSecondView.scrollToPosition(index)
        return this
    }

    override fun run() {
        if (mHourView.scrollState == RecyclerView.SCROLL_STATE_IDLE
            && mMinuteView.scrollState == RecyclerView.SCROLL_STATE_IDLE
            && mSecondView.scrollState == RecyclerView.SCROLL_STATE_IDLE
        ) {
            var calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = mHourManager.getPickedPosition()
            calendar[Calendar.MINUTE] = mMinuteManager.getPickedPosition()
            calendar[Calendar.SECOND] = mSecondManager.getPickedPosition()
            if (System.currentTimeMillis() - calendar.timeInMillis < 3000) {
                calendar = Calendar.getInstance()
                setHour(calendar[Calendar.HOUR_OF_DAY])
                setMinute(calendar[Calendar.MINUTE])
                setSecond(calendar[Calendar.SECOND])
                postDelayed(this, 1000)
            }
        }
    }

    private class PickerAdapter(private var mDataList: MutableList<String>) : RecyclerView.Adapter<BaseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_picker, parent, false))
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.setText(R.id.tv_picker_name, mDataList[position])
        }

        fun setNewData(dataList: MutableList<String>) {
            this.mDataList = dataList
            notifyDataSetChanged()
        }
    }

    interface OnSelectedListener {
        /**
         * 选择完时间后回调
         *
         * @param hour              时钟
         * @param minute            分钟
         * @param second            秒钟
         */
        fun onSelected(dialog: BaseDialog, hour: Int, minute: Int, second: Int)

    }

    interface OnCancelListener {
        /**
         * 点击取消时回调
         */
        fun onCancel(dialog: BaseDialog)
    }
}