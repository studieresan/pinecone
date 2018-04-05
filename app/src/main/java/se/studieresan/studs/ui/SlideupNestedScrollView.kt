package se.studieresan.studs.ui

import android.content.Context
import android.graphics.Rect
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import se.studieresan.studs.R

class SlideupNestedScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private val viewRect = Rect()
    private val viewLocation = IntArray(2)

    // The gradient above the content
    private val gradient: View by lazy { findViewById<View>(R.id.scrim) }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        // Disregard touch events to the gradient, intercept all others
        val contents = gradient
        contents.getDrawingRect(viewRect)
        contents.getLocationOnScreen(viewLocation)
        viewRect.offset(viewLocation[0], viewLocation[1])
        super.onTouchEvent(event)
        val b = viewRect.contains(event.rawX.toInt(), event.rawY.toInt())
        return !b
    }
}
