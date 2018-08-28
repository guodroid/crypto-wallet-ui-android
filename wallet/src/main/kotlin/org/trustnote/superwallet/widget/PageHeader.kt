package org.trustnote.superwallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import org.trustnote.superwallet.R

open class PageHeader @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var closeAction: () -> Unit = {}

    init {
        val view = View.inflate(context, R.layout.l_page_head, null)
        addView(view)

        val title = findViewById<TextView>(R.id.title__)
        val titleMiddle = findViewById<TextView>(R.id.title_middle)
        val icon = findViewById<ImageView>(R.id.icon)

        val a = context.obtainStyledAttributes(attrs, R.styleable.PageHeader)

        title.text = a.getString(R.styleable.PageHeader_ttt_title)
        titleMiddle.text = a.getString(R.styleable.PageHeader_ttt_title_middle)
        icon.setImageDrawable(a.getDrawable(R.styleable.PageHeader_ttt_icon))

        findViewById<View>(R.id.ic_dialog_close).setOnClickListener {
            closeAction.invoke()
        }

    }

    fun hideCloseBtn() {
        findViewById<View>(R.id.ic_dialog_close)?.visibility = View.INVISIBLE
    }

}


