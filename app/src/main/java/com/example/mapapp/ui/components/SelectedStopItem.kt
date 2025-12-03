package com.example.mapapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mapapp.ui.components.buttons.DeleteIconButton

@Composable
fun SelectedStopItem(
    index: Int,
    locationName: String,
    distance: String,
    duration: String,
    closingInfo: String? = null,
    placesId: String,
    navigateToLocationScreen: (String) -> Unit,
    onStayTimeChange: (String) -> Unit,
    deleteOnClick: () -> Unit,
    isFirst: Boolean = false, // New parameter to disable Up button
    isLast: Boolean = false,  // New parameter to disable Down button
    onMoveUp: () -> Unit,     // New callback
    onMoveDown: () -> Unit,   // New callback
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /** reorder buttons */
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.1f)
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isFirst,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move Up"
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move Down"
                    )
                }
            }

            /** track line test */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .weight(1f)
                        .background(color = MaterialTheme.colorScheme.primary)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .weight(1f)
                        .background(color = if (isLast) Color.Transparent else MaterialTheme.colorScheme.primary)
                )
            }

            /** duration and distance + location name */
            Column(
                modifier = Modifier
                    .weight(0.7f)
            ) {
                Row {
                    Column(modifier = Modifier.offset(y = (-36).dp)) {
                        Text(
                            text = distance,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                        Text(
                            text = duration,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                    }

                    Text(
                        text = locationName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.offset(x = (if (distance == "") (0) else (-14)).dp)
                    )
                }
            }

            /** buttons */
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
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SelectedStopItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // State 1: Normal
            SelectedStopItem(
                index = 0,
                locationName = "Central Park",
                distance = "2.5 km",
                duration = "15s",
                closingInfo = null,
                placesId = "place_1",
                navigateToLocationScreen = {},
                onStayTimeChange = {},
                deleteOnClick = {},
                isFirst = true,
                isLast = false,
                onMoveUp = {},
                onMoveDown = {},
            )

            Spacer(modifier = Modifier.height(16.dp))

            // State 2: With Closing Info and Longer Text
            SelectedStopItem(
                index = 1,
                locationName = "The National Museum of Natural History",
                distance = "12 km",
                duration = "450s",
                closingInfo = "",
                placesId = "place_2",
                navigateToLocationScreen = {},
                onStayTimeChange = {},
                deleteOnClick = {},
                isFirst = false,
                isLast = true,
                onMoveUp = {},
                onMoveDown = {},
            )
        }
    }
}
