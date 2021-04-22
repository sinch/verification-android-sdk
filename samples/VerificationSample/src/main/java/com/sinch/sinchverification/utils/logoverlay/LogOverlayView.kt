package com.sinch.sinchverification.utils.logoverlay

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.sinch.sinchverification.R
import kotlinx.android.synthetic.main.view_logoverlay.view.*


class LogOverlayView @JvmOverloads internal constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "LogOverlayView"
    }

    init {
        inflate(context, R.layout.view_logoverlay, this)
        toggleOverlayButton.setOnClickListener {
            overlayRecycler.isVisible = !overlayRecycler.isVisible
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "onInterceptTouchEvent called ${super.onInterceptTouchEvent(ev)}")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent called ${super.onTouchEvent(event)}")
        return super.onTouchEvent(event)
    }
}