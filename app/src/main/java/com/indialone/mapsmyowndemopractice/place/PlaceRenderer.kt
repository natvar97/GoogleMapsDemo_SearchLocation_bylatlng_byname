package com.indialone.mapsmyowndemopractice.place

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ktx.model.markerOptions
import com.indialone.mapsmyowndemopractice.BitmapHelper
import com.indialone.mapsmyowndemopractice.R

class PlaceRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<Place>
) : DefaultClusterRenderer<Place>(context, map, clusterManager) {


    val homeIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(
            context,
            R.color.teal_700
        )

        BitmapHelper.vectorToBitmap(
            context,
            R.drawable.icon_home,
            color
        )
    }

    override fun onBeforeClusterItemRendered(
        item: Place,
        markerOptions: MarkerOptions
    ) {
        markerOptions {
            title(item.name)
            position(item.latLng)
            icon(homeIcon)
        }
    }

    override fun onClusterItemRendered(clusterItem: Place, marker: Marker) {
        marker.tag = clusterItem
    }
}