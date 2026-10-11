package com.example.exp3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** 二级Activity2：输入内容，确定后将内容返回给MainActivity */
class Activity2 : AppCompatActivity() {

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }

    private lateinit var etInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity2Root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etInput = findViewById(R.id.etInput)
    }

    /** 确定按钮：把输入内容放入Intent并设置为RESULT_OK返回 */
    fun onConfirmClick(view: View) {
        val data = Intent().apply {
            putExtra(EXTRA_RESULT, etInput.text.toString())
        }
        setResult(RESULT_OK, data)
        finish()
    }
}
