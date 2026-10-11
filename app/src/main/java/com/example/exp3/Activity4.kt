package com.example.exp3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 二级Activity4：秒表功能。
 * 使用 SystemClock.elapsedRealtime() 记录计时，并通过 onSaveInstanceState/onRestoreInstanceState
 * 在屏幕旋转（Activity重建）前后保存与恢复状态，保证横竖屏切换时计时连续不中断。
 */
class Activity4 : AppCompatActivity() {

    companion object {
        private const val KEY_ELAPSED_MS = "key_elapsed_ms"
        private const val KEY_RUNNING = "key_running"
        private const val KEY_START_REALTIME = "key_start_realtime"
        private const val UPDATE_INTERVAL_MS = 10L
    }

    private lateinit var tvTime: TextView
    private val handler = Handler(Looper.getMainLooper())

    /** 已累计的计时时长（毫秒） */
    private var elapsedMs = 0L

    /** 是否正在计时 */
    private var isRunning = false

    /** 本次计时的起始时刻（elapsedRealtime基准） */
    private var startRealtime = 0L

    /** 定时刷新界面的任务 */
    private val updateTask = object : Runnable {
        override fun run() {
            if (isRunning) {
                val total = elapsedMs + (SystemClock.elapsedRealtime() - startRealtime)
                tvTime.text = formatTime(total)
                handler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity4Root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvTime = findViewById(R.id.tvTime)

        // 恢复旋转前保存的状态，计时继续
        if (savedInstanceState != null) {
            elapsedMs = savedInstanceState.getLong(KEY_ELAPSED_MS, 0L)
            isRunning = savedInstanceState.getBoolean(KEY_RUNNING, false)
            startRealtime = savedInstanceState.getLong(KEY_START_REALTIME, 0L)
        }
        if (isRunning) {
            handler.post(updateTask)
        } else {
            tvTime.text = formatTime(elapsedMs)
        }
    }

    /** 开始/继续计时 */
    fun onStartClick(view: View) {
        if (!isRunning) {
            isRunning = true
            startRealtime = SystemClock.elapsedRealtime()
            handler.post(updateTask)
        }
    }

    /** 暂停计时 */
    fun onPauseClick(view: View) {
        if (isRunning) {
            elapsedMs += SystemClock.elapsedRealtime() - startRealtime
            isRunning = false
            handler.removeCallbacks(updateTask)
            tvTime.text = formatTime(elapsedMs)
        }
    }

    /** 重置秒表 */
    fun onResetClick(view: View) {
        isRunning = false
        elapsedMs = 0L
        handler.removeCallbacks(updateTask)
        tvTime.text = formatTime(0L)
    }

    /** 保存状态，用于屏幕旋转等配置变化后的恢复 */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentElapsed = if (isRunning) {
            elapsedMs + (SystemClock.elapsedRealtime() - startRealtime)
        } else {
            elapsedMs
        }
        outState.putLong(KEY_ELAPSED_MS, currentElapsed)
        outState.putBoolean(KEY_RUNNING, isRunning)
        outState.putLong(KEY_START_REALTIME, SystemClock.elapsedRealtime())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTask)
    }

    /** 将毫秒格式化为 HH:mm:ss.SSS */
    private fun formatTime(ms: Long): String {
        val hours = ms / 3_600_000
        val minutes = (ms % 3_600_000) / 60_000
        val seconds = (ms % 60_000) / 1_000
        val millis = ms % 1_000
        return getString(R.string.activity4_time_format, hours, minutes, seconds, millis)
    }
}
