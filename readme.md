<h1 align="center">
  <br>
  <img src="./docs/icon.webp" alt="Markdown" width="200">
  <br>
  Karto
  <br>
</h1>

<h4 align="center">Your personal travel guide</h4>

<p align="center">
  <a href="https://img.shields.io/badge/Kotlin-2.0.20-green.svg">
    <img src="https://img.shields.io/badge/Kotlin-2.0.20-green.svg"
         alt="Kotlin">
  </a>
  <a href="https://img.shields.io/badge/Compose-1.7.0-green.svg"><img src="https://img.shields.io/badge/Compose-1.7.0-green.svg"></a>
  <a href="https://saythanks.io/to/bullredeyes@gmail.com">
      <img src="https://img.shields.io/badge/SayThanks.io-%E2%98%BC-1EAEDB.svg">
  </a>
  <a href="https://www.paypal.me/AmitMerchant">
    <img src="https://img.shields.io/badge/$-donate-ff69b4.svg?maxAge=2592000&amp;style=flat">
  </a>
</p>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#download">Download</a> •
  <a href="#credits">Credits</a> •
  <a href="#related">Related</a> •
  <a href="#license">License</a>
</p>


# Karto
Karto is a personal travel guide app designed for travelers exploring a location, whether near or abroad.

It helps users discover points of interest, plan routes, and explore efficiently based on travel time, distance, and category preferences.

Users can create, save, modify, and track routes, access reviews, mark visited locations, and get AI-powered descriptions for a richer travel experience.

## How to setup
1. Clone the git repository
2. Add a google maps API key into the .properties.template file. Remove .template from the file name.
3. Done, happy travelling!

## Key Features
 * Seach near by places
<img src="./docs/Screenshot_20251208_190725.png" height="600" alt="img1">
 * Add locations and manage routes

## How to use
1. Choose the type of places you want to visit, then click “Start Route” 
2. In the route screen, you can see the locations you want to visit on the map. 
3. Choose more places using the “select nearby locations” button, while changing the distance limit using the distance bar. 
4. Change the sequence of places you want to visit; you can see the distance and duration of each trip between waypoints as well as the sum of travel distance and time. 
5. You can also make an auto generated route by checking the “make route for me” option box. 
6. If you don’t start at once, select “save route” to save your route into the database. You can always find the saved routes on the “Saved” page. 
7. After managing your travel plan, select “start route” to start your trip. 
8. Check the book icon beside the waypoints to see the description of this place. 
9. When you arrive at a place, this app automatically sends a notification to your phone (if you turn on the notification on your phone’s setting) and marks the place as visited. 
10. When the trip is over, click the “Complete Route” button to end your route. The completed routes are in the Route History on the setting page. 
 - How to change the username – go to the “Setting” page, input the new names in the first name and last name file, then click “Update Name” 
 - You can check the nearby popular locations by checking the “Suggestions” under the home page. 


## Technical Stack Overview
| Module / Library  | Purpose  | Used Place  | Descriptions |
|:----:|:----:|------| ------| 
| Google Map Compose  | Map displacement  | ExploreScreen, RouteScreen  | |
| Google route Api  | Fetch route info  | RouteApiService  | |
