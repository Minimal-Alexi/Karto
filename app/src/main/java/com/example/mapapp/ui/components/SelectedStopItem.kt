package com.example.mapapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mapapp.ui.components.buttons.DeleteIconButton

@Composable
fun SelectedStopItem(
    index: Int,
    time: String,
    locationName: String,
    distance: String,
    duration: String,
    closingInfo: String? = null,
    placesId: String,
    navigateToLocationScreen: (String) -> Unit,
    onStayTimeChange: (String) -> Unit,
    deleteOnClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val stayTimeOptions: List<String> = listOf("15 min", "30 min", "45 min", "1 h")
    var selectedStayTime by remember { mutableStateOf(stayTimeOptions.first()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(max = 210.dp)
                    )

                    closingInfo?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Stay time selector
                    Box {
                        OutlinedTextField(
                            value = selectedStayTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Stay time") },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .clickable { expanded = true },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select stay time"
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            stayTimeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedStayTime = option
                                        onStayTimeChange(option)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "Read more about the route stop" button
                IconButton(
                    onClick = {
                        navigateToLocationScreen(placesId)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = "Read more",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp)
                    )
                }

                DeleteIconButton(
                    onClick = deleteOnClick,
                    modifier = Modifier.size(32.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}