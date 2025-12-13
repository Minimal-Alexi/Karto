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

<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/e875c3ae-1fe5-4e83-add7-ee47c6357f4f" />

## Key Features

 * Interactive map with pins & route lines
<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/0fae5403-12ab-463a-a2f4-5aa5ad50b4c7" />

 * Route creation by category & distance
<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/abe54705-3743-4119-970f-6fda9ee4393c" />

 * Route optimization (manual or auto)
<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/d62da9bd-1942-45be-8319-bc7edbb12a37" />

 * Travel process tracking and arrival notifications
<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/98f33cc5-e911-4cfc-b286-d12c44b500cb" />

 * Save routes & view history
<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/31709c65-4d3d-4372-b024-90a5ce326965" />
<img height="500" alt="Copilot_20251211_144208" src="https://github.com/user-attachments/assets/c0f88877-e885-4c5e-a657-a15bc03ff516" />

## User Handbook
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

## How to setup
### 1. If you are using Android Studio
1. Clone the git repository
2. Add a google maps API key into the .properties.template file. Remove .template from the file name.
3. Done, happy travelling!
### 2. How to applu a Google map API key
1. Go to the Google Cloud Console at https://console.cloud.google.com/. You may need to log in with your Google account if you‘re not already.
2. Select or create a new Google Cloud project to associate your API key with. If creating a new project, give it a name and ID. Wait a few seconds for the project to be created.
3. Make sure your new project is selected in the top dropdown menu, then open the navigation sidebar and go to “APIs & Services” > “Credentials”.
4. On the Credentials page, click “+ Create Credentials” and choose “API key” from the dropdown.
5. Your new API key will be displayed. Click “Close” to return to the Credentials list. You should see your key listed under “API Keys”.
6. (Optional) Click “Edit API key” to set up restrictions. You can give it a name, choose which websites or IP addresses can use it, and set an expiration date. Recommended for production use.
7. Copy your API key to use in your application code. Keep it private and never commit it to version control!
### 3. If you are using an android device
1. Go to (placeholder) this page to download the app.
2. Install the app to yout phone
3. Done, happy travelling! 

## Technical Stack Overview
| Module / Library  | Purpose  | Used Place  | Descriptions |
|:----:|:----:|------| ------| 
| Google Map Compose  | Map displacement  | ExploreScreen, RouteScreen  |  |
| Google route Api  | Fetch route info  | RouteApiService  |  |
| Google Map route matrix Api  |  |  |  |
| Location and Context  |  |  |  |
| Places  |  |  |  |
| Retrofit  |  |  |  |
| OkHttp3  |  |  |  |
| Junit  |  |  |  |
| Androidx room  |  |  |  |
| Google geo coding  |  |  |  |

## Project Architecture
<img width="1001" height="642" alt="karto drawio" src="https://github.com/user-attachments/assets/c825d2df-f7c5-4c73-bc9e-f88d7655adb6" />
