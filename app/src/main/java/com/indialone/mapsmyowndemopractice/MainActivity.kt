package com.indialone.mapsmyowndemopractice

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.addCircle
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import com.indialone.mapsmyowndemopractice.place.Place
import com.indialone.mapsmyowndemopractice.place.PlaceReader
import com.indialone.mapsmyowndemopractice.place.PlaceRenderer
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView

    private var place: Place? = null

    // map implementation using map ktx library
    private var circle: Circle? = null

    private val places: List<Place> by lazy {
        PlaceReader(this).read()
    }

    private val homeIcon: BitmapDescriptor by lazy {
        val color = resources.getColor(R.color.teal_700)
        BitmapHelper.vectorToBitmap(this, R.drawable.icon_home, color)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        etSearch = findViewById(R.id.et_search)
        btnSearch = findViewById(R.id.btn_search)

        btnSearch.setOnClickListener {
            lifecycleScope.launchWhenCreated {

                val googleMap = mapFragment.awaitMap()
                googleMap.awaitMapLoad()

                googleMap.isBuildingsEnabled = true
                googleMap.setMinZoomPreference(1f)
                googleMap.setMaxZoomPreference(20f)
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.uiSettings.isZoomGesturesEnabled = true
                googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
                googleMap.uiSettings.isCompassEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
                googleMap.setOnMyLocationClickListener {
                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        )
                    ).title = "Your Location"
                }

                val bound = LatLngBounds.builder()
//                places.forEach { place ->
//                    bound.include(place.latLng)
//                }

                val searchText: String? = etSearch.text.toString()
                var addresses: List<Address>? = null

                if (searchText != null || searchText != "") {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        addresses = geocoder.getFromLocationName(searchText, 1)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val address = addresses!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    bound.include(latLng)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 20))

                    googleMap.addMarker {
                        title("${address.featureName}, ${address.subAdminArea}, ${address.countryName}, ${address.postalCode}")
                        position(latLng)
                    }
                }
            }
        }


    }

    private fun addMarker(googleMap: GoogleMap) {
        val marker = googleMap.addMarker {
            title(place!!.address)
            position(place!!.latLng)
        }
        marker.tag = place
    }

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker {
                title(place.name)
                position(place.latLng)
                icon(homeIcon)
            }
            marker.tag = place
        }
    }

    private fun addClutteredMarkers(googleMap: GoogleMap) {
        val clusterManager = ClusterManager<Place>(this, googleMap)

        clusterManager.renderer = PlaceRenderer(
            this,
            googleMap,
            clusterManager
        )

//        clusterManager.addItems(places)

        clusterManager.addItem(place!!)

        clusterManager.cluster()

        googleMap.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
        }


        clusterManager.setOnClusterItemClickListener { place ->
            addCircle(googleMap, place)
            return@setOnClusterItemClickListener false
        }

    }

    private fun addCircle(googleMap: GoogleMap, place: Place) {
        circle?.remove()
        circle = googleMap.addCircle {
            center(place.latLng)
            radius(10.0)
            fillColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            strokeColor(ContextCompat.getColor(this@MainActivity, R.color.teal_700))
        }
    }


}

/*
    * if you want to search location by longitude and latitude then use below code
    *


        etLat = findViewById(R.id.et_lat)
        etLng = findViewById(R.id.et_lng)
        btnSearch = findViewById(R.id.btn_search)

        btnSearch.setOnClickListener {
            lifecycleScope.launchWhenCreated {

                val googleMap = mapFragment.awaitMap()
                googleMap.awaitMapLoad()

                googleMap.isBuildingsEnabled = true
                googleMap.setMinZoomPreference(1f)
                googleMap.setMaxZoomPreference(20f)
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.uiSettings.isZoomGesturesEnabled = true
                googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
                googleMap.uiSettings.isCompassEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
                googleMap.setOnMyLocationClickListener {
                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        )
                    ).title = "Your Location"
                }

                val bound = LatLngBounds.builder()
//                places.forEach { place ->
//                    bound.include(place.latLng)
//                }

                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(
                    etLat.text.toString().toDouble(),
                    etLng.text.toString().toDouble(),
                    1
                )

                val address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName = addresses[0].featureName

                val lat = etLat.text.toString().toDouble()
                val lng = etLng.text.toString().toDouble()
                val latLng = LatLng(lat, lng)

                place = Place(knownName, latLng, address, 0f)
                Log.e("place details", "$place")

                bound.include(place!!.latLng)

//                googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this@MainActivity))

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 20))
                addMarker(googleMap)

                addClutteredMarkers(googleMap)

            }
        }


 */