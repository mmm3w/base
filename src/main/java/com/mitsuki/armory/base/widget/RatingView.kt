package com.mitsuki.armory.base.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.mitsuki.armory.base.R
import com.mitsuki.armory.base.extend.dp2px
import kotlin.math.floor
import kotlin.math.min

class RatingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var baseSize = dp2px(16f)
        set(value) {
            if (field != value) {

                borderDrawable?.setBounds(0, 0, value.toInt(), value.toInt())
                halfDrawable?.setBounds(0, 0, value.toInt(), value.toInt())
                solidDrawable?.setBounds(0, 0, value.toInt(), value.toInt())

                field = value
                postInvalidate()
            }
        }

    var intervalPadding = dp2px(2f)
        set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }

    var maxRating = 5
        set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }

    var rating = -1f
        set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }

    var adaptive = true
        set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }

    var borderDrawable = obtainDrawable(R.drawable.ic_baseline_star_border_16)
    var halfDrawable = obtainDrawable(R.drawable.ic_baseline_star_half_16)
    var solidDrawable = obtainDrawable(R.drawable.ic_baseline_star_16)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                if (adaptive) {
                    baseSize =
                        (widthSize - paddingLeft - paddingRight - (maxRating - 1) * intervalPadding) / maxRating
                }
                widthSize
            }
            else -> baseSize * maxRating + (maxRating - 1) * intervalPadding + paddingLeft + paddingRight
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                if (adaptive) {
                    baseSize = min((heightSize - paddingTop - paddingBottom).toFloat(), baseSize)
                }
                heightSize
            }
            else -> baseSize + paddingTop + paddingBottom
        }

        borderDrawable?.setBounds(0, 0, baseSize.toInt(), baseSize.toInt())
        halfDrawable?.setBounds(0, 0, baseSize.toInt(), baseSize.toInt())
        solidDrawable?.setBounds(0, 0, baseSize.toInt(), baseSize.toInt())

        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            val saved = save()
            translate(paddingLeft.toFloat(), paddingTop.toFloat())
            for (i in 0 until maxRating) {
                if (rating < 0) {
                    borderDrawable?.draw(this)
                    translate((baseSize + intervalPadding).toFloat(), 0f)
                    continue
                }

                (rating - i).let {
                    if (it >= 0.75) {
                        solidDrawable?.draw(this)
                        translate((baseSize + intervalPadding).toFloat(), 0f)
                    } else if (it < 0.75 && it >= 0.25) {
                        halfDrawable?.draw(this)
                        translate((baseSize + intervalPadding).toFloat(), 0f)
                    } else {
                        borderDrawable?.draw(this)
                        translate((baseSize + intervalPadding).toFloat(), 0f)
                    }
                }
            }
            restoreToCount(saved)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var handle = super.onTouchEvent(event)

        if (isEnabled) {
            with(event.x - paddingLeft) {
                if (this > 0) {
                    val i = floor(this / (baseSize + intervalPadding)).toInt()
                    val remainder = this % (baseSize + intervalPadding)
                    rating = if (remainder > baseSize / 2) i + 1f else i + 0.5f
                    handle = true
                }
            }
        }

        return handle
    }

    fun setDrawable(
        @DrawableRes solid: Int = R.drawable.ic_baseline_star_16,
        @DrawableRes half: Int = R.drawable.ic_baseline_star_half_16,
        @DrawableRes border: Int = R.drawable.ic_baseline_star_border_16
    ) {
        this.solidDrawable = obtainDrawable(solid)
        this.halfDrawable = obtainDrawable(half)
        this.borderDrawable = obtainDrawable(border)
    }

    private fun obtainDrawable(@DrawableRes id: Int): Drawable? =
        ResourcesCompat.getDrawable(resources, id, null)
}