package com.motorola.cn.flowlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

class  FlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :ViewGroup(context,attrs,defStyleAttr) {

    private var allLines:MutableList<MutableList<View>> = ArrayList()
    private var lineHeights:MutableList<Int> = ArrayList()
    private val mHorizontalSpacing = 20;
    private val mVerticalSpacing = 20;

    private fun clearMeasureParams(){
        allLines.clear()
        lineHeights.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        clearMeasureParams()

        var childCount = childCount
        var paddingLeft = paddingLeft
        var paddingRight = paddingRight
        var paddingTop = paddingTop
        var paddingBottom = paddingBottom

        var selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        var selfHeight = MeasureSpec.getSize(heightMeasureSpec)

        var widthMode1 = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode1 = MeasureSpec.getMode(heightMeasureSpec)

        var lineViews:MutableList<View> = ArrayList()
        var lineWidthUsed = 0
        var lineHeight = 0

        var parentNeededWidth = 0
        var parentNeededHeight = 0

        for(index in 0 until childCount){
            var childView = getChildAt(index)
            var childLP = childView.layoutParams
            if(childView.visibility != View.GONE){
                var childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,paddingLeft+paddingRight,childLP.width)
                var childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,paddingTop+paddingBottom,childLP.height)
                childView.measure(childWidthMeasureSpec,childHeightMeasureSpec)

                var childMesauredWidth = childView.measuredWidth
                var childMeasuredHeight = childView.measuredHeight

                if(childMesauredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth){
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)

                    parentNeededHeight += lineHeight + mVerticalSpacing
                    parentNeededWidth = Math.max(parentNeededWidth,lineWidthUsed+mHorizontalSpacing)

                    lineViews = ArrayList()
                    lineWidthUsed = 0
                    lineHeight = 0;
                }

                lineViews.add(childView)
                lineWidthUsed += childMesauredWidth + mHorizontalSpacing
                lineHeight = Math.max(lineHeight,childMeasuredHeight)

                if(index == childCount - 1){
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    parentNeededWidth = Math.max(parentNeededWidth,lineWidthUsed+mHorizontalSpacing)
                }
            }
        }

        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var realWidth = if(widthMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
        var realHeight = if(heightMode == MeasureSpec.EXACTLY) selfHeight else parentNeededHeight
        setMeasuredDimension(realWidth,realHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val lineCount = allLines.size

        var curL = paddingLeft
        var curT = paddingTop

        for(index in 0 until lineCount){
            val lineViews = allLines[index]

            val lineHeight = lineHeights[index]
            for(i in 0 until lineViews.size){
                val view = lineViews[i]
                val left = curL
                val top = curT

                val right = left + view.measuredWidth
                val bottom = top + view.measuredHeight
                view.layout(left,top,right,bottom)
                curL = right + mHorizontalSpacing
            }
            curT += curT + lineHeight + mVerticalSpacing
            curL = paddingLeft
        }
    }
}