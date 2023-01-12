package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.marginEnd
import com.lokalise.sdk.LokaliseContextWrapper
import com.lokalise.sdk.LokaliseResources
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var animatorCount = 3
    private var isAdvertClose = false
    private var mAnimatorSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testData()
        val rotateAnimation = RotateAnimation(
                0f,
                20f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                1f
        )

        val resources = LokaliseResources(this)
        val newKey = resources.getString("str_test") ?: ""

        Log.e("newKey", newKey)
//        rotateAnimation.setInterpolator(CycleInterpolator(1f))//周期插值器
//        rotateAnimation.setRepeatCount(2)//重复次数，不停止
//        rotateAnimation.setDuration(300)
//        testView3.setAnimation(rotateAnimation)
        startAnimator(testView3)

        advert_button.setOnClickListener {
            if (isAdvertClose) {
                advertInAnimation(advert_test)
            } else {
                advertOutAnimation(advert_test)
            }
            isAdvertClose = !isAdvertClose
        }
        var index = 0
        val data = getPreviewData()
        var diff = 1
        pathAnimView.setOnClickListener {
            startScaleAnimator(advert_test222)
            if (index <= 0) {
                diff = 1
            } else if (index >= data.size - 1) {
                diff = -1
            }
            index += diff
            index = index.coerceAtMost(data.size - 1)
            rewardsProgressView.scrollToPoint(data[index].value)
        }
        advert_test.setOnClickListener {
            Toast.makeText(this, "点击了广告", Toast.LENGTH_SHORT).show()
        }
        rewardsProgressView.post {
            rewardsProgressView.setData(data)
            rewardsProgressView.setProgress(15)
        }
        advert_test222.post {
            startScaleAnimator(advert_test222)
        }
        Log.e("科学计数法", ("+1.23400E-03".toDoubleOrNull()).toString() + "+1.23400E-03".isNumber())
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LokaliseContextWrapper.wrap(newBase))
    }

    private fun startScaleAnimator(view: View) {
        val animatorSet = AnimatorSet() //组合动画
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.03f, 0.97f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1.03f, 0.97f, 1f)
        animatorSet.duration = 500
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.play(scaleX).with(scaleY) //两个动画同时开始
        animatorSet.start()
    }

    private fun startAnimator(view: View) {
        if (animatorCount > 0) {
            val animator =
                    ObjectAnimator.ofFloat(view, "rotation", 0f, -20f, 0f, 20f, 0f, -20f, 0f, 20f, 0f)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.duration = 600
            animator.start()
            view.postDelayed({
                startAnimator(view)
            }, 1000)
        }
        animatorCount--
    }

    private fun getPreviewData(): ArrayList<RewardsProgressView.PointData> {
        val list = ArrayList<RewardsProgressView.PointData>()
        list.add(RewardsProgressView.PointData(10, "Cumulative purchase quantity"))
        list.add(RewardsProgressView.PointData(20, "Cumulative purchase quantity"))
        list.add(RewardsProgressView.PointData(30, "Cumulative purchase quantity"))
        list.add(RewardsProgressView.PointData(40, "Cumulative purchase quantity"))
        list.add(RewardsProgressView.PointData(50, "Cumulative purchase quantity"))
        return list
    }

    private fun testData() {
        val data: ArrayList<HotCommentView.BaseBean> = ArrayList()
        data.add(HotCommentView.TestBean("111111111111111"))
        data.add(TestBean("222222222"))
        data.add(TestBean("33333333333333333333"))
        data.add(TestBean("44444444444444444444444"))
        data.add(TestBean("555555"))
        data.add(TestBean("6666"))
        data.add(TestBean("777777777777777777777"))
        data.add(TestBean("8888888888888888888888888888888888"))
        data.add(TestBean("9999999999999"))
        testView.setData(data)
    }

    class TestBean(private val content: String) : HotCommentView.BaseBean {
        override fun getContent(): String {
            return content
        }

        override fun getUrl(): String {
            return ""
        }

    }

    override fun onResume() {
        super.onResume()
        testView.startFlipping()
        flipAnimation()
        advertRemindAnimation(advert_test)
    }

    override fun onPause() {
        super.onPause()
        testView.stopFlipping()
    }

    private fun flipAnimation() {
        val outAnimator =
                ObjectAnimator.ofFloat(box_back, View.ROTATION_Y, 0f, 90f).setDuration(500)
        val inAnimator =
                ObjectAnimator.ofFloat(box_positive, View.ROTATION_Y, -90f, 0f).setDuration(500)

        outAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                inAnimator.start()
                box_back.visibility = View.INVISIBLE
            }

        })

        inAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                box_positive.visibility = View.VISIBLE
            }
        })
        outAnimator.start()
    }

    private fun advertRemindAnimation(view: View) {
        view.post {
            if (mAnimatorSet?.isRunning == true) mAnimatorSet?.end()
            mAnimatorSet = AnimatorSet().apply {
                val translationX = view.width.toFloat() + view.marginEnd
                val translation1 =
                        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, translationX).setDuration(0)
                val translation2 =
                        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f).setDuration(500)
                val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(500)
                translation1.doOnStart {
                    view.visibility = View.INVISIBLE
                }
                translation1.doOnEnd {
                    view.visibility = View.VISIBLE
                    view.alpha = 0f
                }
                play(translation1).before(translation2).with(alpha).after(1000)
                start()
            }
            mAnimatorSet?.doOnEnd {
                mAnimatorSet = AnimatorSet().apply {
                    val shake = ObjectAnimator.ofFloat(
                            view,
                            View.ROTATION,
                            0f,
                            -15f,
                            0f,
                            15f,
                            0f,
                            -15f,
                            0f,
                            15f,
                            0f
                    )
                    shake.interpolator = AccelerateDecelerateInterpolator()
                    shake.duration = 600
                    val flip = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 360f).setDuration(500)
                    play(shake).before(flip).after(800)
                    start()
                }
            }
        }
    }

    private fun advertInAnimation(view: View) {
        view.post {
            if (mAnimatorSet?.isRunning == true) return@post
            mAnimatorSet = AnimatorSet().apply {
                val shake = ObjectAnimator.ofFloat(view, View.ROTATION, 0f).setDuration(300)
                val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(300)
                val translation =
                        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f).setDuration(500)
                play(shake).with(alpha).after(translation)
                start()
            }
        }
    }

    private fun advertOutAnimation(view: View) {
        view.post {
            if (mAnimatorSet?.isRunning == true) return@post
            mAnimatorSet = AnimatorSet().apply {
                val translationX = view.width.toFloat() * 0.8f + view.marginEnd
                val shake = ObjectAnimator.ofFloat(view, View.ROTATION, -15f).setDuration(300)
                val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.5f).setDuration(300)
                val translation =
                        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, translationX).setDuration(500)
                play(shake).with(alpha).before(translation)
                start()
            }
        }
    }
}

/**
 * 判断是否是数字,包含小数，和科学计数法
 */
fun String?.isNumber(): Boolean {
    return if (this.isNullOrEmpty()) {
        false
    } else {
        this.toDoubleOrNull() != null
    }
}
