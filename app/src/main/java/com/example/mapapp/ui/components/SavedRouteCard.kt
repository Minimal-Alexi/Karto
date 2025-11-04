package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
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

// this should later take in the actual route as parameter though... just styling for now
@Composable
fun SavedRouteCard(route: String) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
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
                    text = route, style = MaterialTheme.typography.titleLarge,
                )
                Text(text = "Completed 23 October 2023", style = MaterialTheme.typography.bodyMedium)
                Text(text = "4 locations", style = MaterialTheme.typography.bodyMedium)
                Text(text = "15 km", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                painterResource(R.drawable.arrow_icon),
                contentDescription = "Arrow right",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}