package nl.pindab0ter.edinburghinternationalfilmfestival.utilities

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ListView

class ResizableListView(context: Context, attributeSet: AttributeSet) : ListView(context, attributeSet) {
    private var oldCount = 0

    override fun onDraw(canvas: Canvas) {
        if (count != oldCount) {
            val itemHeight = getChildAt(0).height + 1
            oldCount = count
            layoutParams = layoutParams.apply { height = count * itemHeight }
        }

        super.onDraw(canvas)
    }
}