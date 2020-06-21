package co.tuister.uisers.utils.sectionsDecorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tuister.uisers.R
import co.tuister.uisers.utils.extensions.addToValueList
import co.tuister.uisers.utils.extensions.nullCheck2

class SectionDecorator(private val context: Context) : RecyclerView.ItemDecoration() {

    private val headerVerticalOffset by lazy {
        context.resources.getDimensionPixelSize(R.dimen.section_decorator_header_vertical_offset).toFloat()
    }

    private val linePaint = Paint()

    private var headerView: TextView? = null
    private var headerLayoutId: Int = R.layout.item_section_decorator_header

    private var painter: VerticalPainter? = null

    init {
        linePaint.color = ContextCompat.getColor(context, android.R.color.black)
        linePaint.strokeWidth =
            context.resources.getDimensionPixelSize(R.dimen.section_decorator_divider_size).toFloat()
    }

    fun setLineColor(@ColorRes color: Int) {
        linePaint.color = ContextCompat.getColor(context, color)
    }

    fun setLineWidth(width: Float) {
        linePaint.strokeWidth = width
    }

    fun setHeaderView(@LayoutRes layout: Int) {
        headerView = null
        headerLayoutId = layout
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        createPainter(parent)

        painter?.getOutRect(outRect)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val adapter = getSectionsAdapter(parent)

        createPainter(parent)

        if (headerView == null) createHeaderView(parent)

        val sectionsList = parent.asSequence()
            .map {
                Pair(adapter.getSectionTitleForPosition(parent.getChildAdapterPosition(it)), it)
            }.fold(mapOf<String, List<View>>()) { acc, data ->
                acc.addToValueList(data.first, data.second)
            }.toList()

        nullCheck2(painter, headerView) { painter, headerView ->
            sectionsList.forEachIndexed { index, (sectionTitle, sectionsVisibleElements) ->
                painter.paint(
                    canvas,
                    index,
                    sectionTitle,
                    sectionsVisibleElements,
                    headerView,
                    sectionsList.size,
                    parent
                )
            }
        }
    }

    private fun getSectionsAdapter(parent: RecyclerView): SectionsAdapterInterface {
        return parent.adapter as SectionsAdapterInterface
    }

    private fun createHeaderView(parent: RecyclerView) {
        headerView = LayoutInflater.from(parent.context)
            .inflate(headerLayoutId, parent, false) as TextView
        fixLayoutSize(headerView!!, parent)
    }

    private fun createPainter(parent: RecyclerView) {
        if (painter == null) {
            if (parent.layoutManager !is LinearLayoutManager) {
                throw IllegalArgumentException("Section decorator only works with linear layout manager")
            } else {
                painter = VerticalPainter(::fixLayoutSize, headerVerticalOffset, linePaint)
            }
        }
    }

    private fun SectionsAdapterInterface.getSectionTitleForPosition(currentPosition: Int): String {
        var count = 0
        for (i in 0..getSectionsCount()) {
            count += getItemCountForSection(i)
            if (currentPosition < count) return getSectionTitleAt(i)
        }
        throw IndexOutOfBoundsException("try to get index=$currentPosition from items lenght=$count")
    }

    /**
     * Measures the headerTitle view to make sure its size is greater than 0 and will be drawn
     * [RecyclerView item decorations](https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations)
     */
    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height
        )

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}

fun ViewGroup.asSequence(): Sequence<View> = object : Sequence<View> {

    override fun iterator(): Iterator<View> = object : Iterator<View> {
        private var nextValue: View? = null
        private var done = false
        private var position: Int = 0

        override fun hasNext(): Boolean {
            if (nextValue == null && !done) {
                nextValue = getChildAt(position)
                position++
                if (nextValue == null) done = true
            }
            return nextValue != null
        }

        override fun next(): View {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val answer = nextValue
            nextValue = null
            return answer!!
        }
    }
}
