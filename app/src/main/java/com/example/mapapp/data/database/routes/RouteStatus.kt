package com.example.mapapp.data.database.routes

enum class RouteStatus {
    // should only be used as a placeholder,
    // and thus should only have 1 Usage at all times -
    // the DAO functions replace the status with what's applicable
    DEFAULT,

    // used in Saved -screen
    SAVED,

    // used in Route History
    COMPLETED,

    // used when user is currently on the route (pressed "start route")
    // only one exists at all times
    CURRENT
}
