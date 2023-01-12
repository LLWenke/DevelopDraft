package com.example.myapplication

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_hot_comment.view.*
import kotlin.math.ceil

/**
 * 热门评论
 */
class HotCommentView constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), Runnable {
    private var mAnimIn: Animation? = null
    private var mAnimOut: Animation? = null
    private var mAnimInStart = true
    private var mAnimOutStart = true
    private var mInterval = 2000L
    private var animDuration = 500
    private var mCurrentView: View? = null
    private var mNextView: View? = null
    private var mView1: View? = null
    private var mView2: View? = null
    private var mCurrentPageIndex = 0
    private var mNextPageIndex = 0
    private var mPageCount = 0
    private var mData: ArrayList<BaseBean> = ArrayList()
    private var mHandler = Handler()

    init {
        initView()
        if (isInEditMode) {
            showPreviewData()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.e("边界", "left:" + left + "   top:" + top + "   right:" + right + "   bottom:" + bottom)
    }

    private fun initView() {
        mAnimIn = AnimationUtils.loadAnimation(context, R.anim.anim_scroll_in)
        mAnimOut = AnimationUtils.loadAnimation(context, R.anim.anim_scroll_out)
        mAnimIn?.duration = animDuration.toLong()
        mAnimOut?.duration = animDuration.toLong()
        mView1 =
            LayoutInflater.from(context).inflate(R.layout.view_hot_comment, this, false).apply {
                visibility = View.INVISIBLE
                addView(this)
            }
        mView2 =
            LayoutInflater.from(context).inflate(R.layout.view_hot_comment, this, false).apply {
                visibility = View.INVISIBLE
                addView(this)
            }
        mAnimIn?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                mNextView?.visibility = View.VISIBLE
                mAnimInStart = true
            }

            override fun onAnimationEnd(animation: Animation?) {
                mNextView?.visibility = View.VISIBLE
                mAnimInStart = false
                toggleView()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        mAnimOut?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                mCurrentView?.visibility = View.VISIBLE
                mAnimOutStart = true
            }

            override fun onAnimationEnd(animation: Animation?) {
                mCurrentView?.visibility = View.INVISIBLE
                mAnimOutStart = false
                toggleView()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }

    private fun bindData(page: Int, view: View) {
        val position = page * 2
        getItem(position - 2)?.let {
//            view.user_icon1.setHeadPortraitUrl(it.getUrl(), null, 0L, 0L)
            view.tv_comment1.text = it.getContent()
        }
        getItem(position - 1)?.let {
//            view.user_icon2.setHeadPortraitUrl(it.getUrl(), null, 0L, 0L)
            view.tv_comment2.text = it.getContent()
            view.group_comment2.visibility = View.VISIBLE
            return
        }
        view.group_comment2.visibility = View.INVISIBLE
    }

    private fun getItem(position: Int): BaseBean? {
        if (position < 0 || position >= mData.size) {
            return null
        }
        return mData[position]
    }

    fun setData(data: ArrayList<BaseBean>) {
        mData.clear()
        mData.addAll(data)
        mPageCount = ceil(mData.size.toDouble() / 2.0).toInt()
        if (mPageCount == 0) {
            stopFlipping()
            mCurrentView?.visibility = View.INVISIBLE
            mCurrentPageIndex = 0
            mNextPageIndex = 0
            return
        }
        initCurrentView()
        initNextView()
        mCurrentView?.let {
            mCurrentPageIndex = 1
            bindData(mCurrentPageIndex, it)
            it.visibility = View.VISIBLE
        }
        if (mPageCount > 1) {
            mNextView?.let {
                mNextPageIndex = 2
                bindData(mNextPageIndex, it)
            }
            startFlipping()
        }
    }

    private fun initCurrentView() {
        mCurrentView = if (mNextView == mView1) {
            mView2
        } else {
            mView1
        }
    }

    private fun initNextView() {
        mNextView = if (mCurrentView == mView1) {
            mView2
        } else {
            mView1
        }
    }

    private fun toggleView() {
        if (mAnimInStart || mAnimOutStart) {
            return
        }
        mCurrentView = mNextView
        mCurrentPageIndex = mNextPageIndex
        initNextView()
        mNextView?.let {
            mNextPageIndex = getPageIndex(mCurrentPageIndex + 1)
            bindData(mNextPageIndex, it)
        }
        startFlipping()
    }

    private fun getPageIndex(pageIndex: Int): Int {
        if (pageIndex <= 0) {
            return 1
        }
        if (pageIndex > mPageCount) {
            return 1
        }
        return pageIndex
    }

    fun startFlipping() {
        stopFlipping()
        if (mPageCount > 1) {
            mHandler.postDelayed(this, mInterval)
        }
    }

    fun stopFlipping() {
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun run() {
        mAnimOut?.let {
            mCurrentView?.startAnimation(it)
        }
        mAnimIn?.let {
            mNextView?.startAnimation(it)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopFlipping()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startFlipping()
    }

    private fun showPreviewData() {
        val data: ArrayList<BaseBean> = ArrayList()
        data.add(TestBean("别幻想了，BTC马上要归零了，机构割肉的…"))
        data.add(TestBean("早就入局了，比特币翻十倍"))
        setData(data)
    }


    interface BaseBean {
        fun getContent(): String
        fun getUrl(): String
    }

    class TestBean(private val content: String) : BaseBean {
        override fun getContent(): String {
            return content
        }

        override fun getUrl(): String {
            return ""
        }

    }

}