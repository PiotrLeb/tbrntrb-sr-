package me.piotrleb.myapplication


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RunViewModel : ViewModel() {
    private val _distance = MutableStateFlow(0.0) // w metrach
    val distance: StateFlow<Double> = _distance.asStateFlow()

    private val _pace = MutableStateFlow(0.0) // minuty na kilometr
    val pace: StateFlow<Double> = _pace.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var startTime = 0L

    private val _pathPoints = mutableStateListOf<LatLng>()
    val pathPoints: List<LatLng> = _pathPoints

    fun toggleRun(context: Context) {
        if (_isRunning.value) {
            stopRun()
        } else {
            startRun(context)
        }
    }

    @SuppressLint("MissingPermission") // Uprawnienia sprawdzamy w UI
    private fun startRun(context: Context) {
        _isRunning.value = true
        _distance.value = 0.0
        _pace.value = 0.0
        lastLocation = null
        startTime = System.currentTimeMillis()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .build()

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopRun() {
        _isRunning.value = false
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (lastLocation != null) {
                    val distanceGained = lastLocation!!.distanceTo(location)
                    _distance.value += distanceGained

                    // Obliczanie tempa (min/km)
                    val timeElapsedMin = (System.currentTimeMillis() - startTime) / 60000.0
                    val distanceKm = _distance.value / 1000.0
                    if (distanceKm > 0.01) { // żeby uniknąć dzielenia przez zero i kosmicznych wyników
                        _pace.value = timeElapsedMin / distanceKm
                    }
                }
                lastLocation = location
            }
        }
    }
}