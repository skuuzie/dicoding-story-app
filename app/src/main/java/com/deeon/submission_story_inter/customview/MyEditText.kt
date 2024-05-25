package com.deeon.submission_story_inter.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.deeon.submission_story_inter.R
import java.util.regex.Pattern

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var contentType: Int = 0

    enum class ContentType {
        Normal,
        Email,
        Password
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyEditText,
            0, 0
        ).apply {
            try {
                contentType = getInteger(R.styleable.MyEditText_contentType, 0)
            } finally {
                recycle()
            }
        }

        val emailRegex = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,3}$")

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (contentType) {
                    ContentType.Email.ordinal -> {
                        if (!emailRegex.matcher(s).matches()) {
                            error = context.getString(R.string.input_email_error)
                        } else {
                            setError(null, null)
                        }
                    }

                    ContentType.Password.ordinal -> {
                        if (s.toString().length < 8) {
                            error = context.getString(R.string.input_password_error)
                        } else {
                            setError(null, null)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
}