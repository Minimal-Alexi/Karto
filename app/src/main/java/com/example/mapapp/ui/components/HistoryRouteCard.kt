package com.example.mapapp.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mapapp.R
import com.example.mapapp.data.database.routes.RouteWithStopCount
import com.example.mapapp.ui.components.buttons.DeleteIconButton

@Composable
fun HistoryRouteCard(
    route: RouteWithStopCount, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.route_icon),
                contentDescription = "Route Icon",
                modifier = Modifier
                    .padding(8.dp)
                    .size(42.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = route.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.basicMarquee()
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "Started ${
                        java.text.SimpleDateFormat(
                            "dd MMM yyyy, HH:mm", java.util.Locale.getDefault()
                        ).format(java.util.Date(route.startedAt))
                    }", style = MaterialTheme.typography.bodyMedium
                )
                if (route.completedAt != null) {
                    Text(
                        text = "Completed ${
                            java.text.SimpleDateFormat(
                                "dd MMM yyyy, HH:mm", java.util.Locale.getDefault()
                            ).format(java.util.Date(route.completedAt))
                        }", style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Not completed yet", style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(text = "${route.stopsCount} locations", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))

            DeleteIconButton(
                onClick = { onDelete() }, modifier = Modifier.size(42.dp)
            )
        }
    }
}