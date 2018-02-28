package ui.anwesome.com.fillrectview

/**
 * Created by anweshmishra on 28/02/18.
 */
import android.content.*
import android.graphics.*
import android.view.*
class FillRectView(ctx : Context) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas : Canvas) {

    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}