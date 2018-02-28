package ui.anwesome.com.fillrectview

/**
 * Created by anweshmishra on 28/02/18.
 */
import android.content.*
import android.graphics.*
import android.view.*
import java.util.concurrent.ConcurrentLinkedQueue

class FillRectView(ctx : Context, var n : Int = 3) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap(event.x, event.y)
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
    data class FillRect(var i : Int) {
        var x : Float = 0f
        var y : Float = 0f
        var size : Float = 0f
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            size = Math.min(w, h) / 4
            x = w / 2
            y = i * 1.5f * size + size
            canvas.save()
            canvas.translate(x, y)
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
        fun update(stopcb : (Int) -> Unit) {
            state.update {
                stopcb(i)
            }
        }
        fun startUpdate(x : Float, y : Float, startcb : () -> Unit) {
            if(x >= this.x - size/2 && x <= this.x + size/2 && y >= this.y - size/2 && y <= this.y +size/2) {
                state.startUpdating(startcb)
            }
        }
    }
    class FillRectContainer(var n : Int) {
        val fillRects : ConcurrentLinkedQueue<FillRect> = ConcurrentLinkedQueue()
        val updatingRects : ConcurrentLinkedQueue<FillRect> = ConcurrentLinkedQueue()
        init {
            for(i in 0..n-1) {
                fillRects.add(FillRect(i))
            }
        }
        fun draw(canvas : Canvas, paint : Paint) {
            fillRects.forEach {
                it.draw(canvas, paint)
            }
        }
        fun update(stopcb : () -> Unit) {
            updatingRects.forEach { rect ->
                rect.update {
                    updatingRects.remove(rect)
                    if(updatingRects.size == 0) {
                        stopcb()
                    }
                }
            }
        }
        fun handleTap(x : Float, y : Float, startcb : () -> Unit) {
            fillRects.forEach { rect ->
                rect.startUpdate(x, y, {
                    updatingRects.add(rect)
                    if(updatingRects.size == 1) {
                        startcb()
                    }
                })
            }
        }
    }
    data class Renderer(var view : FillRectView) {
        val animator = Animator(view)
        val container = FillRectContainer(view.n)
        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            container.draw(canvas, paint)
            animator.animate {
                container.update {
                    animator.stop()
                }
            }
        }
        fun handleTap(x : Float, y : Float) {
            container.handleTap(x, y) {
                animator.start()
            }
        }
    }
}