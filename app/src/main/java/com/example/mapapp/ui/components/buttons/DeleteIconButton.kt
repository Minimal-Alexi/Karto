package com.example.mapapp.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = "Delete Button",
            tint = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
    }
}