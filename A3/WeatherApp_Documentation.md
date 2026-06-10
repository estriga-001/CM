# Weather App Project - Full Documentation

Welcome to the documentation for the **Cool Weather App**. This guide is written for beginners to Android development. We will explain how the app works, what Jetpack Compose is, and the general architecture of modern Android apps using Kotlin.

## 1. What is an Android App built with Kotlin?
Android apps are traditionally built using Java, but nowadays, **Kotlin** is the standard. It provides a more modern, concise, and safer way to write Android applications.

### Key Concepts in Modern Android:
- **`MainActivity.kt`**: This is the entry point of an Android application with a user interface. When you open the app on your phone, the Android system looks for this file and runs the code inside `onCreate()`.
- **`AndroidManifest.xml`**: This is the "ID card" of the app. It tells the Android system everything it needs to know about the app: its name, its icon, what permissions it needs (like accessing the Internet or Location), and which Activity to launch first.
- **Gradle (`build.gradle.kts`)**: This is the build system. It manages dependencies (external libraries the app needs to work, like Maps or network clients) and compiles the code into an APK that your phone can install.

---

## 2. What are Composables? (Jetpack Compose)
In the past, Android UI was built using XML files to define buttons and text, and Java/Kotlin code to control them. This was complicated. 

**Jetpack Compose** is the modern way to build UI in Android. It is a *declarative* framework. This means you just describe what the UI should look like in Kotlin code, and the system handles drawing it.

- **`@Composable`**: This annotation tells the system that a function is not a normal function, but a UI component. 
- **State**: In Compose, UI is driven by "State". When the data (state) changes, Compose automatically redraws (recomposes) the parts of the screen that use that data.

### Example of a Composable:
```kotlin
@Composable
fun WeatherRow(label: String, value: String) {
    Row {
        Text(text = label)
        Text(text = value)
    }
}
```
This simply creates a row with two pieces of text. You can reuse this `WeatherRow` anywhere in your app!

---

## 3. Project Architecture (MVVM)
This app uses a pattern similar to **Model-View-ViewModel (MVVM)**, which is the recommended architecture for Android.

1. **Model (Data Layer)**: This is where the app gets its data. In this app, the data comes from the internet (Open-Meteo API).
2. **View (UI Layer)**: This is the visual part of the app (the `@Composable` functions). It shouldn't contain complex logic.
3. **ViewModel**: This acts as a bridge between the Data and the View. It holds the data (State) for the View and survives screen rotations (unlike regular Activities).

---

## 4. Deep Dive into the Code

### 4.1 The Data Layer
**`data/WeatherData.kt`**
This file contains the "Data Classes". Data classes in Kotlin are simple classes used just to hold data. The `@Serializable` annotation tells the system that this data will be converted to and from JSON (the format the internet API sends data in).
- It defines what a `WeatherData`, `CurrentWeather`, and `Hourly` forecast looks like.
- It also has an `enum class WMOWeatherCode` which maps weather codes from the API (like 0 for clear sky, 61 for rain) to icons in the app.

**`data/WeatherApiClient.kt`**
This file is responsible for talking to the internet. 
- It uses a library called **Ktor** to make network requests.
- The `getWeather(lat, lon)` function connects to `https://api.open-meteo.com/` and asks for the weather at the given latitude and longitude.

### 4.2 The View Layer (UI / Composables)
All these files are in the `ui` folder and are marked with `@Composable`.

**`WeatherCard.kt`**
- This is a UI component that draws a card on the screen.
- It takes data like `temperature` and `windSpeed` and displays them in a neat format using the `WeatherRow` composable we mentioned earlier.

**`CoordinatesCard.kt`**
- This provides the input fields for the user.
- It contains `OutlinedTextField` composables where the user can type the latitude and longitude, and a `Button` to update the weather.

**`WeatherMapScreen.kt`**
- This is a more complex screen that uses Google Maps.
- It uses the `GoogleMap` composable from the Maps library to display a map and place a marker at the chosen coordinates.

**`WeatherScreen.kt`**
- This is the main screen that glues everything together. It contains the `CoordinatesCard` and the `WeatherCard`.

### 4.3 The App Entry Point
**`MainActivity.kt`**
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Checks if the user gave permission to use Location
        checkLocationPermissions() 

        // 2. Starts drawing the Jetpack Compose UI
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    WeatherUI() // Calls the main composable
                }
            }
        }
    }
}
```

### 4.4 Resources (`res/` folder)
The app doesn't just use code; it uses resources.
- **`drawable/`**: Contains images, icons, and background shapes (like `ic_sun.xml`, `landscape_cyber.jpg`).
- **`values/strings.xml`**: Contains all the text used in the app. This is good practice so the app can be translated to other languages easily.
- **`values/colors.xml`**: Defines the color palette of the app.

## Summary
The **Cool Weather App** is a modern Android application. It uses **Jetpack Compose** to build the UI declaratively, retrieves data from the internet using **Ktor**, and asks the user for permissions to access their location. It separates the "looks" (Composables) from the "data" (ApiClient) for a clean architecture.
