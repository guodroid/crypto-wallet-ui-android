package org.trustnote.superwallet.biz.home

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class FlingBehavior : AppBarLayout.Behavior {
    private var isPositive: Boolean = false

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        var velocityY = velocityY
        var consumed = consumed
        if (velocityY > 0 && !isPositive || velocityY < 0 && isPositive) {
            velocityY = velocityY * -1
        }
        if (target is RecyclerView && velocityY < 0) {
            val firstChild = target.getChildAt(0)
            val childAdapterPosition = target.getChildAdapterPosition(firstChild)
            consumed = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)
        isPositive = dy > 0
    }

    companion object {
        private val TOP_CHILD_FLING_THRESHOLD = 3
    }
}
