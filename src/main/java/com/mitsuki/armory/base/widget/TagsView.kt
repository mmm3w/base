package com.mitsuki.armory.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.mitsuki.armory.base.extend.marginHorizontal
import com.mitsuki.armory.base.extend.marginVertical

class TagsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    //xml添加的view会从这里获取layoutParams
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams? {
        return MarginLayoutParams(context, attrs)
    }

    //要写就写全套算了
    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    //根据addInnerView方法中的调用，强行执行generateLayoutParams方法
    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    //将子view的params全都转换为MarginLayoutParams
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return p?.run { MarginLayoutParams(this) }
            ?: MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (childCount < 1) return

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //在EXACTLY模式下直接确定大小
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            (0 until childCount).any {
                getChildAt(it).run {
                    measureChildWithMargins(this, widthMeasureSpec, 0, heightMeasureSpec, 0)
                    false
                }
            }
            setMeasuredDimension(widthSize, heightSize)
            return
        }

        //先测定第一个view
        val firstView = getChildAt(0).also {
            measureChildWithMargins(
                it, widthMeasureSpec, 0,
                heightMeasureSpec, 0
            )
        }

        //除去第一个view，其他view尺寸限制范围
        val limitedWidth = when (widthMode) {
            MeasureSpec.UNSPECIFIED -> -1
            else -> (widthSize - firstView.measuredWidth - firstView.marginHorizontal() - paddingStart - paddingEnd)
                .coerceAtLeast(0)
        }
        val limitedHeight = when (heightMode) {
            MeasureSpec.UNSPECIFIED -> -1
            else -> (heightSize - firstView.measuredHeight - firstView.marginVertical() - paddingBottom - paddingTop)
                .coerceAtLeast(0)
        }

        var tempWidth = 0
        var tempHeight = 0
        var currentWidth = 0
        var currentHeight = 0

        val newWidthMeasureSpec = if (limitedWidth > -1) MeasureSpec.makeMeasureSpec(
            limitedWidth,
            widthMode
        ) else widthMeasureSpec

        for (i in 1 until childCount) {
            getChildAt(i).run {
                measureChildWithMargins(this, newWidthMeasureSpec, 0, heightMeasureSpec, 0)
                when (widthMode) {
                    MeasureSpec.EXACTLY -> {
                        //tempHeight用于存储当前行最大的view的高度，是作为一个预计累加的高度

                        //累加宽度值
                        tempWidth += measureWidthWithMargin()
                        //判断累加值有没有超过限定的宽度
                        if (tempWidth > limitedWidth) {
                            //该view属于下一行
                            //重置临时参数
                            tempWidth = measureWidthWithMargin()  //用于保证下次累加判断
                            tempHeight = 0  //因为换行了，所以当前行的最高高度清零
                        } else {
                            //该view属于本行
                            //先减去预先增加的高度
                            //因为temp初始值为0,表示首次到此处是预加高度为0
                            currentHeight -= tempHeight
                        }

                        //拿到当前行需要预加的最大高度
                        tempHeight = tempHeight.coerceAtLeast(measureHeightWithMargin())
                        //加上预计高度
                        currentHeight += tempHeight
                    }
                    MeasureSpec.AT_MOST -> {
                        //获取最宽的view
                        currentWidth = currentWidth
                            .coerceAtLeast(measureWidthWithMargin())
                            .coerceAtMost(limitedWidth)

                        //判断空余是否够塞下view
                        if (measureWidthWithMargin() > tempWidth) {
                            //view去下一行
                            tempWidth = currentWidth
                            tempHeight = tempHeight.coerceAtLeast(measureHeightWithMargin())
                            currentHeight += tempHeight
                            tempHeight = 0
                        } else {
                            //view留在本行
                            tempWidth -= measureWidthWithMargin()
                            currentHeight -= tempHeight
                            tempHeight = tempHeight.coerceAtLeast(measureHeightWithMargin())
                            currentHeight += tempHeight
                        }
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        currentWidth += measureWidthWithMargin()
                        currentHeight = currentHeight.coerceAtLeast(measureHeightWithMargin())
                    }
                }
            }
        }

        val resultWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
                .coerceAtMost(
                    currentWidth +
                            firstView.measureWidthWithMargin() + paddingStart + paddingEnd
                )
            else -> currentWidth + firstView.measureWidthWithMargin() + paddingStart + paddingEnd
        }

        val resultHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> heightSize
                .coerceAtMost(currentHeight + paddingTop + paddingBottom)
                .coerceAtMost(limitedHeight)
            else -> currentHeight.coerceAtLeast(firstView.measureHeightWithMargin()) + paddingTop + paddingBottom
        }

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        if (childCount < 1) return

        val originL = paddingStart
        val originT = paddingTop

        //直接先布局第一个view
        val firstChild = getChildAt(0)
        firstChild.layout(
            originL + firstChild.marginStart,
            originT + firstChild.marginTop,
            originL + firstChild.marginStart + firstChild.measuredWidth,
            originT + firstChild.marginTop + firstChild.measuredHeight
        )

        val startLeft = originL + firstChild.measureWidthWithMargin()
        val maxWidth = width - paddingEnd
        var leftOffset = 0
        var topOffset = originT

        var tempHeight = 0

        for (i in 1 until childCount) {
            getChildAt(i).run {
                if (leftOffset != 0 && startLeft + leftOffset + measureWidthWithMargin() > maxWidth) {
                    //该到下一行了
                    leftOffset = 0
                    topOffset += tempHeight
                    tempHeight = 0
                }
                layout(
                    startLeft + leftOffset + marginStart,
                    topOffset + marginTop,
                    startLeft + leftOffset + marginStart + measuredWidth,
                    topOffset + marginTop + measuredHeight
                )
                leftOffset += measureWidthWithMargin()
                tempHeight = tempHeight.coerceAtLeast(measureHeightWithMargin())
            }
        }
    }


    private fun View.measureWidthWithMargin(): Int {
        return measuredWidth + marginStart + marginEnd
    }

    private fun View.measureHeightWithMargin(): Int {
        return measuredHeight + marginTop + marginBottom
    }
}