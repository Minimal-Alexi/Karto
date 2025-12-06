package com.example.mapapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun ConfirmationModal(
    title: String,
    text: String,
    confirmLabel: String = "Continue",
    dismissLabel: String = "Cancel",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    showState: MutableState<Boolean>
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = {
            showState.value = false
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    showState.value = false
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                    showState.value = false
                }
            ) {
                Text(dismissLabel)
            }
        }
    )
}
