package com.example.exp3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etParam: EditText
    private lateinit var tvResult: TextView

    /** 注册用于接收二级Activity2返回结果的启动器 */
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringExtra(Activity2.EXTRA_RESULT).orEmpty()
                tvResult.text = getString(R.string.main_result_prefix) + data
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etParam = findViewById(R.id.etParam)
        tvResult = findViewById(R.id.tvResult)
    }

    /** 按钮1：正向传参启动二级Activity1，参数为输入框内容 */
    fun onForwardClick(view: View) {
        val intent = Intent(this, Activity1::class.java).apply {
            putExtra(Activity1.EXTRA_PARAM, etParam.text.toString())
        }
        startActivity(intent)
    }

    /** 按钮2：启动二级Activity2，等待返回结果 */
    fun onBackClick(view: View) {
        startForResult.launch(Intent(this, Activity2::class.java))
    }

    /** 按钮3：启动二级Activity3 */
    fun onCameraClick(view: View) {
        startActivity(Intent(this, Activity3::class.java))
    }

    /** 按钮4：启动二级Activity4 */
    fun onStopwatchClick(view: View) {
        startActivity(Intent(this, Activity4::class.java))
    }
}
