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
    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if(animated) {
                updatecb?.invoke()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class State(var prevScale : Float = 0f, var j : Int = 0, var dir : Float = 0f) {
        var scales : Array<Float> = arrayOf(0f, 0f)
        fun update(stopcb : () -> Unit) {
            scales[j] += 0.1f * dir
            if(Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j++
                if(j == scales.size) {
                    j = 0
                    dir = 0f
                    stopcb()
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                dir = 1f
                scales = arrayOf(0f, 0f)
                startcb()
            }
        }
    }
    data class FillRect(var w : Float, var h : Float) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            val size = Math.min(w, h) / 3
            canvas.save()
            canvas.translate(w / 2, h / 2)
            canvas.rotate(180f * state.scales[0])
            paint.color = Color.GRAY
            canvas.drawRect(RectF(-size / 2, - size / 2, size / 2, size / 2 ), paint)
            paint.color = Color.BLUE
            val y = -(size / 2) * state.scales[1]
            canvas.save()
            canvas.translate(0f, y)
            canvas.drawRect(RectF(-size / 2, 0f , size / 2, size / 2), paint)
            canvas.restore()
            canvas.restore()
        }
        fun update(stopcb : () -> Unit) {
            state.update(stopcb)
        }
        fun startUpdate(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
}