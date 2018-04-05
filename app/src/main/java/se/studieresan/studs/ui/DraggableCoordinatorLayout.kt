package se.studieresan.studs.ui

import android.content.Context
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import se.studieresan.studs.R

class DraggableCoordinatorLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        val TAG = DraggableCoordinatorLayout::class.simpleName
    }

    val map: View by lazy { findViewById<View>(R.id.map_holder) }

    val scrollview: NestedScrollView by lazy { findViewById<NestedScrollView>(R.id.scrollview) }

    var focusListener: MapFocusListener? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        map.setOnClickListener {
            focusListener?.onMapFocus()
            scrollview
                    .animate()
                    .translationY(1000.0f)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .setDuration(300L)
                    .start()
        }
    }

    interface MapFocusListener {
        fun onMapFocus()
    }

}
