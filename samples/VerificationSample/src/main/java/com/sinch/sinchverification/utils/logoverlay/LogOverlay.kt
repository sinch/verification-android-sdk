package com.sinch.sinchverification.utils.logoverlay

import android.Manifest
import android.Manifest.permission.SYSTEM_ALERT_WINDOW
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinch.logging.logger
import kotlinx.android.synthetic.main.view_logoverlay.view.*
import java.lang.Exception

object LogOverlay {

    private lateinit var appContext: Application
    private lateinit var overlayView: LogOverlayView
    private val overlayAdapter = LogOverlayAdapter()
    private val linearLayoutManager by lazy {
        LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false)
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val windowManager: WindowManager by lazy {
        appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val areOverlayPermissionsGranted: Boolean get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(appContext)
        } else {
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED
        }

    fun init(app: Application) {
        this.appContext = app
        this.overlayView = LogOverlayView(appContext)
    }

    fun log(tag: String, message: String, level: LogOverlayItemLevel) {
        mainThreadHandler.post {
            overlayAdapter.addItem(LogOverlayItem(tag, message, level))
            linearLayoutManager.scrollToPosition(overlayAdapter.itemCount - 1)
        }
    }

    fun show() {
        if (overlayView.parent == null) {
            attachToWindow()
            overlayView.overlayRecycler.apply {
                layoutManager = linearLayoutManager
                adapter = overlayAdapter
            }
            overlayAdapter.notifyDataSetChanged()
        }
    }

    fun hide() {
        if (overlayView.isAttachedToWindow) {
            windowManager.removeView(overlayView)
        }
    }

    private fun attachToWindow() {
        val overlayFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val windowParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            overlayFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM
        }
        if (areOverlayPermissionsGranted) {
            windowManager.addView(overlayView, windowParams)
        } else {
            logger().debug("To use the overlay log view, please grant required permissions explicitly (via app settings).")
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}