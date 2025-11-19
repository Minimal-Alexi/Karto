package com.example.mapapp.ui.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mapapp.R

import androidx.compose.material3.IconButtonDefaults

@Composable
fun BackButton(onClick : () -> Unit, modifier: Modifier = Modifier) {
    FilledIconButton(onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(
            painterResource(R.drawable.arrow_icon_left),
            contentDescription = "arrow left"
        )
    }
}