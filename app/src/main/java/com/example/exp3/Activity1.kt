package com.example.exp3

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** 二级Activity1：接收MainActivity正向传递的参数并显示 */
class Activity1 : AppCompatActivity() {

    companion object {
        const val EXTRA_PARAM = "extra_param"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity1Root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 取出MainActivity传入的参数并显示
        val param = intent.getStringExtra(EXTRA_PARAM).orEmpty()
        findViewById<TextView>(R.id.tvParam).text = getString(R.string.activity1_received_prefix) + param
    }
}
