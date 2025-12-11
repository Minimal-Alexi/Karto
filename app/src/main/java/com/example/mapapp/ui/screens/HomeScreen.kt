package com.example.mapapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mapapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.TypesOfPlaces
import com.example.mapapp.ui.components.DistanceSlider
import com.example.mapapp.ui.components.PlaceTypeSelector
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.route.StartingLocationSelector
import com.example.mapapp.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.debounce

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel(),generateRoute:(TypesOfPlaces, Double, LatLng) -> Unit,
               navigateToLocationScreen: (String) -> Unit) {

    val greeting = homeViewModel.firstName.collectAsState().value
    val location = homeViewModel.greetingLocation.collectAsState().value
    val userCoordinates = homeViewModel.userLocation.collectAsState().value
    val distanceToPlaces = homeViewModel.distanceToPlaces.collectAsState().value
    val typeOfPlaceToVisit = homeViewModel.placeTypeSelector.collectAsState().value
    val suggestionCardNumbers = homeViewModel.suggestionCardNumber.collectAsState().value
    val suggestionsIdMap = homeViewModel.suggestionRecommendations.collectAsState().value
    val customLocation = homeViewModel.customLocation.collectAsState().value
    LaunchedEffect(location, userCoordinates){
        homeViewModel.getReverseGeocodedLocation()
    }
    LaunchedEffect(Unit) {
        snapshotFlow { userCoordinates }
            .debounce(3000)
            .collect { coords ->
                Log.d(null,"API CALL X3")
                homeViewModel.getNumberOfNearbySuggestions(TypesOfPlaces.BEACHES)
                homeViewModel.getNumberOfNearbySuggestions(TypesOfPlaces.NATURAL_FEATURES)
                homeViewModel.getNumberOfNearbySuggestions(TypesOfPlaces.RESTAURANTS)
            }
    }

    LaunchedEffect(greeting) {
        homeViewModel.getName()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp)
        )
        CurrentLocationWidget(location)
        MakeYourRouteCard(typeOfPlaceToVisit,
            distanceToPlaces,
            homeViewModel::changeDistanceToPlaces,
            homeViewModel::changePlaceType,
            homeViewModel::nullCustomLocation,
            homeViewModel::setOriginLocation,
            homeViewModel::setCustomLocationText,
            generateRoute,
            typeOfPlaceToVisit,
            /* TODO:Extremely unsafe if the user clicks the button before initialization. But this will do.*/
            customLocation?:userCoordinates?: LatLng(0.0,0.0))
        SuggestionsSection(suggestionCardNumbers,suggestionsIdMap,navigateToLocationScreen,location)
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
fun CurrentLocationWidget(location:String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF42A5F5))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Your current location:",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location icon",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )

                    Text(
                        text = location,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Let’s explore it together!",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Image(
                painter = painterResource(id = R.drawable.helsinki),
                contentDescription = "Helsinki",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSecondary, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun MakeYourRouteCard(
    currentTypesOfPlace: TypesOfPlaces,
    distanceToPlaces: Double,
    distanceSliderOnChange: (Double) -> Unit,
    onDropdownMenuChange: (TypesOfPlaces) -> Unit,
    nullifyCustomLocation: () -> Unit,
    setOriginLocation: (LatLng) -> Unit,
    setCustomLocationText: (String) -> Unit,
    generateRoute:(TypesOfPlaces, Double, LatLng) -> Unit,
    typesOfPlaces: TypesOfPlaces,
    customLocation: LatLng
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Make Your Route",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            PlaceTypeSelector(currentTypesOfPlace,onDropdownMenuChange)
            Spacer(modifier = Modifier.height(1.dp))

            StartingLocationSelector(nullifyCustomLocation,setOriginLocation, setCustomLocationText)
            Spacer(modifier = Modifier.height(2.dp))


            DistanceSlider(
                distanceValue = distanceToPlaces,
                onDistanceChange = {distanceSliderOnChange(it)}
            )

            PrimaryButton(
                text = "Make A Route",
                backgroundColor = MaterialTheme.colorScheme.secondary
            ) {
                generateRoute(typesOfPlaces,distanceToPlaces,customLocation)
            }
        }
    }
}

@Composable
fun SuggestionsSection(suggestionsNumbersMap: HashMap<TypesOfPlaces,Int>,
                       suggestionsIdMap: HashMap<TypesOfPlaces,String>,
                       navigateToLocationScreen: (String) -> Unit,
                       location:String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Suggestions",
            style = MaterialTheme.typography.titleLarge
        )

        SuggestionItem(
            icon = Icons.Default.Nature,
            title = "Nature Locations",
            subtitle = "${suggestionsNumbersMap[TypesOfPlaces.NATURAL_FEATURES]} locations found in $location",
            placeId = suggestionsIdMap[TypesOfPlaces.NATURAL_FEATURES]!!,
            navigateToLocationScreen = navigateToLocationScreen,
        )

        SuggestionItem(
            icon = Icons.Default.BeachAccess,
            title = "Beaches",
            subtitle = "${suggestionsNumbersMap[TypesOfPlaces.BEACHES]} locations found in $location",
            placeId = suggestionsIdMap[TypesOfPlaces.BEACHES]!!,
            navigateToLocationScreen = navigateToLocationScreen,
        )

        SuggestionItem(
            icon = Icons.Default.LocalBar,
            title = "Bars",
            subtitle = "${suggestionsNumbersMap[TypesOfPlaces.RESTAURANTS]} locations found in $location",
            placeId = suggestionsIdMap[TypesOfPlaces.RESTAURANTS]!!,
            navigateToLocationScreen = navigateToLocationScreen,
        )
    }
}

@Composable
fun SuggestionItem(icon: ImageVector,
                   title: String,
                   subtitle: String,
                   placeId:String,
                   navigateToLocationScreen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .clickable{navigateToLocationScreen(placeId)},
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}