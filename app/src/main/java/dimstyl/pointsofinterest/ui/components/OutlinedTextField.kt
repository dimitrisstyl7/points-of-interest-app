package dimstyl.pointsofinterest.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dimstyl.pointsofinterest.ui.theme.DialogTextFieldFocusedBorderColor
import dimstyl.pointsofinterest.ui.theme.DialogTextFieldFocusedLabelColor
import dimstyl.pointsofinterest.ui.theme.DialogTextFieldFocusedTextColor
import dimstyl.pointsofinterest.ui.theme.DialogTextFieldUnfocusedBorderColor
import dimstyl.pointsofinterest.ui.theme.DialogTextFieldUnfocusedLabelColor
import dimstyl.pointsofinterest.ui.theme.DialogTextFieldUnfocusedTextColor

@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    ),
    keyboardActions: KeyboardActions
) {
    androidx.compose.material3.OutlinedTextField(
        modifier = modifier,
        label = { Text(label) },
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = singleLine,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = DialogTextFieldFocusedTextColor,
            unfocusedTextColor = DialogTextFieldUnfocusedTextColor,
            focusedBorderColor = DialogTextFieldFocusedBorderColor,
            unfocusedBorderColor = DialogTextFieldUnfocusedBorderColor,
            focusedLabelColor = DialogTextFieldFocusedLabelColor,
            unfocusedLabelColor = DialogTextFieldUnfocusedLabelColor
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}