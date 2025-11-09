package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mapapp.R

@Composable
fun BackButton(onClick : () -> Unit) {
    FilledIconButton(onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            painterResource(R.drawable.arrow_icon_left),
            contentDescription = "arrow left"
        )
    }
}