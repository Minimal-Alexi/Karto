package com.example.mapapp.data.model

import com.google.android.gms.maps.model.LatLng

enum class TypesOfPlaces(val displayName: String, val places: List<String>) {

    RESTAURANTS("Restaurants", listOf(
        "restaurant", "cafe", "bar", "pub", "bakery", "coffee_shop",
        "food_court", "meal_takeaway", "meal_delivery", "fast_food_restaurant",
        "fine_dining_restaurant", "pizza_restaurant", "seafood_restaurant",
        "sushi_restaurant", "steak_house", "breakfast_restaurant",
        "brunch_restaurant", "vegan_restaurant", "vegetarian_restaurant",
        "thai_restaurant", "japanese_restaurant", "italian_restaurant",
        "french_restaurant", "mexican_restaurant", "indian_restaurant",
        "greek_restaurant", "turkish_restaurant", "barbecue_restaurant",
        "buffet_restaurant", "dessert_restaurant", "ice_cream_shop",
        "wine_bar", "tea_house"
    )),

    BEACHES("Beaches",listOf(
        "beach"
    )),

    NATURAL_FEATURES("Natural Features", listOf(
        "beach", "park", "national_park", "state_park",
        "garden", "botanical_garden", "hiking_area",
        "wildlife_park", "wildlife_refuge", "campground"
    )),

    LANDMARKS("Landmarks & Monuments", listOf(
        "tourist_attraction", "historical_landmark", "monument",
        "museum", "art_gallery", "cultural_landmark",
        "historical_place", "sculpture", "plaza",
        "observation_deck", "amphitheatre"
    )),

    STORES("Stores", listOf(
        "store", "shopping_mall", "gift_shop",
        "convenience_store", "supermarket", "grocery_store",
        "clothing_store", "shoe_store", "jewelry_store",
        "book_store", "electronics_store", "hardware_store", "pet_store",
        "sporting_goods_store", "market", "liquor_store"
    ));
}

data class PlacesRequest(
    val maxResultCount:Int = 20,
    val includedTypes:List <String>,
    val locationRestriction: LocationRestriction
)

data class PlacesResponse(
    val places:List<Place>
)

data class Place(
    val displayName:DisplayName,
    val location: LatLng
)

data class DisplayName(
    val text:String
)

data class LocationRestriction(
    val circle:Circle
)
data class Circle(
    val center: LatLng,
    val radius:Double = 1000.0
)