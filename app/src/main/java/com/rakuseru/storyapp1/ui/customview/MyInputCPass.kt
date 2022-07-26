package com.rakuseru.storyapp1.ui.customview

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rakuseru.storyapp1.R

class MyInputCPass : AppCompatEditText, View.OnTouchListener {
    var isCPassValid: Boolean = false
    private lateinit var lockIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        lockIcon = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        transformationMethod = PasswordTransformationMethod.getInstance()
        onShowVisibilityIcon(lockIcon)
    }

    private fun onShowVisibilityIcon(icon: Drawable) {
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )

    }

    private fun checkCPass() {
        val pass = text?.trim()
        if (pass.isNullOrEmpty()) {
            isCPassValid = false
            error = resources.getString(R.string.cpass_required)
        } else {
            isCPassValid = true
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        checkCPass()
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}