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
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("???????????????")
        dataList.add("??????????????????")
        dataList.add("????????????")
        dataList.add("????????????")
        dataList.add("????????????")
        dataList.add("????????????")
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
        // ???????????????
        ToastDialog(this)
            .setType(ToastDialog.Type.FINISH)
            .setDuration(5000)
            .setMessage("??????")
            .show()
    }

    private fun warnDialog() {
        // ???????????????
        ToastDialog(this)
            .setType(ToastDialog.Type.WARN)
            .setDuration(5000)
            .setMessage("??????")
            .show()
    }

    private fun failDialog() {
        // ???????????????
        ToastDialog(this)
            .setType(ToastDialog.Type.ERROR)
            .setDuration(5000)
            .setMessage("??????")
            .show()
    }

    private fun messageDialog() {
        val dialog = MessageDialog(this)
            .setTitle("????????????")
            .setMessage("????????????????????????")
            .setAnimStyle(R.style.ScaleAnimStyle) as MessageDialog

        dialog.setConfirm("??????", object : MessageDialog.OnConfirmListener {
            override fun onConfirm(dialog: BaseDialog) {
                toast("??????")
            }
        }).setCancel("??????", object : MessageDialog.OnCancelListener {
            override fun onCancel(dialog: BaseDialog) {
                toast("??????")
            }
        }).show()
    }

    private fun inputDialog() {
        InputDialog(this)
            .setTitle("????????????")
            .setHint("?????????...")
            .setConfirm("??????", object : InputDialog.OnConfirmListener {
                override fun onConfirm(dialog: BaseDialog, content: String) {
                    toast(content)
                }
            })
            .show()
    }

    private fun bottomMenuDialog() {
        val data: MutableList<String> = ArrayList()
        for (i in 0..9) {
            data.add("????????????$i")
        }

        MenuDialog(this)
            .setList(data)
            .setLimitHeight(5, 200f)
            .setSelectedListener(object : MenuDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, position: Int) {
                    toast("?????????$position????????????${data[position]}")
                }
            })
            .setCancelListener(object : MenuDialog.OnCancelListener {
                override fun onCancel(dialog: BaseDialog?) {
                    toast("?????????")
                }
            })
            .show()
    }

    private fun centerMenuDialog() {
        val data: MutableList<String> = ArrayList()
        for (i in 0..9) {
            data.add("????????????$i")
        }
        // ???????????????
        MenuDialog(this)
            .setGravity(Gravity.CENTER)
            .setList(data)
            .setTitle("?????????")
            .setSelectedListener(object : MenuDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, position: Int) {
                    toast("?????????$position????????????${data[position]}")
                }
            })
            .show()
    }

    private fun loadingDialog() {
        // ???????????????
        val dialog: BaseDialog = WaitDialog(this)
            .setMessage("????????????")
            .show()
        RunnableUtils.postDelayed(Runnable {
            dialog.dismiss()
        }, 5000)
    }

    private fun customDialog() {
        class Builder(activity: FragmentActivity) : BaseDialogFragment.Builder(activity)
        // ??????????????????
        Builder(this)
            .setContentView(R.layout.dialog_custom)
            .setOnClickListener(R.id.btn_dialog_custom_ok) { dialog, _ -> dialog?.dismiss() }
            .addOnShowListener {
                toast("Dialog  ?????????")
            }
            .addOnCancelListener {
                toast("Dialog ?????????")
            }
            .addOnDismissListener {
                toast("Dialog ?????????")
            }
            .setOnKeyListener { _, event ->
                toast("???????????????" + event.keyCode)
                false
            }
            .show()
    }

    private fun dateDialog() {
        DateDialog(this)
            .setTitle("????????????")
            .setConfirm("??????", object : DateDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog, year: Int, month: Int, day: Int) {
                    toast("${year}???${month}???${day}???")
                    // ???????????????????????????????????????????????????
                    val calendar = Calendar.getInstance(Locale.CHINA)
                    calendar[Calendar.YEAR] = year
                    // ???????????????????????????????????? 1
                    calendar[Calendar.MONTH] = month - 1
                    calendar[Calendar.DAY_OF_MONTH] = day
                    toast("????????????" + calendar.timeInMillis)
                }

            })
            .show()
    }

    private fun timeDialog() {
        val dialog = TimeDialog(this)
            .setTitle("????????????")
            .setWidth((ScreenUtils.getScreenWidth() * 0.8).toInt()) as TimeDialog

        dialog.setConfirm("??????", object : TimeDialog.OnSelectedListener {
            override fun onSelected(dialog: BaseDialog, hour: Int, minute: Int, second: Int) {
                toast("${hour}???${minute}???${second}???")
                // ???????????????????????????????????????????????????
                val calendar = Calendar.getInstance()
                calendar[Calendar.HOUR_OF_DAY] = hour
                calendar[Calendar.MINUTE] = minute
                calendar[Calendar.SECOND] = second
                toast("????????????" + calendar.timeInMillis)
            }
        }).show()
    }

    private fun singleDialog() {
        // ???????????????
        SelectedDialog(this)
            .setTitle("?????????????????????")
            .setList("???", "???") // ??????????????????
            .setSingleSelect() // ??????????????????
            .setSelect(0)
            .setConfirm("??????", object : SelectedDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                    toast(data.toString())
                }
            })
            .setCancel("??????", null)
            .show()
    }

    private fun multipleDialog() {
        // ???????????????
        SelectedDialog(this)
            .setTitle("???????????????")
            .setList("??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "?????????", "?????????")
            .setLimitHeight(6, 300f)
            .setMaxSelect(4)
            .setConfirm("??????", object : SelectedDialog.OnSelectedListener {
                override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                    toast(data.toString())
                }
            })
            .setCancel("??????", null)
            .show()
    }

    fun toast(@NonNull msg: CharSequence) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
