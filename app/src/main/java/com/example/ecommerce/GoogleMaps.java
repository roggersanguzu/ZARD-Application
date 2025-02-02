package com.example.ecommerce;




import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private AutocompleteSupportFragment autocompleteFragment;
    String placeName;
    Button saveLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);


        // Initialize Places API
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));


        // Initialize MapView and get Map instance
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        saveLocation = findViewById(R.id.btnlocation);
saveLocation.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
//        Intent intent = new Intent(GoogleMaps.this, MainShoppingCart.class);
//        intent.putExtra("location", placeName);
//        Toast.makeText(GoogleMaps.this, "Place saved Successfully", Toast.LENGTH_SHORT).show();
//        startActivity(intent);
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Selected_place",placeName);
        editor.apply();
        Toast.makeText(GoogleMaps.this, "Place saved Successfully", Toast.LENGTH_SHORT).show();

    }
});

        // Initialize FusedLocationProviderClient for location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Set up the Autocomplete Support Fragment
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);



        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // When a place is selected from the search
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


                                placeName = place.getName();



                        }

                    }



                @Override
                public void onError(@NonNull Status status) {
                    // Handle errors
                    Toast.makeText(GoogleMaps.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        // Check and request location permissions
        checkLocationPermission();
    }


    // Check and request location permissions (request if not granted)
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, fetch the location
            getCurrentLocation();
        }
    }


    // Get current location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));


                        // Set the current location in the search bar using place name
                        String address = getPlaceNameFromLatLng(currentLocation.latitude, currentLocation.longitude);
                        autocompleteFragment.setText(address);  // Set the text to the place name
                    } else {
                        Toast.makeText(GoogleMaps.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Get the place name (address) from latitude and longitude using Geocoder
    private String getPlaceNameFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Returning the full address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";  // Default if unable to find address
    }


    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if location permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();  // Permission granted, fetch the location
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Enable location tracking on the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }


        // Add click listener on the map to set a marker and update the search bar with the place name
        googleMap.setOnMapClickListener(latLng -> {
            // Add a marker where the user clicked
            googleMap.clear();  // Clear existing markers
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


            // Update the search bar with the selected location (place name)
            String address = getPlaceNameFromLatLng(latLng.latitude, latLng.longitude);
            autocompleteFragment.setText(address);  // Set the text to the place name
        });
    }


    // Lifecycle methods to handle MapView's lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}








