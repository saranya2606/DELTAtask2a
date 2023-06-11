package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

private lateinit var mediaPlayer: MediaPlayer
private lateinit var mediaPlayer2: MediaPlayer


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val start=findViewById<ImageView>(R.id.iv1)
        mediaPlayer = MediaPlayer.create(this, R.raw.colorful)

        mediaPlayer.start()
        start.setOnClickListener{
            val intent=Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }












    }
}

