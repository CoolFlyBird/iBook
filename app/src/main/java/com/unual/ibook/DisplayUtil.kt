package com.unual.ibook

import android.content.Context

/**
 * Created by Administrator on 2018/5/22.
 */
class DisplayUtil {
    companion object {
        /**
         * dp转px
         *
         * @param context context
         * @param dpValue dp
         * @return px
         */
        fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * px转dp
         *
         * @param context context
         * @param pxValue px
         * @return dp
         */
        fun px2dp(context: Context, pxValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxValue / scale + 0.5f
        }

        /**
         * px转dp
         *
         * @param context context
         * @param spValue sp
         * @return dp
         */
        fun sp2px(context: Context, spValue: Int): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }
}