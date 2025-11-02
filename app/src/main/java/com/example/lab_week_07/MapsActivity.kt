package com.example.lab_week_07

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.lab_week_07.databinding.ActivityMapsBinding
import android.util.Log
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //This is the variable through which we will launch the permission request and track user responses
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //This is used to register for activity result
        //The activity result will be used to handle the permission request to the user
        //It accepts an ActivityResultContract as a parameter
        //which in this case we're using the RequestPermission() ActivityResultContract
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    //If granted by the user, execute the necessary function
                    getLastLocation()
                } else {
                    //If not granted, show a rationale dialog
                    //A rationale dialog is used for a warning to the user that the app will now work without the required permission
                    showPermissionRationale {
                        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    }
                }
            }
    }

    //This is used to check if the user already has the permission granted
    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    //This is used to bring up a rationale dialog which will be used to ask the user for permission again
    //A rationale dialog is used for a warning to the user that the app will now work without the required permission
    //Usually it's brought up when the user denies the needed permission in the previous permission request
    private fun showPermissionRationale(positiveAction: () -> Unit) {
        //Create a pop up alert dialog that's used to ask for the required permission again to the user
        AlertDialog.Builder(this)
            .setTitle("Location permission")
            .setMessage("This app will not work without knowing your current location")
            .setPositiveButton(android.R.string.ok) { _, _ -> positiveAction() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called.")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //OnMapReady is called when the map is ready to be used
        //The code below is used to check for the location permission for the map functionality to work
        //If it's not granted yet, then the rationale dialog will be brought up
        when {
            hasLocationPermission() -> getLastLocation()
            //shouldShowRequestPermissionRationale automatically checks if the user has denied the permission before
            //If it has, then the rationale dialog will be brought up
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }
            else -> requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }
}

