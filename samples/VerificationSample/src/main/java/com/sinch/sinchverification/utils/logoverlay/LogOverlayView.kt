package com.sinch.sinchverification.utils.logoverlay

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sinch.sinchverification.databinding.ViewLogoverlayBinding

class LogOverlayView @JvmOverloads internal constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewLogoverlayBinding

    val overlayRecycler: RecyclerView get() = binding.overlayRecycler

    companion object {
        const val TAG = "LogOverlayView"
    }

    init {
        binding = ViewLogoverlayBinding.inflate(LayoutInflater.from(context), this)
        binding.toggleOverlayButton.setOnClickListener {
            binding.overlayRecycler.isVisible = !binding.overlayRecycler.isVisible
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