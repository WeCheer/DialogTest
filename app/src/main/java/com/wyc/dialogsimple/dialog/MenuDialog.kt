package com.wyc.dialogsimple.dialog

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wyc.dialog.base.BaseAnimStyle
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.R
import com.wyc.dialogsimple.base.BaseViewHolder
import com.wyc.dialogsimple.utils.ScreenUtils
import java.util.*

/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/4/9 17:48
 * <p>
 * 文件名字： com.wyc.tool.widget.dialog
 * <p>
 * 类的介绍：菜单Dialog
 */
class MenuDialog(activity: FragmentActivity) : BaseDialogFragment.Builder(activity),
    View.OnClickListener {

    private var mSelectedListener: OnSelectedListener? = null
    private var mCancelListener: OnCancelListener? = null
    private var mAutoDismiss = true

    private var mRecyclerView: RecyclerView
    private var mAdapter: MenuAdapter
    private var mCancelView: TextView
    private var mDataList = mutableListOf<String>()
    private var mContainer: View
    private var mTitleView: TextView
    private var mCancelContainer: View

    init {
        setContentView(R.layout.dialog_menu)
        setAnimStyle(BaseAnimStyle.BOTTOM)

        mRecyclerView = findViewById(R.id.rv_menu_list)
        mCancelView = findViewById(R.id.tv_menu_cancel)
        mContainer = findViewById(R.id.rl_title_container)
        mTitleView = findViewById(R.id.tv_menu_title)
        mCancelContainer = findViewById(R.id.cancel_container)

        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.setHasFixedSize(true)
        mAdapter = MenuAdapter(mDataList)
        mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, position ->
            if (mAutoDismiss) {
                dismiss()
            }
            mSelectedListener?.onSelected(dialog, position)
        }

        mCancelView.setOnClickListener(this)
    }

    override fun setGravity(gravity: Int): MenuDialog {
        when (gravity) {
            Gravity.CENTER, Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL -> {
                //不显示取消按钮
                setCancel(null)
                //重新设置动画
                setAnimStyle(BaseAnimStyle.SCALE)
            }
        }
        return super.setGravity(gravity) as MenuDialog
    }

    fun setList(vararg ids: Int): MenuDialog {
        val data = ArrayList<String>(ids.size)
        ids.forEach {
            data.add(getString(it))
        }
        return setList(data)
    }

    fun setList(vararg data: String): MenuDialog {
        return setList(data.toList() as MutableList<String>)
    }

    fun setList(data: MutableList<String>): MenuDialog {
        mDataList.clear()
        mDataList.addAll(data)
        mAdapter.setNewData(mDataList)
        return this
    }

    fun setTitle(@StringRes id: Int): MenuDialog {
        return setTitle(getString(id))
    }

    fun setTitle(text: CharSequence): MenuDialog {
        mContainer.visibility = View.VISIBLE
        mTitleView.text = text
        return this
    }

    fun setLimitHeight(count: Int, limitHeight: Float): MenuDialog {
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

    fun setCancel(@StringRes id: Int): MenuDialog {
        return setCancel(getString(id))
    }

    fun setCancel(text: CharSequence?): MenuDialog {
        if (TextUtils.isEmpty(text)) {
            mCancelContainer.visibility = View.GONE
        }
        mCancelView.text = text
        return this
    }

    fun setAutoDismiss(dismiss: Boolean): MenuDialog {
        mAutoDismiss = dismiss
        return this
    }

    fun setSelectedListener(listener: OnSelectedListener): MenuDialog {
        mSelectedListener = listener
        return this
    }

    fun setCancelListener(listener: OnCancelListener?): MenuDialog {
        mCancelListener = listener
        return this
    }

    override fun onClick(v: View) {
        if (mAutoDismiss) {
            dismiss()
        }
        if (v == mCancelView) {
            mCancelListener?.onCancel(dialog)
        }
    }

    interface OnSelectedListener {
        /**
         * 选择条目时回调
         */
        fun onSelected(dialog: BaseDialog?, position: Int)

    }

    interface OnCancelListener {
        /**
         * 点击取消时回调
         */
        fun onCancel(dialog: BaseDialog?)
    }

    private class MenuAdapter(private var mDataList: MutableList<String>) : RecyclerView.Adapter<BaseViewHolder>() {
        private var mListener: ((view: View, position: Int) -> Unit)? = null

        fun setOnItemClickListener(listener: ((view: View, position: Int) -> Unit)?) {
            this.mListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false))
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.setText(R.id.tv_menu_name, mDataList[position])
            holder.itemView.setOnClickListener {
                mListener?.invoke(it, holder.adapterPosition)
            }
        }

        fun setNewData(dataList: MutableList<String>) {
            this.mDataList = dataList
            notifyDataSetChanged()
        }
    }


}