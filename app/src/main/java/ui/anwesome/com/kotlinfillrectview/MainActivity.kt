package ui.anwesome.com.kotlinfillrectview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.fillrectview.FillRectView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FillRectView.create(this)
    }
}
