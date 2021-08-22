package com.wyc.dialogsimple.manager

import android.content.Context
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min


/**
 *作者： wyc
 * <p>
 * 创建时间： 2020/5/7 15:00
 * <p>
 * 文件名字：
 * <p>
 * 类的介绍：
 */
class PickerLayoutManager private constructor(
    context: Context,
    private var mOrientation: Int,
    reverseLayout: Boolean,
    private var mMaxItem: Int,
    private var mScale: Float,
    private var mAlpha: Boolean
) : LinearLayoutManager(context, mOrientation, reverseLayout) {

    private val mHelper = LinearSnapHelper()
    private var mRecyclerView: RecyclerView? = null
    private var mListener: OnPickerListener? = null

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        mRecyclerView = view
        //设置子控件的边界可以超过父布局的范围
        mRecyclerView?.clipToPadding = false
        //添加LinearSnapHelper
        mHelper.attachToRecyclerView(mRecyclerView)
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        mRecyclerView = null
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return mMaxItem == 0
    }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        mRecyclerView?.let {
            var width = RecyclerView.LayoutManager.chooseSize(
                widthSpec,
                paddingLeft + paddingRight,
                ViewCompat.getMinimumWidth(it)
            )
            var height = RecyclerView.LayoutManager.chooseSize(
                heightSpec,
                paddingTop + paddingBottom,
                ViewCompat.getMinimumHeight(it))

            if (state.itemCount != 0 && mMaxItem != 0) {
                val itemView = recycler.getViewForPosition(0)
                measureChildWithMargins(itemView, widthSpec, heightSpec)

                if (mOrientation == HORIZONTAL) {
                    val measureWidth = itemView.measuredWidth
                    val paddingHorizontal = (mMaxItem - 1) / 2 * measureWidth
                    it.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
                    width = measureWidth * mMaxItem
                } else if (mOrientation == VERTICAL) {
                    val measureHeight = itemView.measuredHeight
                    val paddingVertical = (mMaxItem - 1) / 2 * measureHeight
                    it.setPadding(0, paddingVertical, 0, paddingVertical)
                    height = measureHeight * mMaxItem
                }
            }
            setMeasuredDimension(width, height)
        }

    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        //当RecyclerView停止滚动时
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            mListener?.onPicked(mRecyclerView, getPickedPosition())
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount < 0 || state.isPreLayout) {
            return
        }

        if (mOrientation == HORIZONTAL) {
            scaleHorizontalChildView()
        } else if (mOrientation == VERTICAL) {
            scaleVerticalChildView()
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        scaleHorizontalChildView()
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        scaleVerticalChildView()
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     * 横向滚动的情况
     */
    private fun scaleHorizontalChildView() {
        val mid = width / 2f
        for (index in 0 until childCount) {
            val childView = getChildAt(index)
            childView?.let {
                val childMid = (getDecoratedLeft(it) + getDecoratedRight(it)) / 2f
                val scale = 1.0f + -1 * (1 - mScale) * min(mid, abs(mid - childMid)) / mid
                it.scaleX = scale
                it.scaleY = scale
                if (mAlpha) {
                    it.alpha = scale
                }
            }
        }
    }

    /**
     * 纵向滚动的情况
     */
    private fun scaleVerticalChildView() {
        val mid = height / 2.0f
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView?.let {
                val childMid = (getDecoratedTop(it) + getDecoratedBottom(it)) / 2.0f
                val scale = 1.0f + -1 * (1 - mScale) * min(mid, abs(mid - childMid)) / mid
                it.scaleX = scale
                it.scaleY = scale
                if (mAlpha) {
                    it.alpha = scale
                }
            }
        }
    }

    /**
     * 获取选中的位置
     */
    fun getPickedPosition(): Int {
        val itemView = mHelper.findSnapView(this)
        return if (itemView != null) {
            getPosition(itemView)
        } else {
            0
        }
    }

    /**
     * 设置监听器
     */
    fun setOnPickerListener(listener: OnPickerListener) {
        mListener = listener
    }

    interface OnPickerListener {
        /**
         * 滚动停止时触发的监听
         *
         * @param recyclerView              RecyclerView 对象
         * @param position                  当前滚动的位置
         */
        fun onPicked(recyclerView: RecyclerView?, position: Int)
    }

    class Builder(private var mContext: Context) {
        private var mOrientation = VERTICAL
        private var mReverseLayout: Boolean = false
        private var mListener: OnPickerListener? = null

        private var mMaxItem = 3
        private var mScale = 0.6f
        private var mAlpha = true

        /**
         * 设置布局方向
         */
        fun setOrientation(@RecyclerView.Orientation orientation: Int): Builder {
            mOrientation = orientation
            return this
        }

        /**
         * 是否反向显示
         */
        fun setReverseLayout(reverseLayout: Boolean): Builder {
            mReverseLayout = reverseLayout
            return this
        }

        /**
         * 最大显示条目
         */
        fun setMaxItem(maxItem: Int): Builder {
            mMaxItem = maxItem
            return this
        }

        /**
         * 设置缩放比例
         */
        fun setScale(scale: Float): Builder {
            mScale = scale
            return this
        }

        /**
         * 设置透明开关
         */
        fun setAlpha(alpha: Boolean): Builder {
            mAlpha = alpha
            return this
        }

        /**
         * 设置选择监听事件
         */
        fun setOnPickListener(listener: OnPickerListener): Builder {
            mListener = listener
            return this
        }

        fun build(): PickerLayoutManager {
            val layoutManager = PickerLayoutManager(
                mContext,
                mOrientation,
                mReverseLayout,
                mMaxItem,
                mScale,
                mAlpha
            )
            mListener?.let { layoutManager.setOnPickerListener(it) }
            return layoutManager
        }

        /**
         *
         * 应用到RecyclerView
         */
        fun into(recyclerView: RecyclerView) {
            recyclerView.layoutManager = build()
        }
    }
}