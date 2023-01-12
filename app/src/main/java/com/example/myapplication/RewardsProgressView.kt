package com.example.myapplication

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.ceil

/**
 * 投票View
 */
class RewardsProgressView : View {
    private val mData = ArrayList<PointData>()
    private val pointIconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressDefaultPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointValuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val pointLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mValueMatrix: Matrix = Matrix()
    private val mProgressIconMatrix: Matrix = Matrix()
    private val mValueRect: Rect = Rect()
    private val mLabelRect: Rect = Rect()
    private val mProgressRect: RectF = RectF()
    private val mProgressDefaultRect: RectF = RectF()
    private var mValueAnimator: ValueAnimator = ValueAnimator()
    private val mPts = FloatArray(2)
    private var progressRadius: Float = dp2px(2f)
    private var progressHeight: Float = dp2px(3f)
    private var pointValueMarginTop: Float = dp2px(4f)
    private var pointLabelMarginTop: Float = dp2px(2f)
    private var pointValueTextSize: Float = sp2px(14f)
    private var pointLabelTextSize: Float = sp2px(8f)
    private var pointHeight: Float = dp2px(18f)
    private var progressIcon: Bitmap? = null
    private var pointNotReachedIcon: Bitmap? = null
    private var pointSoonReachedIcon: Bitmap? = null
    private var pointReachedIcon: Bitmap? = null
    private var progressDefaultColor: IntArray? = null
    private var progressColor: IntArray? = null
    private var mViewHeight = 0f
    private var mProgress = 0
    private var maxScrollOffset = 0f
    private var mLastScroll = 0f
    private var mFirstScroll = 0f
    private var mScrollValues = 0f
    private var mSoonReached = false
    private var mIsmProgressChange = false

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    init {
        val value = "90"
        val label = "CGgyYIiFf"
        progressDefaultColor = intArrayOf(
                Color.parseColor("#7A6E7BD2"),
                Color.parseColor("#F26E7BD2"),
                Color.parseColor("#B06E7BD2")
        )
        progressColor = intArrayOf(
                Color.parseColor("#FEB807"),
                Color.parseColor("#FFDA16")
        )
        progressDefaultPaint.style = Paint.Style.FILL
        progressPaint.style = Paint.Style.FILL
        pointValuePaint.color = Color.parseColor("#FFFFFF")
        pointValuePaint.typeface = Typeface.DEFAULT_BOLD
        pointValuePaint.textSize = pointValueTextSize

        pointLabelPaint.color = Color.parseColor("#BED0FF")
        pointLabelPaint.textSize = pointLabelTextSize
        pointLabelPaint.textAlign = if (isLayoutRtl()) Paint.Align.RIGHT else Paint.Align.LEFT

        mViewHeight = pointHeight + pointValueMarginTop + pointLabelMarginTop
        pointValuePaint.getTextBounds(value, 0, value.length, mValueRect)
        mViewHeight += mValueRect.height()
        pointLabelPaint.getTextBounds(label, 0, label.length, mLabelRect)
        mViewHeight += mLabelRect.height()
        mViewHeight += dp2px(3f)//高度补偿
        initAnimatorListener()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = mViewHeight + paddingTop + paddingBottom
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ceil(height).toInt())
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!changed) {
            return
        }
        val left = paddingStart.toFloat()
        val top = paddingTop.toFloat()
        val right = width - left
        val offset = (pointHeight - progressHeight) / 2f
        mProgressRect.set(left, top + offset, right, top + offset + progressHeight)
        mProgressDefaultRect.set(left, top + offset, right, top + offset + progressHeight)
        mFirstScroll = if (isLayoutRtl()) {
            width.toFloat()
        } else {
            0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mData.isEmpty()) return
        canvas.translate(mScrollValues, 0f)
        canvas.drawRoundRect(mProgressDefaultRect, progressRadius, progressRadius, progressDefaultPaint)
        var progressRight = 0f
        var prevX = 0f
        var prevValue = 0
        if (mIsmProgressChange) {
            mIsmProgressChange = false
            mSoonReached = false
            for (i in mData.indices) {
                val item = mData[i]
                var pointWidthOffset = 0f
                getPointCoin(item.value)?.let {
                    calculationPointCoinPosition(it, i)
                    pointWidthOffset = it.width / 2f
                }
                if (mProgress <= item.value) {
                    val diffWidth = mPts[0] - prevX
                    val diffValue = item.value - prevValue
                    val value = mProgress - prevValue
                    progressRight = prevX + diffWidth * (value.toFloat() / diffValue.toFloat()) + pointWidthOffset
                    break
                } else {
                    val diffWidth = mProgressDefaultRect.right - mPts[0]
                    val diffValue = item.value - prevValue
                    val value = mProgress - item.value
                    progressRight = mPts[0] + diffWidth * (value.toFloat() / diffValue.toFloat()) + pointWidthOffset
                    prevX = mPts[0]
                    prevValue = item.value
                }
            }
            progressRight = if (isLayoutRtl()) {
                progressRight.coerceAtLeast(mProgressDefaultRect.right)
            } else {
                progressRight.coerceAtMost(mProgressDefaultRect.right)
            }
            mProgressRect.set(
                    mProgressRect.left,
                    mProgressRect.top,
                    progressRight,
                    mProgressRect.bottom
            )
            progressColor?.let {
                progressPaint.shader = LinearGradient(
                        mProgressRect.left,
                        mProgressRect.top,
                        mProgressRect.right,
                        mProgressRect.top,
                        it,
                        null,
                        Shader.TileMode.MIRROR)
            }
        }
        if (mProgress > 0f) {
            canvas.drawRoundRect(mProgressRect, progressRadius, progressRadius, progressPaint)
            if (null != progressIcon) {
                progressIcon?.let {
                    var iconX = mProgressRect.right
                    val iconY = mProgressRect.top - (it.height - mProgressRect.height()) / 2f
                    val scaleX = if (isLayoutRtl()) {
                        iconX += it.width / 2f
                        -1f
                    } else {
                        iconX -= it.width / 2f
                        1f
                    }
                    mProgressIconMatrix.reset()
                    mProgressIconMatrix.postScale(scaleX, 1f)
                    mProgressIconMatrix.postTranslate(iconX, iconY)
                    canvas.drawBitmap(it, mProgressIconMatrix, pointIconPaint)
                }
            }
        }
        mSoonReached = false
        val leftX = left + paddingStart
        val rightX = right - paddingEnd
        for (i in mData.indices) {
            val item = mData[i]
            getPointCoin(item.value)?.let {
                calculationPointCoinPosition(it, i)
                if (mPts[0] + mScrollValues < leftX) return@let
                if (mPts[0] + mScrollValues > rightX) return@let
                val value = item.value.toString()
                val textWidth = pointValuePaint.measureText(value)
                var textX = mPts[0] - (textWidth - it.width) / 2f
                var textY = paddingTop + pointHeight + pointValueMarginTop + mValueRect.height()
                canvas.drawBitmap(it, mPts[0], mPts[1], pointIconPaint)
                canvas.drawText(value, textX, textY, pointValuePaint)
                textY += pointLabelMarginTop + mLabelRect.height()
                textX = if (isLayoutRtl()) (mPts[0] + textWidth) else textX
                canvas.drawText(item.label, textX, textY, pointLabelPaint)
            }
        }
        canvas.clipRect(left, top, right, bottom)
    }

    private fun calculationPointCoinPosition(icon: Bitmap, index: Int) {
        val offsetX = width.toFloat() * 0.25f
        val diffHeight = pointHeight - icon.height
        var offsetY = 0f
        mPts[0] = index.toFloat()
        mValueMatrix.mapPoints(mPts)
        if (diffHeight > 0f) {
            offsetY = diffHeight / 2f
        }
        if (isLayoutRtl()) {
            mPts[0] = mPts[0] - offsetX - icon.width / 2f
        } else {
            mPts[0] = mPts[0] + offsetX - icon.width / 2f
        }
        mPts[1] = offsetY + paddingTop.toFloat()
    }

    private fun computeScrollRange(dataSize: Int) {
        val width = width.toFloat()
        val scaleX = width / dataSize.toFloat()
        val valueScaleX = dataSize.toFloat() / 2f
        mScrollValues = mFirstScroll
        mValueMatrix.reset()
        if (isLayoutRtl()) {
            mValueMatrix.postScale(-(scaleX * valueScaleX), 1f)
        } else {
            mValueMatrix.postScale(scaleX * valueScaleX, 1f)
        }
        maxScrollOffset = if (valueScaleX > 1f) {
            width * (valueScaleX - 1f)
        } else {
            0f
        }
        val compensateOffset = if (maxScrollOffset > 0f) width * 0.5f else 0f
        val rectRight = if (isLayoutRtl()) {
            -(width + maxScrollOffset + compensateOffset)
        } else {
            (width + maxScrollOffset + compensateOffset)
        }
        mProgressDefaultRect.set(
                mProgressDefaultRect.left,
                mProgressDefaultRect.top,
                rectRight,
                mProgressDefaultRect.bottom
        )
        progressDefaultColor?.let {
            progressDefaultPaint.shader = LinearGradient(
                    mProgressDefaultRect.left,
                    mProgressDefaultRect.top,
                    mProgressDefaultRect.right,
                    mProgressDefaultRect.top,
                    it,
                    null,
                    Shader.TileMode.MIRROR)
        }
    }

    private fun getPointCoin(value: Int): Bitmap? {
        return if (mProgress >= value) {
            pointReachedIcon
        } else if (mProgress == 0 || mSoonReached) {
            pointNotReachedIcon
        } else {
            mSoonReached = true
            pointSoonReachedIcon
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        progressIcon = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_blind_box_lightning))
        pointNotReachedIcon = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_blind_box_not_reached))
        pointSoonReachedIcon = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_blind_box_soon_reached))
        pointReachedIcon = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_blind_box_reached))
        if (isInEditMode) {
            setData(getPreviewData())
            setProgress(12)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressIcon?.recycle()
        pointNotReachedIcon?.recycle()
        pointSoonReachedIcon?.recycle()
        pointReachedIcon?.recycle()
        progressIcon = null
        pointNotReachedIcon = null
        pointSoonReachedIcon = null
        pointReachedIcon = null
    }

    fun setData(data: ArrayList<PointData>) {
        mData.clear()
        mData.addAll(data)
        computeScrollRange(data.size)
        postInvalidateOnAnimation()
    }

    fun setProgress(progress: Int) {
        mIsmProgressChange = mProgress != progress
        mProgress = progress
        postInvalidateOnAnimation()
    }

    fun scrollToPoint(pointValue: Int) {
        val width = width.toFloat()
        val scroll = mScrollValues
        var valueScroll = scroll
        for (i in mData.indices) {
            val item = mData[i]
            if (pointValue == item.value) {
                getPointCoin(item.value)?.let {
                    calculationPointCoinPosition(it, i)
                    valueScroll = if (isLayoutRtl()) {
                        mPts[0] + width * 0.25f + it.width / 2f
                    } else {
                        mPts[0] - width * 0.25f + it.width / 2f
                    }
                }
                break
            }
        }
        val diffScroll = if (isLayoutRtl()) {
            valueScroll + abs(scroll - mFirstScroll)
        } else {
            valueScroll - abs(scroll)
        }
        diffScroll.coerceAtMost(maxScrollOffset)
        mValueAnimator.setFloatValues(0f, diffScroll)
        startAnimator()
    }

    private fun initAnimatorListener() {
        mValueAnimator.duration = 300
        mValueAnimator.addUpdateListener {
            val dx = it.animatedValue.toString().toFloat()
            val absDx = abs(dx)
            val scroll = absDx - mLastScroll
            mLastScroll = absDx
            if (dx > 0) {
                mScrollValues -= scroll
            } else {
                mScrollValues += scroll
            }
            postInvalidateOnAnimation()
        }
    }

    private fun startAnimator() {
        if (mValueAnimator.isRunning) {
            mValueAnimator.cancel()
        }
        mLastScroll = 0f
        mValueAnimator.start()
    }

    private fun getPreviewData(): ArrayList<PointData> {
        val list = ArrayList<PointData>()
        list.add(PointData(10, "Cumulative purchase quantity"))
        list.add(PointData(20, "Cumulative purchase quantity"))
        list.add(PointData(30, "Cumulative purchase quantity"))
        list.add(PointData(40, "Cumulative purchase quantity"))
        return list
    }

    private fun isLayoutRtl(): Boolean {
        return layoutDirection == LAYOUT_DIRECTION_RTL
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dp2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    private fun sp2px(spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (null == drawable) {
            return null
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    class PointData(
            val value: Int,
            val label: String
    )
}