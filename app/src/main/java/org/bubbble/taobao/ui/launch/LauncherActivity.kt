package org.bubbble.taobao.ui.launch

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import org.bubbble.taobao.ui.main.MainActivity
import org.bubbble.taobao.R

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // 如果没有启动广告则启动Activity
        startActivity(Intent(this, MainActivity::class.java))

        // 防止windowBackground闪烁
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 200)
    }
}