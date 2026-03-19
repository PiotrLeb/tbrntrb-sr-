package me.piotrleb.myapplication

import RunScreen
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import me.piotrleb.myapplication.ui.theme.MyApplicationTheme

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                RunScreen()
            }
        }

    }
}