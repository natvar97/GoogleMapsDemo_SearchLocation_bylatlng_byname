package com.indialone.mapsmyowndemopractice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.indialone.mapsmyowndemopractice.place.Place

class MarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker?): View? {
        val place = marker?.tag as? Place ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.marker_information_content, null)

        val name = view.findViewById<TextView>(R.id.text_view_title)
        name.text = place.name

        val address = view.findViewById<TextView>(R.id.text_view_address)
        address.text = place.address

        val rating = view.findViewById<TextView>(R.id.text_view_rating)
        rating.text = "Rating: ${place.rating}"

        return view
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}