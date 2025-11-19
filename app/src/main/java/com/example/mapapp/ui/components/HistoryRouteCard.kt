package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.navigation.compose.rememberNavController
import com.example.mapapp.R
import com.example.mapapp.data.database.routes.RouteEntity

@Composable
fun HistoryRouteCard(route: RouteEntity) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.route_icon),
                contentDescription = "Route Icon",
                modifier = Modifier.size(50.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = route.title, style = MaterialTheme.typography.titleLarge,
                )

                Text(text = "Completed ${
                    java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
                        .format(java.util.Date(route.timestamp))}",
                    style = MaterialTheme.typography.bodySmall)
            }

            Icon(
                painterResource(R.drawable.arrow_icon),
                contentDescription = "Arrow right",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}