package com.example.dam_a15044coolweatherapp

data class WeatherData (
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current_weather: CurrentWeather,
    val hourly: Hourly
)

data class CurrentWeather (
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Int,
    val weathercode: Int,
    val time: String
)

data class Hourly (
    val time: ArrayList<String>,
    val temperature_2m: ArrayList<Double>,
    val weathercode: ArrayList<Int>,
    val pressure_msl: ArrayList<Double>,
    val windspeed_10m: ArrayList<Double>
)

enum class WMOWeatherCode(val code: Int, val image: String) {
    CLEAR_SKY(0, "ic_sun"),
    MAINLY_CLEAR(1, "ic_cloud"),
    PARTLY_CLOUDY(2, "ic_cloud"),
    OVERCAST(3, "ic_cloud"),
    FOG(45, "ic_fog"),
    DEPOSITING_RIME_FOG(48, "ic_fog"),
    DRIZZLE_LIGHT(51, "ic_rain"),
    DRIZZLE_MODERATE(53, "ic_rain"),
    DRIZZLE_DENSE(55, "ic_rain"),
    FREEZING_DRIZZLE_LIGHT(56, "ic_rain"),
    FREEZING_DRIZZLE_DENSE(57, "ic_rain"),
    RAIN_SLIGHT(61, "ic_rain"),
    RAIN_MODERATE(63, "ic_rain"),
    RAIN_HEAVY(65, "ic_rain"),
    FREEZING_RAIN_LIGHT(66, "ic_rain"),
    FREEZING_RAIN_HEAVY(67, "ic_rain"),
    SNOW_FALL_SLIGHT(71, "ic_cloud"),
    SNOW_FALL_MODERATE(73, "ic_cloud"),
    SNOW_FALL_HEAVY(75, "ic_cloud"),
    SNOW_GRAINS(77, "ic_cloud"),
    RAIN_SHOWERS_SLIGHT(80, "ic_rain"),
    RAIN_SHOWERS_MODERATE(81, "ic_rain"),
    RAIN_SHOWERS_VIOLENT(82, "ic_rain"),
    SNOW_SHOWERS_SLIGHT(85, "ic_cloud"),
    SNOW_SHOWERS_HEAVY(86, "ic_cloud"),
    THUNDERSTORM_SLIGHT_MODERATE(95, "ic_storm"),
    THUNDERSTORM_HAIL_SLIGHT(96, "ic_storm"),
    THUNDERSTORM_HAIL_HEAVY(99, "ic_storm")
}

fun getWeatherCodeMap(): Map<Int, WMOWeatherCode> {
    val weatherMap = HashMap<Int, WMOWeatherCode>()
    WMOWeatherCode.values().forEach {
        weatherMap[it.code] = it
    }
    return weatherMap
}