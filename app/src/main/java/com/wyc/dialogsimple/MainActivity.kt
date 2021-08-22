package com.wyc.dialogsimple

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wyc.dialogsimple.utils.RunnableUtils
import com.wyc.dialog.base.BaseDialog
import com.wyc.dialog.base.BaseDialogFragment
import com.wyc.dialogsimple.base.BaseViewHolder
import com.wyc.dialogsimple.dialog.*
import com.wyc.dialogsimple.utils.ScreenUtils
import com.wyc.dialogsimple.widget.DividerDecoration
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mAdapter: DialogAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dialogRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = DialogAdapter(getListData())
        dialogRecyclerView.adapter = mAdapter
        dialogRecyclerView.addItemDecoration(DividerDecoration(ScreenUtils.dip2px(30f), Color.TRANSPARENT))
        mAdapter?.setOnItemClickListener(object : DialogAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (position) {
                    0 -> successDialog()
                    1 -> warnDialog()
                    2 -> failDialog()
                    3 -> messageDialog()
                    4 -> inputDialog()
                    5 -> bottomMenuDialog()
                    6 -> centerMenuDialog()
                    7 -> loadingDialog()
                    8 -> customDialog()
                    9 -> dateDialog()
                    10 -> timeDialog()
                    11 -> singleDialog()
                    12 -> multipleDialog()
                }
            }
        })
    }


    private fun getListData(): MutableList<String> {
        val dataList = mutableListOf<String>()
        dataList.add("完成对话框")
        dataList.add("警告对话框")
        dataList.add("错误对话框")
        dataList.add("消息对话框")
        dataList.add("输入对话框")
        dataList.add("底部选择框")
        dataList.add("中间选择框")
        dataList.add("加载对话框")
        dataList.add("自定义对话框")
        dataList.add("日期选择")
        dataList.add("时间选择")
        dataList.add("单项选择")
        dataList.add("多项选择")
        return dataList
    }

    private class DialogAdapter(private var mDataList: MutableList<String>) : RecyclerView.Adapter<BaseViewHolder>() {

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }

        private var mListener: OnItemClickListener? = null

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.mListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_recycler, parent, false))
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.setText(R.id.item_dialog_title, mDataList[position])
            holder.itemView.setOnClickListener {
                mListener?.onItemClick(it, holder.adapterPosition)
            }
        }
    }

    private fun successDialog() {
        // 成功对话框
        ToastDialog(this)
            .setType(ToastDialog.Type.FINISH)
            .setDuration(5000)
            .setMessage("完成")
            .show()
    }

    private fun warnDialog() {
        // 警告对话框
        ToastDialog(this)
            .setType(ToastDialog.Type.WARN)
            .setDuration(5000)
            .setMessage("警告")
            .show()
    }

    private fun failDialog() {
        // 失败对话框
        ToastDialog(this)
            .setType(ToastDialog.Type.ERROR)
            .setDuration(5000)
            .setMessage("错误")
            .show()
    }

    private fun messageDialog() {
        val dialog = MessageDialog(this)
            .setTitle("温馨提示")
            .setMessage("确认是否要删除？")
            .setAnimStyle(R.style.ScaleAnimStyle) as MessageDialog

        dialog.setConfirm("删除", object : MessageDialog.OnConfirmListener {
            override fun onConfirm(dialog: BaseDialog) {
                toast("删除")
            }
        }).setCancel("取消", object : MessageDialog.OnCancelListener {
            override fun onCancel(dialog: BaseDialog) {
                toast("取消")
            }
        }).show()
    }

    private fun inputDialog() {
        InputDialog(this)
            .setTitle("温馨提示")
            .setHint("请输入...")
            .setConfirm("确定", object : InputDialog.OnConfirmListener {
                override fun onConfirm(dialog: BaseDialog, content: String) {
                    toast(content)
                }
            })
            .show()
    }

    private fun bottomMenuDialog() {
        val data: MutableList<String> = ArrayList()
        for (i in 0..9) {
            data.add("我是数据$i")
        }

        MenuDialog(this)
            .setList(data)
            .setLimitHeight(5, 200f)
            .setSelectedListener(object : MenuDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, position: Int) {
                    toast("位置：$position，文本：${data[position]}")
                }
            })
            .setCancelListener(object : MenuDialog.OnCancelListener {
                override fun onCancel(dialog: BaseDialog?) {
                    toast("取消了")
                }
            })
            .show()
    }

    private fun centerMenuDialog() {
        val data: MutableList<String> = ArrayList()
        for (i in 0..9) {
            data.add("我是数据$i")
        }
        // 居中选择框
        MenuDialog(this)
            .setGravity(Gravity.CENTER)
            .setList(data)
            .setTitle("请选择")
            .setSelectedListener(object : MenuDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, position: Int) {
                    toast("位置：$position，文本：${data[position]}")
                }
            })
            .show()
    }

    private fun loadingDialog() {
        // 等待对话框
        val dialog: BaseDialog = WaitDialog(this)
            .setMessage("正在加载")
            .show()
        RunnableUtils.postDelayed(Runnable {
            dialog.dismiss()
        }, 5000)
    }

    private fun customDialog() {
        class Builder(activity: FragmentActivity) : BaseDialogFragment.Builder(activity)
        // 自定义对话框
        Builder(this)
            .setContentView(R.layout.dialog_custom)
            .setOnClickListener(R.id.btn_dialog_custom_ok) { dialog, _ -> dialog?.dismiss() }
            .addOnShowListener {
                toast("Dialog  显示了")
            }
            .addOnCancelListener {
                toast("Dialog 取消了")
            }
            .addOnDismissListener {
                toast("Dialog 销毁了")
            }
            .setOnKeyListener { _, event ->
                toast("按键代码：" + event.keyCode)
                false
            }
            .show()
    }

    private fun dateDialog() {
        DateDialog(this)
            .setTitle("选择日期")
            .setConfirm("确定", object : DateDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog, year: Int, month: Int, day: Int) {
                    toast("${year}年${month}月${day}日")
                    // 如果不指定年月日则默认为今天的日期
                    val calendar = Calendar.getInstance(Locale.CHINA)
                    calendar[Calendar.YEAR] = year
                    // 月份从零开始，所以需要减 1
                    calendar[Calendar.MONTH] = month - 1
                    calendar[Calendar.DAY_OF_MONTH] = day
                    toast("时间戳：" + calendar.timeInMillis)
                }

            })
            .show()
    }

    private fun timeDialog() {
        val dialog = TimeDialog(this)
            .setTitle("时间选择")
            .setWidth((ScreenUtils.getScreenWidth() * 0.8).toInt()) as TimeDialog

        dialog.setConfirm("确定", object : TimeDialog.OnSelectedListener {
            override fun onSelected(dialog: BaseDialog, hour: Int, minute: Int, second: Int) {
                toast("${hour}时${minute}分${second}秒")
                // 如果不指定时分秒则默认为现在的时间
                val calendar = Calendar.getInstance()
                calendar[Calendar.HOUR_OF_DAY] = hour
                calendar[Calendar.MINUTE] = minute
                calendar[Calendar.SECOND] = second
                toast("时间戳：" + calendar.timeInMillis)
            }
        }).show()
    }

    private fun singleDialog() {
        // 单选对话框
        SelectedDialog(this)
            .setTitle("请选择你的性别")
            .setList("男", "女") // 设置单选模式
            .setSingleSelect() // 设置默认选中
            .setSelect(0)
            .setConfirm("确定", object : SelectedDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                    toast(data.toString())
                }
            })
            .setCancel("取消", null)
            .show()
    }

    private fun multipleDialog() {
        // 多选对话框
        SelectedDialog(this)
            .setTitle("请选择月份")
            .setList("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月")
            .setLimitHeight(6, 300f)
            .setMaxSelect(4)
            .setConfirm("确定", object : SelectedDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                    toast(data.toString())
                }
            })
            .setCancel("放弃", null)
            .show()
    }

    fun toast(@NonNull msg: CharSequence) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
