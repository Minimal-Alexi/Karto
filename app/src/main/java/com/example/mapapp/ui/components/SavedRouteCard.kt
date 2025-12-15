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
import com.example.mapapp.data.database.templates.TemplateWithStopCount
import com.example.mapapp.ui.components.buttons.SecondaryButton

@Composable
fun SavedRouteCard(
    template: TemplateWithStopCount,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painterResource(R.drawable.route_icon),
                contentDescription = "Route Icon",
                modifier = Modifier.padding(8.dp).size(42.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = template.title, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.basicMarquee())
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "Saved ${
                        java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(template.savedAt))
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = "${template.stopsCount} locations", style = MaterialTheme.typography.bodyMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    SecondaryButton(
                        text = "Open",
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        {
                            onOpen()
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    SecondaryButton(
                        text = "Delete",
                        backgroundColor = MaterialTheme.colorScheme.error,
                        {
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}