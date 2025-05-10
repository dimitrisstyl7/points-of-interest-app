package dimstyl.pointsofinterest.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldCursorColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldErrorBorderColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldErrorCursorColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldErrorLabelColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldErrorTextColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldFocusedBorderColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldFocusedLabelColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldFocusedTextColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldUnfocusedBorderColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldUnfocusedLabelColor
import dimstyl.pointsofinterest.ui.theme.OutlinedTextFieldUnfocusedTextColor

@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
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
            focusedTextColor = OutlinedTextFieldFocusedTextColor,
            unfocusedTextColor = OutlinedTextFieldUnfocusedTextColor,
            focusedBorderColor = OutlinedTextFieldFocusedBorderColor,
            unfocusedBorderColor = OutlinedTextFieldUnfocusedBorderColor,
            focusedLabelColor = OutlinedTextFieldFocusedLabelColor,
            unfocusedLabelColor = OutlinedTextFieldUnfocusedLabelColor,
            cursorColor = OutlinedTextFieldCursorColor,
            errorCursorColor = OutlinedTextFieldErrorCursorColor,
            errorTextColor = OutlinedTextFieldErrorTextColor,
            errorLabelColor = OutlinedTextFieldErrorLabelColor,
            errorBorderColor = OutlinedTextFieldErrorBorderColor

        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}