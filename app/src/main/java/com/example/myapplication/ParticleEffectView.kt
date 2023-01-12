package com.example.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import java.util.*

class ParticleEffectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val borderWidth: Float = dip2px(context, 3f).toFloat()
    private val borderRadius: Float = dip2px(context, 4f).toFloat()
    private val offset: Float = dip2px(context, 9.5f).toFloat()
    private var random: Random = Random()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pos: FloatArray = FloatArray(2)
    private val tan: FloatArray = FloatArray(2)
    private val list = mutableListOf<Ball>()
    private var mLength = 0f
    private var mAnimatorValue = 0f
    private val mPathMeasure: PathMeasure
    private val path: Path

    init {
        mPaint.style = Paint.Style.STROKE
        particlePaint.style = Paint.Style.FILL
        particlePaint.color = Color.parseColor("#FFF7D9")
        path = Path()
        mPathMeasure = PathMeasure()
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            mAnimatorValue = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.duration = 5000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val borderHalfHeight = borderWidth / 2f
        path.addRoundRect(
            paddingStart + borderHalfHeight + offset,
            paddingTop + borderHalfHeight + offset,
            w - borderHalfHeight - offset,
            h - borderHalfHeight - offset,
            borderRadius,
            borderRadius,
            Path.Direction.CW
        )
        mPathMeasure.setPath(path, false)
        mLength = mPathMeasure.length
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawPath(path, mPaint)
        mPathMeasure.getPosTan(mLength * mAnimatorValue, pos, tan)
        val x1 = pos[0]
        val y1 = pos[1]
        onDrawParticle(canvas, x1, y1)
        var sss = mAnimatorValue + 0.5f
        if (sss > 1) {
            sss -= 1
        }
        mPathMeasure.getPosTan(mLength * sss, pos, tan)
        val x2 = pos[0]
        val y2 = pos[1]
        onDrawParticle(canvas, x2, y2)
    }

    private fun onDrawParticle(canvas: Canvas, x: Float, y: Float) {
        for (i in 1..3) {
            val ball = Ball(borderWidth, point = Point(x.toInt(), y.toInt()))
            list.add(ball)
        }
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val ball = iterator.next()
            if (ball.radius > borderWidth / 2f) {
                particlePaint.alpha = ball.alpha
                particlePaint.shader = RadialGradient(
                    ball.point.x.toFloat(), ball.point.y.toFloat(), ball.radius,
                    particlePaint.color, Color.TRANSPARENT, Shader.TileMode.REPEAT
                )
                canvas.drawCircle(
                    ball.point.x.toFloat(),
                    ball.point.y.toFloat(),
                    ball.radius,
                    particlePaint
                )
                ball.alpha = ball.alpha - 2
                ball.radius = ball.radius - 0.05f
                val rx = randomInt(-2, 2) - ball.point.x
                val ry = randomInt(-2, 2) - ball.point.y
                ball.point.x = rx
                ball.point.y = ry
            } else {
                iterator.remove()
            }
        }
    }

    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun randomInt(start: Int = 0, end: Int): Int {
        return start + random.nextInt(end - start - 1)
    }
}


data class Ball(var radius: Float, var alpha: Int = 255, var point: Point)
