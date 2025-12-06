package com.example.mapapp.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

data class DialogData(
    val title: String,
    val text: String = "",

    val confirmLabel: String = "Continue",
    val onConfirm: () -> Unit = { isShowing.value = false },
    val confirmColor: Color = Color.Unspecified,

    val dismissLabel: String = "Cancel",
    val onDismiss: () -> Unit = { isShowing.value = false },
    val dismissColor: Color = Color.Unspecified,

    val isShowing: MutableState<Boolean>
)