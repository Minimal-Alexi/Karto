package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mapapp.data.model.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedPlaceInfoCard(selectedPlace: Place?){
    val sheetState = rememberModalBottomSheetState()
    var selectedPlace by remember { mutableStateOf<Place?>(selectedPlace) }
    if(selectedPlace != null){
        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null },
            sheetState = sheetState
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(selectedPlace!!.displayName.text)
                Spacer(Modifier.height(8.dp))

                Button(onClick = { /* action */ }) {
                    Text("Add to route")
                }

                Button(onClick = { /* action */ }) {
                    Text("Navigate")
                }
            }
        }
    }
}