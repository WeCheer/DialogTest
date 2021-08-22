package com.wyc.dialogsimple.dialog

import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.R
import com.wyc.dialogsimple.base.BaseViewHolder
import com.wyc.dialogsimple.utils.ScreenUtils
import java.util.*
import kotlin.collections.ArrayList


/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/5/7 18:01
 * <p>
 * 文件名字： com.wyc.tool.widget
 * <p>
 * 类的介绍：
 */
class SelectedDialog(private var mActivity: FragmentActivity) : BaseDialogFragment.Builder(mActivity) {

    private var mAdapter: SelectAdapter? = null

    private var mTvSelectCancel: TextView
    private var mTvSelectTitle: TextView
    private var mTvSelectConfirm: TextView
    private var mRecyclerView: RecyclerView

    private var mDataList = mutableListOf<String>()
    private var mAutoDismiss = true

    init {
        setContentView(R.layout.dialog_select)
        mRecyclerView = findViewById(R.id.rvSelectList)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.itemAnimator = null
        setWidth((ScreenUtils.getScreenWidth() * 0.75).toInt())

        mAdapter = SelectAdapter(mDataList)
        mRecyclerView.adapter = mAdapter

        mTvSelectCancel = findViewById(R.id.tvSelectCancel)
        mTvSelectTitle = findViewById(R.id.tvSelectTitle)
        mTvSelectConfirm = findViewById(R.id.tvSelectConfirm)

        setCancel("取消", null)
        setTitle("请选择")
    }

    fun setAutoDismiss(dismiss: Boolean): SelectedDialog {
        mAutoDismiss = dismiss
        return this
    }

    fun setTitle(@StringRes id: Int): SelectedDialog {
        return setTitle(getString(id))
    }

    fun setTitle(text: CharSequence): SelectedDialog {
        mTvSelectTitle.text = text
        return this
    }

    fun setCancel(@StringRes id: Int, listener: OnCancelListener?): SelectedDialog {
        return setCancel(getString(id), listener)
    }

    fun setCancel(text: CharSequence, listener: OnCancelListener?): SelectedDialog {
        mTvSelectCancel.text = text
        mTvSelectCancel.setOnClickListener {
            listener?.onCancel(getDialog()!!)
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }

    fun setConfirm(@StringRes id: Int, listener: OnSelectedListener?): SelectedDialog {
        return setConfirm(getString(id), listener)
    }

    fun setConfirm(text: CharSequence, listener: OnSelectedListener?): SelectedDialog {
        mTvSelectConfirm.text = text
        mTvSelectConfirm.setOnClickListener {
            val data: HashMap<Int, String> = mAdapter!!.getSelectSet()
            when {
                data.size == 0 || data.size >= mAdapter!!.getMinSelect() -> {
                    listener?.onSelected(dialog, data)
                }
                else -> {
                    Toast.makeText(mActivity, "已超出最大值${mAdapter!!.getMinSelect()}", Toast.LENGTH_SHORT).show()
                }
            }
            if (mAutoDismiss) {
                dismiss()
            }
        }
        return this
    }


    fun setList(vararg ids: Int): SelectedDialog {
        val data: MutableList<String> = ArrayList(ids.size)
        for (id in ids) {
            data.add(getString(id))
        }
        return setList(data)
    }

    fun setList(vararg data: String): SelectedDialog {
        return setList(data.toList() as MutableList<String>)
    }

    fun setList(data: MutableList<String>): SelectedDialog {
        mDataList.clear()
        mDataList.addAll(data)
        mAdapter?.setNewData(data)
        return this
    }

    fun setLimitHeight(count: Int, limitHeight: Float): SelectedDialog {
        if (mDataList.isEmpty()) {
            throw IllegalArgumentException("data list is empty!")
        }
        val lp = mRecyclerView.layoutParams
        if (mDataList.size > count) {
            lp.height = ScreenUtils.dip2px(limitHeight)
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        mRecyclerView.layoutParams = lp
        return this
    }

    /**
     * 设置默认选中的位置
     */
    fun setSelect(vararg positions: Int): SelectedDialog {
        mAdapter?.setSelect(positions)
        return this
    }

    /**
     * 设置最大选择数量
     */
    fun setMaxSelect(count: Int): SelectedDialog {
        mAdapter?.setMaxSelect(count)
        return this
    }

    /**
     * 设置最小选择数量
     */
    fun setMinSelect(count: Int): SelectedDialog {
        mAdapter?.setMinSelect(count)
        return this
    }

    /**
     * 设置单选模式
     */
    fun setSingleSelect(): SelectedDialog {
        mAdapter?.setSingleSelect()
        return this
    }

    private class SelectAdapter(private var mData: MutableList<String>) : RecyclerView.Adapter<BaseViewHolder>() {

        /** 最小选择数量  */
        private var mMinSelect = 1

        /** 最大选择数量  */
        private var mMaxSelect = Int.MAX_VALUE

        /** 选择对象集合  */
        private val mSelectSet: HashMap<Int, String> = HashMap()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_select, parent, false))
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.setText(R.id.tv_select_text, mData[position])
            val checkbox = holder.getView<AppCompatCheckBox>(R.id.tv_select_checkbox)

            checkbox.isChecked = mSelectSet.containsKey(holder.layoutPosition)
            if (mMaxSelect == 1) {
                checkbox.isClickable = false
            } else {
                checkbox.isEnabled = false
            }
            initListener(holder, holder.adapterPosition)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        fun setNewData(data: MutableList<String>) {
            this.mData = data
            notifyDataSetChanged()
        }

        private fun initListener(holder: BaseViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                if (mSelectSet.containsKey(position)) {
                    // 当前必须不是单选模式才能取消选中
                    if (!isSingleSelect()) {
                        mSelectSet.remove(position)
                        notifyItemChanged(position)
                    }
                } else {
                    if (mMaxSelect == 1) {
                        mSelectSet.clear()
                        notifyDataSetChanged()
                    }

                    when {
                        mSelectSet.size < mMaxSelect -> {
                            mSelectSet[position] = mData[position]
                            notifyItemChanged(position)
                        }
                        else -> {
                            Toast.makeText(it.context, "已超出设置最大值$mMaxSelect", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        fun setSelect(positions: IntArray) {
            for (position in positions) {
                mSelectSet[position] = mData[position]
            }
            notifyDataSetChanged()
        }

        fun setMaxSelect(count: Int) {
            mMaxSelect = count
        }

        fun setMinSelect(count: Int) {
            mMinSelect = count
        }

        fun getMinSelect(): Int {
            return mMinSelect
        }

        fun setSingleSelect() {
            setMaxSelect(1)
            setMinSelect(1)
        }

        fun isSingleSelect(): Boolean {
            return mMaxSelect == 1 && mMinSelect == 1
        }

        fun getSelectSet(): HashMap<Int, String> {
            return mSelectSet
        }
    }

    interface OnSelectedListener {
        /**
         * 选择回调
         *
         * @param data              选择的位置和数据
         */
        fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>)

    }

    interface OnCancelListener {
        /**
         * 点击取消时回调
         */
        fun onCancel(dialog: BaseDialog?)
    }
}