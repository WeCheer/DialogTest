package com.wyc.dialogsimple.dialog

import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wyc.dialog.base.BaseAnimStyle
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.R
import com.wyc.dialogsimple.base.BaseViewHolder
import com.wyc.dialogsimple.manager.PickerLayoutManager
import java.text.SimpleDateFormat
import java.util.*


/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/5/7 15:42
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：
 */

class DateDialog(activity: FragmentActivity) : BaseDialogFragment.Builder(activity), PickerLayoutManager.OnPickerListener {

    private val mStartYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR) - 100
    private val mEndYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR)

    private val mYearAdapter: PickerAdapter
    private val mMonthAdapter: PickerAdapter
    private val mDayAdapter: PickerAdapter

    private var mYearView: RecyclerView
    private var mMonthView: RecyclerView
    private var mDayView: RecyclerView

    private var mYearManager: PickerLayoutManager
    private var mMonthManager: PickerLayoutManager
    private var mDayManager: PickerLayoutManager

    private val mYearData = mutableListOf<String>()
    private val mMonthData = mutableListOf<String>()
    private val mDayData = mutableListOf<String>()

    private var mTvDateCancel: TextView
    private var mTvDateTitle: TextView
    private var mTvDateConfirm: TextView

    private var mAutoDismiss = true


    init {
        setContentView(R.layout.dialog_date)
        setAnimStyle(BaseAnimStyle.BOTTOM)

        mYearView = findViewById(R.id.rvDateYear)
        mMonthView = findViewById(R.id.rvDateMonth)
        mDayView = findViewById(R.id.rvDateDay)

        mTvDateCancel = findViewById(R.id.tvDateCancel)
        mTvDateConfirm = findViewById(R.id.tvDateConfirm)
        mTvDateTitle = findViewById(R.id.tvDateTitle)

        mYearAdapter = PickerAdapter(mutableListOf())
        mMonthAdapter = PickerAdapter(mutableListOf())
        mDayAdapter = PickerAdapter(mutableListOf())

        /**
         * 年
         */
        for (index in mStartYear..mEndYear) {
            mYearData.add("$index 年")
        }
        /**
         * 月
         */
        for (index in 1..12) {
            mMonthData.add("$index 月")
        }

        /**
         * 日
         */
        val calendar = Calendar.getInstance(Locale.CHINA)
        val day = calendar.getActualMaximum(Calendar.DATE)
        for (index in 1..day) {
            mDayData.add("$index 日")
        }

        mYearAdapter.setNewData(mYearData)
        mMonthAdapter.setNewData(mMonthData)
        mDayAdapter.setNewData(mDayData)

        mYearManager = PickerLayoutManager.Builder(activity).build()
        mMonthManager = PickerLayoutManager.Builder(activity).build()
        mDayManager = PickerLayoutManager.Builder(activity).build()

        mYearView.layoutManager = mYearManager
        mMonthView.layoutManager = mMonthManager
        mDayView.layoutManager = mDayManager

        mYearView.adapter = mYearAdapter
        mMonthView.adapter = mMonthAdapter
        mDayView.adapter = mDayAdapter

        setYear(calendar.get(Calendar.YEAR))
        setMonth(calendar.get(Calendar.MONTH) + 1)
        setDay(calendar.get(Calendar.DAY_OF_MONTH))

        mYearManager.setOnPickerListener(this)
        mMonthManager.setOnPickerListener(this)

        setCancel("取消", null)
    }


    fun setAutoDismiss(dismiss: Boolean): DateDialog {
        mAutoDismiss = dismiss
        return this
    }

    fun setTitle(@StringRes id: Int): DateDialog {
        return setTitle(getString(id))
    }

    fun setTitle(text: CharSequence): DateDialog {
        mTvDateTitle.text = text
        return this
    }

    fun setCancel(@StringRes id: Int, listener: OnCancelListener?): DateDialog {
        return setCancel(getString(id), listener)
    }

    fun setCancel(text: CharSequence, listener: OnCancelListener?): DateDialog {
        mTvDateCancel.text = text
        mTvDateCancel.setOnClickListener {
            listener?.onCancel(dialog!!)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun setConfirm(@StringRes id: Int, listener: OnSelectedListener?): DateDialog {
        return setConfirm(getString(id), listener)
    }

    fun setConfirm(text: CharSequence, listener: OnSelectedListener?): DateDialog {
        mTvDateConfirm.text = text
        mTvDateConfirm.setOnClickListener {
            val dialog = dialog ?: return@setOnClickListener
            listener?.onSelected(
                dialog,
                mStartYear + mYearManager.getPickedPosition(),
                mMonthManager.getPickedPosition() + 1,
                mDayManager.getPickedPosition() + 1)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    /**
     * 不选择天数
     */
    fun setIgnoreDay(): DateDialog {
        mDayView.visibility = View.GONE
        return this
    }

    fun setDate(date: Long): DateDialog {
        if (date > 0) {
            setDate(SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(date)))
        }
        return this
    }

    fun setDate(date: String): DateDialog {
        //20200507
        if (date.matches("\\d{8}".toRegex())) {
            setYear(date.substring(0, 4))
            setMonth(date.substring(4, 6))
            setDay(date.substring(6, 8))
            //2020-05-07
        } else if (date.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            setYear(date.substring(0, 4))
            setMonth(date.substring(5, 7))
            setDay(date.substring(8, 10))
        }
        return this
    }

    fun setYear(year: String): DateDialog {
        setYear(Integer.valueOf(year))
        return this
    }

    fun setYear(year: Int) {
        var index = year - mStartYear
        if (index < 0) {
            index = 0
        } else if (index > mYearAdapter.itemCount - 1) {
            index = mYearAdapter.itemCount - 1
        }
        mYearView.scrollToPosition(index)
    }

    fun setMonth(month: String): DateDialog {
        setMonth(Integer.valueOf(month))
        return this
    }

    fun setMonth(month: Int) {
        var index = month - 1
        if (index < 0) {
            index = 0
        } else if (index > mMonthAdapter.itemCount - 1) {
            index = mMonthAdapter.itemCount - 1
        }
        mMonthView.scrollToPosition(index)
    }

    fun setDay(day: String): DateDialog {
        return setDay(Integer.valueOf(day))
    }

    fun setDay(day: Int): DateDialog {
        var index = day - 1
        if (index < 0) {
            index = 0
        } else if (index > mDayAdapter.itemCount - 1) {
            index = mDayAdapter.itemCount - 1
        }
        mDayView.scrollToPosition(index)
        return this
    }

    override fun onPicked(recyclerView: RecyclerView?, position: Int) {
        // 获取这个月最多有多少天
        val calendar = Calendar.getInstance(Locale.CHINA)
        if (recyclerView === mYearView) {
            calendar[mStartYear + position, mMonthManager.getPickedPosition()] = 1
        } else if (recyclerView === mMonthView) {
            calendar[mStartYear + mYearManager.getPickedPosition(), position] = 1
        }

        val day = calendar.getActualMaximum(Calendar.DATE)
        if (mDayAdapter.itemCount != day) {
            val dayData: ArrayList<String> = ArrayList(day)
            for (index in 1..day) {
                dayData.add("$index 日")
            }
            mDayAdapter.setNewData(dayData)
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
         * 选择完日期后回调
         *
         * @param year              年
         * @param month             月
         * @param day               日
         */
        fun onSelected(dialog: BaseDialog, year: Int, month: Int, day: Int)


    }

    interface OnCancelListener {
        /**
         * 点击取消时回调
         */
        fun onCancel(dialog: BaseDialog)
    }
}

