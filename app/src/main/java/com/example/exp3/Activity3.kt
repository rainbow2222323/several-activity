package com.example.exp3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

/** 二级Activity3：采用隐式Intent调用系统相机拍照，拍照成功后在界面中显示照片 */
class Activity3 : AppCompatActivity() {

    private lateinit var ivPhoto: ImageView

    /** 拍照后用于保存照片的Uri */
    private var photoUri: Uri? = null

    /** 拍照结果回调 */
    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // 拍照成功，在ImageView中显示照片
                photoUri?.let { ivPhoto.setImageURI(it) }
            } else {
                Toast.makeText(this, R.string.activity3_capture_cancelled, Toast.LENGTH_SHORT).show()
            }
        }

    /** 权限申请结果回调 */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) {
                capturePhoto()
            } else {
                Toast.makeText(this, R.string.activity3_permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity3Root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ivPhoto = findViewById(R.id.ivPhoto)
    }

    /** 拍照按钮：检查权限后隐式调用系统相机 */
    fun onCaptureClick(view: View) {
        if (hasCapturePermission()) {
            capturePhoto()
        } else {
            requestPermissionLauncher.launch(requiredPermissions())
        }
    }

    /** 隐式Intent启动系统相机 */
    private fun capturePhoto() {
        // 先在隐式Intent中确认存在可用的相机应用
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (captureIntent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, R.string.activity3_no_camera_app, Toast.LENGTH_SHORT).show()
            return
        }

        // 创建照片文件并通过FileProvider生成可共享的Uri
        val imageDir = File(cacheDir, "images").apply { mkdirs() }
        val imageFile = File(imageDir, "IMG_" + System.currentTimeMillis() + ".jpg")
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
        photoUri = uri

        // TakePicture合约内部会以隐式Intent方式启动系统相机，并把照片写入该Uri
        takePictureLauncher.launch(uri)
    }

    /** 是否已具备拍照所需权限 */
    private fun hasCapturePermission(): Boolean = requiredPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    /** 拍照所需权限：相机权限，以及低版本系统写外部存储的权限 */
    private fun requiredPermissions(): Array<String> {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions.toTypedArray()
    }
}
