package com.detab.detabapp.Providers;

import com.detab.detabapp.Models.TRLPothole;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by thiago on 02/05/2017.
 */

public class PotholeRenderProvider
{
    private GoogleMap _map;

    public void SetMapProvider(GoogleMap map)
    {
        _map = map;
    }

    public void Render(List<TRLPothole> potholes)
    {
        Render(potholes, false);
    }

    public void Render(List<TRLPothole> potholes, boolean firstRender)
    {
        BitmapDescriptor markerType = firstRender
                ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

        for (TRLPothole item : potholes)
        {
            if (item.isPinned)
            {
                item.Marker.setPosition(new LatLng(item.Lat, item.Lng));
                item.Marker.setTitle("Pothole " + item.GetDistance());
            } else
            {
                item.Marker = _map.addMarker(new MarkerOptions()
                        .position(new LatLng(item.Lat, item.Lng))
                        .icon(markerType)
                        .title("Pothole " + item.GetDistance()));
                item.isPinned = true;
            }
        }
    }
}
