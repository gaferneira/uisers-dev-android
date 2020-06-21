package co.tuister.uisers.utils.sectionsDecorator

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlin.math.max
import kotlin.math.min

class VerticalPainter(
    private val fixLayoutSize: (View, ViewGroup) -> Unit,
    private val headerToLineOffset: Float,
    private val linePaint: Paint
) {

    fun paint(
        canvas: Canvas,
        sectionIndex: Int,
        sectionTitle: String,
        sectionsVisibleElements: List<View>,
        headerView: TextView,
        sectionSize: Int,
        parent: ViewGroup
    ) {

        val lineStart = getLineStart(sectionIndex, sectionsVisibleElements.first())
        val lineEnd = getLineEnd(sectionIndex, sectionSize, canvas, sectionsVisibleElements.last())

        if (lineEnd > lineStart) {
            canvas.drawLine(
                headerToLineOffset,
                getYStart(sectionIndex, sectionsVisibleElements.first()),
                headerToLineOffset,
                getYEnd(sectionIndex, sectionSize, canvas, sectionsVisibleElements.last()),
                linePaint
            )
        }

        headerView.apply {
            text = sectionTitle
            fixLayoutSize(this, parent)

            val headerWidth = with(this) {
                width + paddingStart + paddingEnd +
                    with(layoutParams as ViewGroup.MarginLayoutParams) {
                        marginStart + marginEnd
                    }
            }

            val startPosition = if (lineEnd - lineStart < headerWidth && sectionIndex == 0) {
                lineEnd - headerWidth
            } else {
                lineStart
            }

            drawHeader(canvas, this, startPosition)
        }
    }

    private fun getLineStart(sectionIndex: Int, first: View): Float {
        val topMargin =
            (first.layoutParams as ViewGroup.MarginLayoutParams).topMargin.toFloat()
        return if (sectionIndex == 0) {
            topMargin
        } else {
            max(first.top.toFloat(), topMargin)
        }
    }

    private fun getLineEnd(
        sectionIndex: Int,
        sectionSize: Int,
        canvas: Canvas,
        last: View
    ): Float {
        val bottomMargin =
            (last.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin.toFloat()

        return if (sectionIndex == sectionSize - 1) {
            canvas.height.toFloat() - bottomMargin
        } else {
            min(last.bottom.toFloat(), canvas.height.toFloat() - bottomMargin)
        }
    }

    private fun getYStart(sectionIndex: Int, first: View) = getLineStart(sectionIndex, first)

    private fun getYEnd(sectionIndex: Int, sectionSize: Int, canvas: Canvas, last: View) =
        getLineEnd(sectionIndex, sectionSize, canvas, last)

    private fun drawHeader(
        canvas: Canvas,
        textView: TextView,
        startPosition: Float
    ) {
        canvas.save()
        canvas.translate(0f, startPosition)
        textView.draw(canvas)
        canvas.restore()
    }

    fun getOutRect(outRect: Rect) {
        outRect.left = headerToLineOffset.toInt()
    }
}
