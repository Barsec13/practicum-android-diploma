package ru.practicum.android.diploma.util

import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
class InputTextChangeHandler(){
private fun TextInputLayout.setInputStrokeColor(colorStateList: Int) {
    this.defaultHintTextColor = resources.getColorStateList(colorStateList, null)
}
 fun setInputStrokeColor(textInputLayout: TextInputLayout, text: CharSequence?) {
    if (text.isNullOrEmpty()) textInputLayout.setInputStrokeColor(R.color.hint_edit_text_empty) else textInputLayout.setInputStrokeColor(
        R.color.hint_edit_text_filed
    )
}
}