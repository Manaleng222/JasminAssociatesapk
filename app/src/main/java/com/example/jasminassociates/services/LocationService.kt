package com.example.jasminassociates.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import javax.inject.Inject

class LocationService @Inject constructor(
    private val context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)



    // Fix the getCurrentLocation function:
    suspend fun getCurrentLocation(): LocationResult? {
        if (!hasLocationPermissions()) {
            return null
        }

        return try {
            val task = fusedLocationClient.lastLocation
            val location: Location? = Tasks.await(task) // Use Tasks.await instead of await()
            location?.let {
                val address = getAddressFromLocation(it.latitude, it.longitude)
                LocationResult(
                    latitude = it.latitude.toString(),
                    longitude = it.longitude.toString(),
                    address = address
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCurrentLocationWithRetry(maxRetries: Int = 3): LocationResult? {
        if (!hasLocationPermissions()) {
            return null
        }

        var retryCount = 0
        while (retryCount < maxRetries) {
            try {
                val location = getCurrentLocation()
                if (location != null) {
                    return location
                }
            } catch (e: Exception) {
                // Log error if needed
            }
            retryCount++
            // Wait before retry (you might want to add delay here)
        }
        return null
    }

    fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        return try {
            // Using Android's Geocoder for reverse geocoding
            val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressParts = mutableListOf<String>()

                address.thoroughfare?.let { addressParts.add(it) }
                address.locality?.let { addressParts.add(it) }
                address.adminArea?.let { addressParts.add(it) }
                address.postalCode?.let { addressParts.add(it) }
                address.countryName?.let { addressParts.add(it) }

                if (addressParts.isNotEmpty()) {
                    addressParts.joinToString(", ")
                } else {
                    "Lat: $latitude, Long: $longitude"
                }
            } else {
                "Lat: $latitude, Long: $longitude"
            }
        } catch (e: Exception) {
            // Fallback to coordinates if geocoding fails
            "Lat: $latitude, Long: $longitude"
        }
    }

    suspend fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] // Distance in meters
    }

    suspend fun isWithinRadius(
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Float
    ): Boolean {
        val currentLocation = getCurrentLocation()
        return if (currentLocation != null) {
            val distance = calculateDistance(
                currentLocation.latitude.toDouble(),
                currentLocation.longitude.toDouble(),
                targetLat,
                targetLon
            )
            distance <= radiusMeters
        } else {
            false
        }
    }

    fun formatLocationForDisplay(latitude: String, longitude: String): String {
        return try {
            val lat = latitude.toDouble()
            val lon = longitude.toDouble()
            "Lat: ${"%.6f".format(lat)}, Long: ${"%.6f".format(lon)}"
        } catch (e: Exception) {
            "Lat: $latitude, Long: $longitude"
        }
    }

    data class LocationResult(
        val latitude: String,
        val longitude: String,
        val address: String
    ) {
        fun toLatLng(): Pair<Double, Double>? {
            return try {
                Pair(latitude.toDouble(), longitude.toDouble())
            } catch (e: Exception) {
                null
            }
        }

        fun getFormattedCoordinates(): String {
            return "Lat: $latitude, Long: $longitude"
        }
    }
}