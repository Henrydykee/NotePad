package com.example.notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        val progress_bar = findViewById<ProgressBar>(R.id.progress_bar)

        //4second splash time
        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@MainActivity, ShelfActivity::class.java))
            //finish this activity
            finish()
            progress_bar.visibility= View.GONE
        },4000)
    }
}
