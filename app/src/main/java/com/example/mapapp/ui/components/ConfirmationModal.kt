package com.example.mapapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.mapapp.ui.components.buttons.SecondaryButton
import com.example.mapapp.utils.DialogData

@Composable
fun ConfirmationModal(
    dialogData: DialogData?
) {
    if (dialogData != null && dialogData.isShowing.value) {
        AlertDialog(
            title = {
                Text(
                    text = dialogData.title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                if (dialogData.text != "") Text(
                    text = dialogData.text,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            onDismissRequest = {
                dialogData.isShowing.value = false
            },
            confirmButton = {
                SecondaryButton(
                    text = dialogData.confirmLabel,
                    backgroundColor = dialogData.confirmColor
                ) {
                    dialogData.onConfirm()
                    dialogData.isShowing.value = false
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = dialogData.dismissColor
                    ),
                    onClick = {
                        dialogData.onDismiss()
                        dialogData.isShowing.value = false
                    }
                ) {
                    Text(
                        text = dialogData.dismissLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}
