package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.ui.components.ConfirmationModal
import com.example.mapapp.ui.components.Placeholder
import com.example.mapapp.ui.components.SavedRouteCard
import com.example.mapapp.utils.DialogData
import com.example.mapapp.viewmodel.SavedViewModel

@Composable
fun SavedScreen(onOpenRoute: (Int) -> Unit) {
    val savedViewModel = viewModel<SavedViewModel>()
    val dialogDataState = savedViewModel.dialogDataState
    val templates by savedViewModel.savedTemplates.collectAsState()

    ConfirmationModal(dialogDataState.value)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Saved Routes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp)
        )

        val errorColor = MaterialTheme.colorScheme.error

        if (templates.isNotEmpty()) {
            templates.forEach { template ->
                SavedRouteCard(
                    template = template,
                    onOpen = { onOpenRoute(template.id) },
                    onDelete = {
                        dialogDataState.value =
                            DialogData(
                                title = "Delete this Saved Route?",
                                text = "If you delete it, it's gone forever.",
                                onConfirm = { savedViewModel.deleteTemplate(template.id) },
                                confirmColor = errorColor,
                                confirmLabel = "Delete",
                                isShowing = mutableStateOf(true)
                            )
                    }
                )
            }
        } else {
            Placeholder("You don't have any saved routes yet. When you save a route, it will appear here.")
        }

        Spacer(modifier = Modifier.height(0.dp))
    }
}