package se.studieresan.studs

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

fun <T: View> Activity.find(id: Int): T = findViewById(id)

fun View.show(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun EditText.onTextChange(onChange: (String) -> Unit): TextWatcher {
    val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable) = Unit

        override fun beforeTextChanged(s: CharSequence, start: Int,
                                       count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int,
                                   before: Int, count: Int) {
            onChange(s.toString())
        }
    }
    this.addTextChangedListener(textWatcher)
    return textWatcher
}
