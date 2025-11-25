package com.example.mapapp.ui.components

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel

@Composable
fun MapWrapper(viewModel: AndroidViewModel, mapInteraction: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // fixed height is important
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // wait for the first down
                        val down = awaitFirstDown(requireUnconsumed = false)
                        mapInteraction.value = true
                        // keep reading pointer events until all pointers are up
                        do {
                            val event = awaitPointerEvent()
                            // optional: you can examine event.changes to consume if needed
                        } while (event.changes.any { it.pressed })

                        mapInteraction.value = false
                    }
                }
            })
}