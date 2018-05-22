package com.unual.ibook

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.unual.tools.Page
import android.opengl.ETC1.getWidth
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.unual.tools.TagContent
import java.util.*


/**
 * Created by Administrator on 2018/5/22.
 */
class EpubDrawView(context: Context) : View(context) {
    fun EpubDrawView.dp2px(dpValue: Int) = dpValue * context.resources.displayMetrics.density + 0.5f
    fun EpubDrawView.px2dp(pxValue: Int) = pxValue / context.resources.displayMetrics.density + 0.5f
    fun EpubDrawView.sp2px(spValue: Int) = spValue * context.resources.displayMetrics.scaledDensity + 0.5f

    //    private var page: Page = Page()
    var tags: ArrayList<TagContent> = ArrayList()
    var infoPaint: Paint

    init {
        infoPaint = Paint()
        infoPaint.color = resources.getColor(R.color.grey_400)
        infoPaint.strokeWidth = 5f
        infoPaint.style = Paint.Style.FILL
    }

//    fun setContent(page: Page) {
//        this.page = page
//    }

    fun setContent(tags: ArrayList<TagContent>) {
        this.tags = tags
        invalidate()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }
//    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        super.onLayout(changed, left, top, right, bottom)
//    }

    var i = 0
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        drawChapterName(canvas)
        for (tag in tags) {
            i++
            if (i < 1000) continue
            when (tag.tagName) {
                "p" -> drawText(tag, canvas)
            }
            if (willBreak) break
        }
    }

    private fun drawChapterName(canvas: Canvas) {
        canvas.drawText("章节名字", 0, "章节名字".length, dp2px(10), dp2px(10), infoPaint)
    }

    var willBreak: Boolean = false
    var topMargin: Float = dp2px(25)
    private fun drawText(tag: TagContent, canvas: Canvas) {
        canvas.save()
        canvas.translate(0f, topMargin)
        val tp = TextPaint()
        tp.style = Paint.Style.FILL
        tp.color = Color.BLACK
        tp.textSize = 28f
        val message = tag.content
        val myStaticLayout = StaticLayout(message, tp, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
        myStaticLayout.draw(canvas)

        topMargin = topMargin + (myStaticLayout.getLineBottom(myStaticLayout.lineCount - 1)).toFloat()
        Log.e("TAG", "top -> $topMargin")
        if (topMargin > canvas.height) {
            willBreak = true
            Log.e("TAG", "message -> $message")
        }
        canvas.restore()
    }
}